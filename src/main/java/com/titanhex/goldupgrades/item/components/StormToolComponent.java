package com.titanhex.goldupgrades.item.components;

import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.item.interfaces.IWeatherInfluencedItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.UUID;

public class StormToolComponent {

    public static final UUID STORM_DAMAGE_MODIFIER = UUID.randomUUID();

    public StormToolComponent(){}

    public double getToolDamageBonus(ItemStack stack, Item powerItem){
        TieredItem item = (TieredItem) stack.getItem();
        double damageBonus = (double) IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack) /2;

        ItemStack powerItemStack = new ItemStack(powerItem.getItem());
        Multimap<Attribute, AttributeModifier> modifiers = powerItemStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);

        Attribute attackDamageAttribute = Attributes.ATTACK_DAMAGE;

        for (AttributeModifier modifier : modifiers.get(attackDamageAttribute)) {
            if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                damageBonus += (float) modifier.getAmount();
            }
        }

        return damageBonus - (item.getDamage(stack) + item.getTier().getAttackDamageBonus()) + 1;
    }

    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip){
        IWeatherInfluencedItem weatherItem = (IWeatherInfluencedItem) stack.getItem();
        boolean isThundering = weatherItem.isThundering(stack, worldIn);
        int harvestBonus = (int) getDestroyBonus(stack)*100;

        if (harvestBonus > 0)
            tooltip.add(new StringTextComponent("§9+" + harvestBonus + "% Harvest Speed"));
        if (isThundering)
            tooltip.add(new StringTextComponent("§eMaxed Harvest Level."));
    }

    public void updateInventory(ItemStack stack, World world, LivingEntity holdingEntity, boolean isSelected) {
        IWeatherInfluencedItem weatherTool = (IWeatherInfluencedItem) stack.getItem();

        if (weatherTool.isThundering(stack, world) && isSelected) {
            world.addParticle(
                    ParticleTypes.ENCHANT, // The type of particle
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
    }

    public int getHarvestLevel(ItemStack stack, int base){
        IWeatherInfluencedItem weatherItem = (IWeatherInfluencedItem) stack.getItem();

        if (weatherItem.isThundering(stack))
            return 5;

        return base;
    }

    public float getDestroyBonus(ItemStack stack) {
        IWeatherInfluencedItem weatherTool = (IWeatherInfluencedItem) stack.getItem();
        boolean isThundering = weatherTool.isThundering(stack);
        float weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack);

        float bonus = 0.03F*weatherBoosterLevel;

        if (isThundering)
            bonus += 0.3F;

        return bonus;
    }

    public void hurtEnemyWithLightning(ItemStack stack, LivingEntity target, LivingEntity attacker){
        if (target.level.isClientSide) return;
        if (!target.level.canSeeSky(target.blockPosition())) return;

        IWeatherInfluencedItem weatherItem = (IWeatherInfluencedItem) stack.getItem();
        ServerWorld serverWorld = (ServerWorld) target.level;
        if (!weatherItem.isThundering(stack, serverWorld)) return;

        int weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack);
        if (weatherBoosterLevel == 0) return;

        int calculatedRoll = serverWorld.getRandom().nextInt(125 - 25*weatherBoosterLevel);
        if (calculatedRoll != 0) return;

        LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(serverWorld);

        if (lightning != null) {
            lightning.moveTo(target.getX(), target.getY(), target.getZ());

            lightning.setCause(attacker instanceof ServerPlayerEntity ? (ServerPlayerEntity) attacker : null);
            target.playSound(SoundEvents.TRIDENT_THUNDER, 5.0F, 1.0F);

            serverWorld.addFreshEntity(lightning);
        }
    }
}
