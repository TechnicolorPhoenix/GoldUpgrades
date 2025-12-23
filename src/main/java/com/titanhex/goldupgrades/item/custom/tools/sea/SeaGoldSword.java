package com.titanhex.goldupgrades.item.custom.tools.sea;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.item.components.DynamicAttributeComponent;
import com.titanhex.goldupgrades.item.components.SeaToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWaterInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectSword;
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
import net.minecraft.item.ItemUseContext;
import net.minecraft.potion.Effect;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SeaGoldSword extends EffectSword implements IWeatherInfluencedItem, IWaterInfluencedItem, ILevelableItem
{
    private final SeaToolComponent seaToolHandler;
    private final DynamicAttributeComponent dynamicAttributeHandler;

    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier.
     *
     * @param tier                  The stat material for the tool.
     * @param attackDamage         The base attack damage.
     * @param attackSpeed          The attack speed modifier.
     * @param effectAmplifications A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration       The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost       The number of durability points to subtract on each use.
     * @param properties           Item properties.
     */
    public SeaGoldSword(IItemTier tier, int attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed, effectAmplifications, effectDuration, durabilityCost, properties);
        this.seaToolHandler = new SeaToolComponent(durabilityCost);
        this.dynamicAttributeHandler = new DynamicAttributeComponent(seaToolHandler.SEA_DAMAGE_MODIFIER, EquipmentSlotType.MAINHAND);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));

        dynamicAttributeHandler.getAttributeModifiers(
                equipmentSlot, builder,
                () -> getIsInRain(stack) || getIsSubmerged(stack),
                () -> (double) getItemLevel() + IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack)
        );

        return builder.build();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int itemSlot, boolean isSelected) {
        if (world.isClientSide) return;

        if (!(holdingEntity instanceof LivingEntity)) return;
        LivingEntity livingEntity = (LivingEntity) holdingEntity;

        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.MAINHAND) == stack;

        boolean submergedChanged = changeSubmerged(stack, holdingEntity);
        boolean isInRainChanged = changeIsInRain(stack, holdingEntity);
        boolean weatherChanged = changeWeather(stack, world);

        if (isInRainChanged || submergedChanged) {
            world.playSound(null, holdingEntity.blockPosition(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        boolean environmentalStateChanged = submergedChanged || weatherChanged || isInRainChanged;

        ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

        if (environmentalStateChanged && isEquipped) {

            if (attackInstance != null) {
                Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(EquipmentSlotType.MAINHAND, stack);

                dynamicAttributeHandler.updateAttributes(livingEntity, newModifiers, attackInstance);
            }
        }

        super.inventoryTick(stack, world, holdingEntity, itemSlot, isSelected);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        seaToolHandler.appendHoverText(stack, tooltip);

        if (!isRaining(stack, worldIn))
            tooltip.add(new StringTextComponent("§cDamage bonus inactive outside of rain and water."));
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return super.damageItem(stack, seaToolHandler.damageItem(stack, amount), entity, onBroken);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        if (baseSpeed > 1.0F) {
            float speedMultiplier = 1.0F + seaToolHandler.getDestroySpeed(stack);

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, @NotNull Hand hand) {
        if (world.isClientSide)
            return super.use(world, player, hand);

        ItemStack stack = player.getItemInHand(hand);
        ActionResult<ItemStack> result = super.use(world, player, hand);

        return seaToolHandler.use(world, player, stack, result);
    }


    /**
     * Handles the item use event (Right Click) with custom logic for water/ice
     * conversion, falling back to the parent class's aura application.
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();

        if (world.isClientSide || player == null)
            return super.useOn(context);

        return seaToolHandler.useOn(this, context, null);
    }
}
