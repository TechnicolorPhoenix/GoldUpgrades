package com.titanhex.goldupgrades.item.custom.tools.effect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import java.util.Map;

/**
 * A specialized Pickaxe that grants multiple defined Potion Effects to the player
 * upon right-click use, incurring a durability cost.
 *
 * The effects, their amplification levels, and the durability cost are all
 * configured via the constructor.
 */
public class EffectShovel extends ShovelItem
{
    // A map to store which Effect applies and what its amplification level should be.
    private final Map<Effect, Integer> effectMap;

    // The duration (in ticks) for which the effects will last.
    private final int effectDuration;

    // The durability cost incurred each time the aura is activated.
    private final int baseDurabilityCost;

    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier of the pickaxe.
     * @param attackDamage The base attack damage of the tool.
     * @param attackSpeed The attack speed modifier of the tool.
     * @param effectAmplifications A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost The number of durability points to subtract on each use.
     * @param properties Item properties.
     */
    public EffectShovel(IItemTier tier, float attackDamage, float attackSpeed,
                        Map<Effect, Integer> effectAmplifications, int effectDuration,
                        int durabilityCost, Properties properties) {

        super(tier, attackDamage, attackSpeed, properties);
        this.effectMap = effectAmplifications;
        this.effectDuration = effectDuration;
        this.baseDurabilityCost = durabilityCost;
    }

    /**
     * Handles the item use event (Right Click) to apply status effects to the player.
     */
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        ItemStack stack = context.getItemInHand();

        // The aura/buff should only apply on the server side and if a player is holding it.
        if (!world.isClientSide && player != null) {

            // 1. Apply effects based on the configuration map
            for (Map.Entry<Effect, Integer> entry : this.effectMap.entrySet()) {
                Effect effect = entry.getKey();
                int amplificationLevel = entry.getValue();

                // Potion amplifications are 0-indexed (Level I = amplifier 0, Level II = amplifier 1).
                int amplifier = Math.max(0, amplificationLevel - 1);

                // Create and apply the effect instance
                player.addEffect(new EffectInstance(
                        effect,
                        this.effectDuration,
                        amplifier,
                        false, // isAmbient
                        true // showsParticles
                ));
            }

            // 2. Damage the tool
            // The durability cost is fixed, as defined in the constructor
            stack.hurtAndBreak(this.baseDurabilityCost, player, (p) -> p.broadcastBreakEvent(context.getHand()));

            // 3. Play a sound to confirm the action (Level Up sound is a good indicator of a buff)
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, 1.0F);

            // Since the action was successful and the item was used, return SUCCESS
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    // Note: The base hitEntity method from PickaxeItem will still apply
    // combat damage. If you need a custom hitEntity, you should override it here.
}
