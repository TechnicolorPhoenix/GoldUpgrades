package com.titanhex.goldupgrades.item.custom.tools.effect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A specialized Pickaxe that grants multiple defined Potion Effects to the player
 * upon right-click use, incurring a durability cost.

 * The effects, their amplification levels, and the durability cost are all
 * configured via the constructor.
 */
public class EffectSword extends SwordItem
{
    // A map to store which Effect applies and what its amplification level should be.
    private final Map<Effect, Integer> effectMap;

    // The duration (in ticks) for which the effects will last.
    private final int effectDuration;

    // The durability cost incurred each time the aura is activated.
    protected final int baseDurabilityCost;

    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier.
     * @param attackDamage The base attack damage.
     * @param attackSpeed The attack speed modifier.
     * @param effectAmplifications A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost The number of durability points to subtract on each use.
     * @param properties Item properties.
     */
    public EffectSword(IItemTier tier, int attackDamage, float attackSpeed,
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
    @NotNull
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide) {

            for (Map.Entry<Effect, Integer> entry : this.effectMap.entrySet()) {
                Effect effect = entry.getKey();
                int amplificationLevel = entry.getValue();

                int amplifier = Math.max(0, amplificationLevel - 1);

                player.addEffect(new EffectInstance(
                        effect,
                        this.effectDuration,
                        amplifier,
                        false,
                        true
                ));
            }

            stack.hurtAndBreak(this.baseDurabilityCost, player, (p) -> p.broadcastBreakEvent(hand));

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, 1.0F);

            return ActionResult.success(stack);
        }

        return ActionResult.pass(stack);
    }
}
