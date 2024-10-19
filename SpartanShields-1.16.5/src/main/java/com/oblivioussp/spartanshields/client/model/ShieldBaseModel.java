package com.oblivioussp.spartanshields.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ShieldBaseModel extends Model
{
	public ModelRenderer plate;
	public ModelRenderer handle;
	
	public ShieldBaseModel()
    {
		super(RenderType::entitySolid);
		this.texWidth = 64;
        this.texHeight = 64;
        plate = new ModelRenderer(this, 0, 0);
		plate.setPos(0.0F, 0.0F, 0.0F);
		plate.addBox(-6.0f, -11.0f, -2.0f, 12.0f, 22.0f, 1.0f, 0.0f);

		handle = new ModelRenderer(this, 26, 0);
		handle.setPos(0.0F, 0.0F, 0.0F);
		handle.addBox(-1.0f, -3.0f, -1.0f, 2.0f, 6.0f, 6.0f, 0.0f);
    }
	
	public ModelRenderer getMainPlate()
	{
		return plate;
	}
	
	public ModelRenderer getHandle()
	{
		return handle;
	}
	
	/**
	 * Render any extra parts of the model here.
	 * @param matrixStack
	 * @param vertexBuilder
	 * @param packedLightIn
	 * @param packedOverlayIn
	 */
	public void renderExtraParts(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn)
	{
		
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha)
	{
		this.plate.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.handle.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
