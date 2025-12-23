package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.item.components.TreasureToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.*;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public class FireGoldHoe extends HoeItem implements ILevelableItem, IIgnitableTool, IDimensionInfluencedItem, IWeatherInfluencedItem, IDayInfluencedItem, ILightInfluencedItem, IElementalHoe
{
    int burnTicks;
    int durabilityUse;
    TreasureToolComponent treasureHandler;

    public FireGoldHoe(IItemTier tier, int atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
        treasureHandler = new TreasureToolComponent();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int inventorySlot, boolean isSelected) {
        super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);

        calibrateLightLevel(stack, world, holdingEntity);

        if (world.isClientSide)
            return;

        if (!(holdingEntity instanceof LivingEntity))
            return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;

        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.OFFHAND) == stack;

        changeDimension(stack, world);
        changeWeather(stack, world);
        changeDay(stack, world);

        if (isEquipped) {
            holdingElementalHoe(stack, livingEntity, Effects.FIRE_RESISTANCE, () -> (isClear(stack) || inValidDimension(stack)));
        }
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
        if (!isClear(usedStack)) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
        MagmaCubeEntity magmaCube = new MagmaCubeEntity(EntityType.MAGMA_CUBE, world);

        try {
            // "func_70799_a" is the SRG name for setSlimeSize in 1.16.5
            Method setSizeMethod = ObfuscationReflectionHelper.findMethod(SlimeEntity.class, "func_70799_a", int.class, boolean.class);

            // Call the method: setSizeMethod.invoke(entity, size, resetHealth)
            setSizeMethod.invoke(magmaCube, world.getRandom().nextInt(5)+1, true);
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        magmaCube.setHealth(magmaCube.getMaxHealth());
        treasureHandler.tryMonsterSpawn(
                world, blockPos, blockState, miningEntity, usedStack,
                Biomes.BADLANDS.location(),
                () -> blockState.is(Blocks.DEAD_BUSH),
                () -> isClear(usedStack, world),
                magmaCube,
                9
        );
        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
    }

    private float calculateBonusDestroySpeed(ItemStack stack) {
        int lightLevel = ILightInfluencedItem.getLightLevel(stack);

        return (lightLevel > 7 ? 0.15F : 0.00F) + (isClear(stack) ? (float) IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack)/100 : 0);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        if (baseSpeed > 1.0F) {
            float bonusSpeed = calculateBonusDestroySpeed(stack);
            float speedMultiplier = 1.0F + bonusSpeed;

            return baseSpeed * speedMultiplier;        }

        return baseSpeed;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        treasureHandler.appendHoverText(stack, tooltip, "§eCutting dead bushes in badlands yields magma cubes in clear weather.");
        IIgnitableTool.appendHoverText(stack, tooltip);
        IElementalHoe.appendHoverText(stack, tooltip, "§eHold for Fire Resistance, use for Strength");
    }

    /**
     * Ignites a target LivingEntity for a short duration when hit.
     * This logic is typically called from the Item's hitEntity method.
     *
     * @param target The LivingEntity to ignite.
     */
    @Override
    public void igniteEntity(LivingEntity target, ItemStack stack) {
        target.setSecondsOnFire(2 + (isDay(stack) ? 2 : 0));
    }

    /**
     * Handles the entity hit event (Left Click on Entity).
     */
    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        igniteEntity(target, stack);
        return true;
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand); // Get the ItemStack directly from the context

        if (hand == Hand.OFF_HAND)
            return IElementalHoe.use(stack, player, Effects.DAMAGE_BOOST, 30*20);

        return super.use(world, player, hand);
    }

    /**
     * Handles the block use event (Right Click) with custom Hoe and Fire functionality.
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (world.isClientSide)
            return ActionResultType.SUCCESS;

        PlayerEntity player = context.getPlayer();
        if (player == null)
            return super.useOn(context);

        //TODO: Set this to cook potato, or bake bread.
        return IIgnitableTool.useOn(context, null);
    }

    @Override
    public DimensionType primaryDimension() {
        return DimensionType.NETHER;
    }
}
