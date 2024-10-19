package net.titanhex.thex.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.titanhex.thex.data.TitanhexTags;
import org.apache.logging.log4j.LogManager;

import java.util.function.Consumer;

public class SeaGrazer extends Item {

    public SeaGrazer(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getWorld();

        if (!world.isRemote) {
            PlayerEntity playerEntity = context.getPlayer();
            BlockState clickedBlock = world.getBlockState(context.getPos());

            if (rightClickOnCertainBlockState(clickedBlock, context, playerEntity)){
                stack.damageItem(1, playerEntity, player -> playerEntity.sendBreakAnimation(context.getHand()));
            }
        }

        return super.onItemUseFirst(stack, context);
    }

    public boolean rightClickOnCertainBlockState(BlockState clickedBlock, ItemUseContext context, PlayerEntity playerEntity) {
        if (clickedBlock.isIn(TitanhexTags.Blocks.SEA_GRAZER_CLICKABLE_BLOCKS)) {
            destroyBlockAndRegen(playerEntity, context.getWorld(), context.getPos());
            return true;
        }
        return false;
    }
    private void destroyBlockAndRegen(PlayerEntity player, World world, BlockPos pos){
        LogManager.getLogger().info("Destroy Block and Regen");
        player.addPotionEffect(new EffectInstance(Effects.INSTANT_HEALTH, 1, 1));
        world.destroyBlock(pos, true);
    }
}
