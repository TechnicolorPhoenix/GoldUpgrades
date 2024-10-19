package net.titanhex.thex.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;

public class ExtendItem {

    public static boolean blockIsValidForMaterial(BlockState clickedBlock, Material material) {
        return clickedBlock.getMaterial() == material;
    }
}
