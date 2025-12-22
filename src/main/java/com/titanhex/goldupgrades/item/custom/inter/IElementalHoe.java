package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface IElementalHoe {

    static int getElementalHoeEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ELEMENTAL_HOE_ENCHANTMENT.get(), stack);
    }

    static boolean hasElementalHoeEnchantment(ItemStack stack) {
        return getElementalHoeEnchantmentLevel(stack) > 0;
    }

    @OnlyIn(Dist.CLIENT)
    static void appendHoverText(ItemStack stack, List<ITextComponent> tooltip, String holdingText) {
        if (holdingText != null && !holdingText.isEmpty()) {
            boolean hasElementalHoeEnchantment = hasElementalHoeEnchantment(stack);
            if (hasElementalHoeEnchantment)
                tooltip.add(new StringTextComponent(holdingText));
        }
    }

    static ActionResult<ItemStack> use(ItemStack stack, PlayerEntity player, Effect effect, int durationTicks){
        int elementalHoeLevel = IElementalHoe.getElementalHoeEnchantmentLevel(stack);

        if (elementalHoeLevel > 0) {
            player.addEffect(new EffectInstance(
                    effect,
                    durationTicks,
                    elementalHoeLevel - 1,
                    true,
                    true
            ));

            stack.hurtAndBreak(5*elementalHoeLevel*2, player, (e) -> e.broadcastBreakEvent(Hand.OFF_HAND));

            return ActionResult.consume(stack);
        }

        return ActionResult.pass(stack);
    }

    default void holdingElementalHoe(ItemStack stack, LivingEntity livingEntity, Effect effect, Supplier<Boolean> environmentCheck) {
        int elementalHoeLevel = getElementalHoeEnchantmentLevel(stack);

        if (elementalHoeLevel > 0 && environmentCheck.get()) {
            int duration = 60;
            if (livingEntity.tickCount % duration == 0)
                stack.hurtAndBreak(1, livingEntity,
                        (e) -> e.broadcastBreakEvent(EquipmentSlotType.OFFHAND)
                );
            livingEntity.addEffect(new EffectInstance(
                    effect,
                    duration,
                    elementalHoeLevel-1,
                    false,
                    false
            ));
        }
    }
}
