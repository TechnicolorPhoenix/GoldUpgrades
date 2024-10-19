package net.titanhex.thex.entity.custom;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

public class HexShieldRender extends ItemStackTileEntityRenderer {
    private final HexShieldModel shieldModel;

    public HexShieldRender(){
        this.shieldModel = new HexShieldModel();
    }

}
