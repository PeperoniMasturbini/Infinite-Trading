package com.natamus.infinitetrading.events;

import com.natamus.infinitetrading.config.ConfigHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class VillagerEvent {
	public static InteractionResult onVillagerClick(Player player, Level level, InteractionHand hand, Entity target, EntityHitResult hitResult) {
		if (level.isClientSide) {
			return InteractionResult.PASS;
		}

		if (target instanceof Villager) {
			if (ConfigHandler.villagerInfiniteTrades) {
				Villager villager = (Villager) target;

				// Если картограф — сбрасываем всё, кроме карт
				if (villager.getVillagerData().getProfession() == VillagerProfession.CARTOGRAPHER) {
					resetMerchantOffersExceptMaps(villager);
				}
				else {
					// Для всех остальных жителей — полный сброс
					resetMerchantOffers(villager);
				}
			}
		}
		else if (target instanceof WanderingTrader) {
			if (ConfigHandler.wanderingTraderInfiniteTrades) {
				WanderingTrader wanderer = (WanderingTrader) target;
				resetMerchantOffers(wanderer);
			}
		}

		return InteractionResult.PASS;
	}

	// Полный сброс (копия логики EntityFunctions.resetMerchantOffers)
	private static void resetMerchantOffers(Merchant merchant) {
		MerchantOffers offers = merchant.getOffers();
		for (MerchantOffer offer : offers) {
			offer.resetUses();
		}
	}

	// Сброс всех предметов, кроме заполненных карт
	private static void resetMerchantOffersExceptMaps(Merchant merchant) {
		MerchantOffers offers = merchant.getOffers();
		for (MerchantOffer offer : offers) {
			if (!offer.getResult().is(Items.FILLED_MAP)) {
				offer.resetUses();
			}
		}
	}
}
