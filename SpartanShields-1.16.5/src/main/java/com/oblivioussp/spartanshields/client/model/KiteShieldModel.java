package com.oblivioussp.spartanshields.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;

public class KiteShieldModel extends ShieldBaseModel 
{
	public ModelRenderer plateLeft;
    public ModelRenderer plateRight;

    public KiteShieldModel()
    {
    	super();
		plateLeft = new ModelRenderer(this, 52, 0);
		plateLeft.setPos(-6.0F, 0.0F, 0.0F);
		setRotationAngle(plateLeft, 0.0F, 0.0F, -0.1963F);
		plateLeft.addBox(-1.85f, -10.788f, -1.99f, 4, 20, 1, false);

		plateRight = new ModelRenderer(this, 42, 0);
		plateRight.setPos(6.0F, 0.0F, 0.0F);
		setRotationAngle(plateRight, 0.0F, 0.0F, 0.1963F);
		plateRight.addBox(-2.15f, -10.788f, -1.99f, 4, 20, 1, false);
    }
    
    @Override
    public void renderExtraParts(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int packedLightIn,
    		int packedOverlayIn) 
    {
    	plateLeft.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn);
    	plateRight.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn);
    }
    
    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn,
    		float red, float green, float blue, float alpha)
    {
    	super.renderToBuffer(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    	plateLeft.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn);
    	plateRight.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn);
    }
	
	protected void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) 
	{
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}
