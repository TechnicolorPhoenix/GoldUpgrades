package com.titanhex.goldupgrades.item.custom.tools.effect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A specialized Pickaxe that grants multiple defined Potion Effects to the player
 * upon right-click use, incurring a durability cost.


 * The effects, their amplification levels, and the durability cost are all
 * configured via the constructor.
 */
public class EffectShovel extends ShovelItem
{
    private final Map<Effect, Integer> effectMap;

    private final int effectDuration;

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
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        ItemStack stack = context.getItemInHand();

        if (!world.isClientSide && player != null) {

            for (Map.Entry<Effect, Integer> entry : this.effectMap.entrySet()) {
                Effect effect = entry.getKey();
                int amplificationLevel = entry.getValue();

                int amplifier = Math.max(0, amplificationLevel - 1);

                player.addEffect(new EffectInstance(
                        effect,
                        this.effectDuration,
                        amplifier,
                        false, // isAmbient
                        true // showsParticles
                ));
            }


            stack.hurtAndBreak(this.baseDurabilityCost, player, (p) -> p.broadcastBreakEvent(context.getHand()));

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, 1.0F);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

}
