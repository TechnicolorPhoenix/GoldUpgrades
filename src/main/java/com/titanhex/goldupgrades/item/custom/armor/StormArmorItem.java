package com.titanhex.goldupgrades.item.custom.armor;

import com.titanhex.goldupgrades.item.custom.inter.IJumpBoostArmor;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import net.minecraft.client.util.ITooltipFlag;
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

public class StormArmorItem extends ArmorItem implements IJumpBoostArmor, ILevelableItem {
    double jumpBoost;
    int armorLevel;

    public StormArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, float jumpBoost, Properties builderIn) {
        super(materialIn, slot, builderIn);
        this.jumpBoost = jumpBoost;
        this.armorLevel = getItemLevel();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        float jumpHeight = 0;

        if (this.armorLevel == 1)
            jumpHeight = 1;
        if (this.armorLevel == 2)
            jumpHeight = 1.5F;
        if (this.armorLevel == 3)
            jumpHeight = 2;

        tooltip.add(new StringTextComponent("§9+" + jumpHeight + " Jump Boost"));
        tooltip.add(new StringTextComponent("§eNo Fall Damage."));
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);

        if (this.slot == EquipmentSlotType.FEET) {
            if (!world.isClientSide) {
                player.fallDistance = 0.0F;
            }
        }
    }

    @Override
    public double getJumpBoostModifier() {
        return this.jumpBoost;
    }
}
