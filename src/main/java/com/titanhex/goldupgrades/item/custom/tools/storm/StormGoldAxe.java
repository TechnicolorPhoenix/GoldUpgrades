package com.titanhex.goldupgrades.item.custom.tools.storm;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.ModItemTier;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectAxe;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectSword;
import com.titanhex.goldupgrades.item.tool.StormToolItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StormGoldAxe extends EffectAxe implements ILevelableItem, IWeatherInfluencedItem {

    private static final UUID STORM_DAMAGE_MODIFIER = UUID.randomUUID();

    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier of the pickaxe.
     *
     * @param tier                  The base material of the tool.
     * @param attackDamage          The base attack damage of the tool.
     * @param attackSpeed           The attack speed modifier of the tool.
     * @param effectAmplifications  A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration        The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost        The number of durability points to subtract on each use.
     * @param properties            Item properties.
     */
    public StormGoldAxe(IItemTier tier, int attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed+1.333F, effectAmplifications, effectDuration, durabilityCost, properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));
        boolean isThundering = getWeather(stack) == Weather.THUNDERING;

        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            if (isThundering) {
                float damageBonus = 0;

                ItemStack powerItem = new ItemStack(StormToolItems.STORM_POWER_GOLD_AXE.get());
                Multimap<Attribute, AttributeModifier> modifiers = powerItem.getAttributeModifiers(EquipmentSlotType.MAINHAND);

                Attribute attackDamageAttribute = Attributes.ATTACK_DAMAGE;

                for (AttributeModifier modifier : modifiers.get(attackDamageAttribute)) {
                    if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                        damageBonus += (float) modifier.getAmount();
                    }
                }

                float maxDamage = damageBonus - (this.getDamage(stack) + this.getTier().getAttackDamageBonus()) + 1;

                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        STORM_DAMAGE_MODIFIER,
                        "Weapon modifier",
                        maxDamage,
                        AttributeModifier.Operation.ADDITION
                ));
            }
        }

        return builder.build();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int uInt, boolean uBoolean) {
        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);

        if (world.isClientSide)
            return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.MAINHAND) == stack;

        Weather oldWeather = getWeather(stack);
        Weather currentWeather = Weather.getCurrentWeather(world);

        boolean environmentalStateChanged = oldWeather != currentWeather;

        if (environmentalStateChanged) {
            setWeather(stack, currentWeather);

            world.addParticle(
                    ParticleTypes.ITEM_SLIME, // The type of particle
                    holdingEntity.getX() + 0F,   // X coordinate
                    holdingEntity.getY() + 1.0,   // Y coordinate
                    holdingEntity.getZ() + 0F,   // Z coordinate
                    0.05, 0.05, 0.05                // Speed/velocity of the particle
            );
            world.playSound(
                    null,
                    holdingEntity.getX(),
                    holdingEntity.getY(),
                    holdingEntity.getZ(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT,
                    SoundCategory.PLAYERS,
                    0.5F,
                    0.8F
            );
        }

        ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

        if (environmentalStateChanged && isEquipped) {

            if (attackInstance != null) {
                attackInstance.removeModifier(STORM_DAMAGE_MODIFIER);
                Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(EquipmentSlotType.MAINHAND, stack);

                for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
                    ModifiableAttributeInstance instance = livingEntity.getAttribute(entry.getKey());
                    if (instance != null) {
                        if (entry.getValue().getId().equals(STORM_DAMAGE_MODIFIER)) {
                            instance.addTransientModifier(entry.getValue());
                        }
                    }
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        boolean isThundering = getWeather(stack) == Weather.THUNDERING;

        if (isThundering) {
            tooltip.add(new StringTextComponent("§9+30% Harvest Speed"));
            tooltip.add(new StringTextComponent("§eMaxed Harvest Level."));
        }
    }

    @Override
    public int getHarvestLevel(@NotNull ItemStack stack, @NotNull ToolType toolType, @Nullable PlayerEntity player, @Nullable BlockState state) {
        if (getWeather(stack) == Weather.THUNDERING)
            return 5;

        return super.getHarvestLevel(stack, toolType, player, state);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        boolean isThundering = getWeather(stack) == Weather.THUNDERING;

        if (isThundering) {
            if (baseSpeed > 1.0F) {
                return baseSpeed + 0.3F;
            }
        }

        return super.getDestroySpeed(stack, state);
    }
}
