package com.oblivioussp.spartanshields.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;

public class TowerShieldModel extends ShieldBaseModel
{
    public ModelRenderer plateLeft;
    public ModelRenderer plateRight;

    public TowerShieldModel()
    {
    	super();
        this.plateLeft = new ModelRenderer(this, 42, 0);
        this.plateLeft.setPos(-6.0f, 0.0f, -2.0f);
        this.plateLeft.yRot = 45.0f;
        this.plateLeft.addBox(-2.0f, -11.0f, 0.0f, 2, 22, 1, 0.0f);
        this.plateRight = new ModelRenderer(this, 48, 0);
        this.plateRight.setPos(6.0f, 0.0f, -2.0f);
        this.plateRight.yRot = -45.0f;
        this.plateRight.addBox(0.0f, -11.0f, 0.0f, 2, 22, 1, 0.0f);
    }
    
    @Override
    public void renderExtraParts(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int packedLightIn,
    		int packedOverlayIn)
    {
    	this.plateLeft.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn);
    	this.plateRight.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn);
    }
    
    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn,
    		float red, float green, float blue, float alpha)
    {
    	super.renderToBuffer(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    	this.plateLeft.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn);
    	this.plateRight.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn);
    }
}
