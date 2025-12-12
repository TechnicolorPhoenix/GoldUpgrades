package com.titanhex.goldupgrades.item.custom.armor;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.custom.inter.IJumpBoostArmor;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StormArmorItem extends ArmorItem implements IJumpBoostArmor, ILevelableItem, IWeatherInfluencedItem
{
    String NBT_FLIGHT_REQUIREMENT_MET = "FlightRequirementMet";

    public StormArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builderIn) {
        super(materialIn, slot, builderIn);
    }

    private boolean getFlightRequirement(LivingEntity livingEntity) {
        return getTotalSetLevel(livingEntity) > 5;
    }
    private boolean getFlightRequirement(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_FLIGHT_REQUIREMENT_MET);
    }
    private void setFlightRequirement(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_FLIGHT_REQUIREMENT_MET, value);
    }

    @Override
    public float getFallDamageReductionFraction() {
        int level = getItemLevel();
        float perLevelBonus = 0.05F * level;
        float finalBonus = (0.1F + perLevelBonus);
        GoldUpgrades.LOGGER.debug("FINAL BONUS: {}, LEVEL: {}, PER LEVEL BONUS: {}", finalBonus, level, perLevelBonus);
        return finalBonus;
    }

    @Override
    public double getJumpBoostModifier() {
        int armorLevel = getItemLevel();
        double totalDesiredBonus = (armorLevel - 0.5);

        return totalDesiredBonus / (10*4);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        double jumpHeight = getJumpBoostModifier() * 10;
        boolean isThundering = getWeather(stack) == Weather.THUNDERING;
        boolean flightRequirementsMet = getFlightRequirement(stack);
        int fallDamageReduction = (int) (getFallDamageReductionFraction()*100);

        if (isThundering && flightRequirementsMet)
            tooltip.add(new StringTextComponent("§eCan Fly"));

        tooltip.add(new StringTextComponent("§9+" + String.format("%.1f", jumpHeight) + " Jump Boost"));
        tooltip.add(new StringTextComponent("§9" + fallDamageReduction + "% Fall Damage Reduction"));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int uInt, boolean uBoolean) {
        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);

        if (world.isClientSide)
            return;

        Weather oldWeather = getWeather(stack);

        Weather currentWeather = Weather.getCurrentWeather(world);

        if (oldWeather != currentWeather) {
            setWeather(stack, currentWeather);
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);

        boolean currentRequirementsMet = getFlightRequirement(player);
        boolean previousRequirementsMet = getFlightRequirement(stack);

        if (currentRequirementsMet != previousRequirementsMet)
            setFlightRequirement(stack, currentRequirementsMet);
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        boolean setRequirementMet = getTotalSetLevel(entity) > 5;

        return getWeather(stack) == Weather.THUNDERING && setRequirementMet;
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level.isClientSide && (flightTicks + 1) % 20 == 0) {
            stack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(this.slot));
        }
        return true;
    }
}
