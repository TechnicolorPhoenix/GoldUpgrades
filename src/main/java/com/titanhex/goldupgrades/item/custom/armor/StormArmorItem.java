package com.titanhex.goldupgrades.item.custom.armor;

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

public class StormArmorItem extends ArmorItem implements IJumpBoostArmor, ILevelableItem, IWeatherInfluencedItem {
    int armorLevel;

    public StormArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builderIn) {
        super(materialIn, slot, builderIn);
        this.armorLevel = getItemLevel();
    }
    @Override
    public float getFallDamageReductionFraction() {
        return 0.1F + 0.5f * armorLevel;
    }
    @Override
    public double getJumpBoostModifier() {
        double term1 = 0.1666 * this.armorLevel * this.armorLevel;
        double term2 = -0.333 * this.armorLevel;

        return (term1 + term2 + 0.5)/5;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        double jumpHeight = getJumpBoostModifier() / 0.33;

        tooltip.add(new StringTextComponent("§9+" + String.format("%.1f", jumpHeight) + " Jump Boost"));
        tooltip.add(new StringTextComponent("§9" + (int) getFallDamageReductionFraction() + "% Fall Damage Reduction    ."));
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

        if (!world.isClientSide) {
            player.fallDistance *= 0.75F;
        }
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        boolean setRequirementMet = ILevelableItem.getTotalSetLevel(entity) > 5;

        return getWeather(stack) == Weather.THUNDERING && setRequirementMet;
    }
}
