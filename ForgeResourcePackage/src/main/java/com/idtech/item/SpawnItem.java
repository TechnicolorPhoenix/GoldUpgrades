package com.idtech.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class SpawnItem extends Item {

    public static final Item SPAWN_ITEM = new SpawnItem(new Properties().tab(CreativeModeTab.TAB_MISC));

    public SpawnItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        player.getInventory().
        player.getInventory().add(new ItemStack(ItemMod.DUST));
        return super.use(world, player, hand);
    }
}
