package com.oblivioussp.spartanshields.client.model;

import net.minecraft.client.renderer.model.ModelRenderer;

// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports

public class DragonBoneShieldModel extends ShieldBaseModel 
{
	private final ModelRenderer righthorn2_r1;
	private final ModelRenderer lefthorn2_r1;
	private final ModelRenderer righthorn1_r1;
	private final ModelRenderer lefthorn1_r1;
	private final ModelRenderer skull_base_r1;
	private final ModelRenderer rightrib3_r1;
	private final ModelRenderer leftrib3_r1;
	private final ModelRenderer rightrib3_1_r1;
	private final ModelRenderer rightrib3_2_r1;
	private final ModelRenderer leftrib3_1_r1;
	private final ModelRenderer leftrib3_2_r1;

	public DragonBoneShieldModel()
	{
		super();
		texWidth = 64;
		texHeight = 64;

		plate = new ModelRenderer(this);
		plate.setPos(0.0F, 4.0F, 0.0F);
		plate.texOffs(0, 0).addBox(-1.5F, -9.0F, -3.5F, 3.0F, 16.0F, 2.0F, 0.0F, false);
//		bb_main.texOffs(0, 0).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F, 0.0F, false);
		plate.texOffs(0, 0).addBox(-1.0F, -8.99F, -4.0F, 2.0F, 16.0F, 3.0F, 0.0F, false);

		righthorn2_r1 = new ModelRenderer(this);
		righthorn2_r1.setPos(-6.0F, -15.0F, -3.0F);
		plate.addChild(righthorn2_r1);
		setRotationAngle(righthorn2_r1, -1.0908F, -0.7854F, 0.0F);
		righthorn2_r1.texOffs(0, 0).addBox(-0.8F, -0.4F, 0.7F, 2.0F, 2.0F, 5.0F, 0.0F, false);

		lefthorn2_r1 = new ModelRenderer(this);
		lefthorn2_r1.setPos(6.0F, -15.0F, -3.0F);
		plate.addChild(lefthorn2_r1);
		setRotationAngle(lefthorn2_r1, -1.0908F, 0.7854F, 0.0F);
		lefthorn2_r1.texOffs(0, 0).addBox(-1.2F, -0.4F, 0.7F, 2.0F, 2.0F, 5.0F, 0.0F, false);

		righthorn1_r1 = new ModelRenderer(this);
		righthorn1_r1.setPos(-2.0F, -13.0F, -7.0F);
		plate.addChild(righthorn1_r1);
		setRotationAngle(righthorn1_r1, 0.5236F, -0.7854F, 0.0F);
		righthorn1_r1.texOffs(0, 0).addBox(-0.8F, -0.2F, 0.3F, 2.0F, 2.0F, 6.0F, 0.0F, false);

		lefthorn1_r1 = new ModelRenderer(this);
		lefthorn1_r1.setPos(2.0F, -13.0F, -7.0F);
		plate.addChild(lefthorn1_r1);
		setRotationAngle(lefthorn1_r1, 0.5236F, 0.7854F, 0.0F);
		lefthorn1_r1.texOffs(0, 0).addBox(-1.2F, -0.2F, 0.3F, 2.0F, 2.0F, 6.0F, 0.0F, false);

		skull_base_r1 = new ModelRenderer(this);
		skull_base_r1.setPos(0.0F, -7.0F, -2.5F);
		plate.addChild(skull_base_r1);
		setRotationAngle(skull_base_r1, 1.1781F, 0.0F, 0.0F);
		skull_base_r1.texOffs(0, 0).addBox(-4.0F, -7.0F, -1.5F, 8.0F, 7.0F, 6.0F, 0.0F, false);
		skull_base_r1.texOffs(0, 0).addBox(-3.0F, -5.0F, -7.5F, 6.0F, 5.0F, 6.0F, 0.0F, false);

		rightrib3_r1 = new ModelRenderer(this);
		rightrib3_r1.setPos(-1.0F, -3.0F, -4.0F);
		plate.addChild(rightrib3_r1);
		setRotationAngle(rightrib3_r1, 0.0F, 2.0944F, 0.0F);
		rightrib3_r1.texOffs(0, 0).addBox(-1.0F, 0.0F, -5.0F, 1.0F, 1.0F, 5.0F, 0.0F, false);
		rightrib3_r1.texOffs(0, 0).addBox(-1.0F, 5.0F, -5.0F, 1.0F, 1.0F, 5.0F, 0.0F, false);

		leftrib3_r1 = new ModelRenderer(this);
		leftrib3_r1.setPos(1.0F, -3.0F, -4.0F);
		plate.addChild(leftrib3_r1);
		setRotationAngle(leftrib3_r1, 0.0F, 1.0472F, 0.0F);
		leftrib3_r1.texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 5.0F, 0.0F, false);
		leftrib3_r1.texOffs(0, 0).addBox(-1.0F, 5.0F, 0.0F, 1.0F, 1.0F, 5.0F, 0.0F, false);

		rightrib3_1_r1 = new ModelRenderer(this);
		rightrib3_1_r1.setPos(-1.0F, 5.0F, -4.0F);
		plate.addChild(rightrib3_1_r1);
		setRotationAngle(rightrib3_1_r1, 0.0F, 1.9199F, 0.0F);
		rightrib3_1_r1.texOffs(0, 0).addBox(-1.0F, -1.0F, -5.0F, 1.0F, 2.0F, 5.0F, 0.0F, false);
		rightrib3_1_r1.texOffs(0, 0).addBox(-1.0F, -6.0F, -5.0F, 1.0F, 2.0F, 5.0F, 0.0F, false);

		rightrib3_2_r1 = new ModelRenderer(this);
		rightrib3_2_r1.setPos(-5.0F, 5.0F, -2.0F);
		plate.addChild(rightrib3_2_r1);
		setRotationAngle(rightrib3_2_r1, 0.0F, -0.7854F, 0.0F);
		rightrib3_2_r1.texOffs(0, 0).addBox(-0.6F, -0.5F, 0.2F, 1.0F, 1.0F, 3.0F, 0.0F, false);
		rightrib3_2_r1.texOffs(0, 0).addBox(-0.6F, -5.5F, 0.2F, 1.0F, 1.0F, 3.0F, 0.0F, false);

		leftrib3_1_r1 = new ModelRenderer(this);
		leftrib3_1_r1.setPos(1.0F, 5.0F, -4.0F);
		plate.addChild(leftrib3_1_r1);
		setRotationAngle(leftrib3_1_r1, 0.0F, 1.2217F, 0.0F);
		leftrib3_1_r1.texOffs(0, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 2.0F, 5.0F, 0.0F, false);
		leftrib3_1_r1.texOffs(0, 0).addBox(-1.0F, -6.0F, 0.0F, 1.0F, 2.0F, 5.0F, 0.0F, false);

		leftrib3_2_r1 = new ModelRenderer(this);
		leftrib3_2_r1.setPos(5.0F, 5.0F, -2.0F);
		plate.addChild(leftrib3_2_r1);
		setRotationAngle(leftrib3_2_r1, 0.0F, 0.7854F, 0.0F);
		leftrib3_2_r1.texOffs(0, 0).addBox(-0.4F, -0.5F, 0.2F, 1.0F, 1.0F, 3.0F, 0.0F, false);
		leftrib3_2_r1.texOffs(0, 0).addBox(-0.4F, -5.5F, 0.2F, 1.0F, 1.0F, 3.0F, 0.0F, false);
	}
	
/*	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
	}*/

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}