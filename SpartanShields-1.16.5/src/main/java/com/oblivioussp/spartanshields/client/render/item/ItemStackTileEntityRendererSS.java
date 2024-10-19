package com.oblivioussp.spartanshields.client.render.item;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import com.oblivioussp.spartanshields.client.model.DragonBoneShieldModel;
import com.oblivioussp.spartanshields.client.model.KiteShieldModel;
import com.oblivioussp.spartanshields.client.model.ShieldBaseModel;
import com.oblivioussp.spartanshields.client.model.TowerShieldModel;
import com.oblivioussp.spartanshields.init.ModItems.TowerShieldRenderType;
import com.oblivioussp.spartanshields.item.ShieldBaseItem;

import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;

public class ItemStackTileEntityRendererSS extends ItemStackTileEntityRenderer 
{
	private final TowerShieldRenderInfo renderInfo;
	private final ShieldBaseModel model;

	public ItemStackTileEntityRendererSS(TowerShieldRenderType modelType) 
	{
		switch(modelType)
		{
			case DRAGONBONE:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_DRAGONBONE;
				model = new DragonBoneShieldModel();
				break;
			case ELECTRUM:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_ELECTRUM;
				model = new TowerShieldModel();
				break;
			case PLATINUM:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_PLATINUM;
				model = new KiteShieldModel();
				break;
			case CONSTANTAN:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_CONSTANTAN;
				model = new TowerShieldModel();
				break;
			case INVAR:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_INVAR;
				model = new TowerShieldModel();
				break;
			case NICKEL:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_NICKEL;
				model = new ShieldBaseModel();
				break;
			case LEAD:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_LEAD;
				model = new KiteShieldModel();
				break;
			case SILVER:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_SILVER;
				model = new TowerShieldModel();
				break;
			case STEEL:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_STEEL;
				model = new KiteShieldModel();
				break;
			case BRONZE:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_BRONZE;
				model = new KiteShieldModel();
				break;
			case TIN:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_TIN;
				model = new ShieldBaseModel();
				break;
			case COPPER:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_COPPER;
				model = new ShieldBaseModel();
				break;
			case OBSIDIAN:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_OBSIDIAN;
				model = new ShieldBaseModel();
				break;
			case NETHERITE:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_NETHERITE;
				model = new KiteShieldModel();
				break;
			case DIAMOND:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_DIAMOND;
				model = new KiteShieldModel();
				break;
			case GOLD:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_GOLD;
				model = new TowerShieldModel();
				break;
			case IRON:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_IRON;
				model = new KiteShieldModel();
				break;
			case STONE:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_STONE;
				model = new ShieldBaseModel();
				break;
			case WOOD:
			default:
				renderInfo = TextureStitcher.RENDER_INFO_TOWER_SHIELD_WOOD;
				model = new ShieldBaseModel();
				break;
		}
	}
	
	@Override
	public void renderByItem(ItemStack stack, TransformType p_239207_2_, MatrixStack matrixStack,
			IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
	{
		Item item = stack.getItem();
		
		if(item instanceof ShieldBaseItem)
		{
			boolean flag = stack.getTagElement("BlockEntityTag") != null;
			
			matrixStack.pushPose();
			matrixStack.scale(1.0f, -1.0f, -1.0f);
			RenderMaterial material = flag ? renderInfo.getMaterialWithPattern() : renderInfo.getMaterialNoPattern();
			IVertexBuilder vertexBuilder = material.sprite().wrap(ItemRenderer.getFoilBuffer(buffer, 
					model.renderType(material.atlasLocation()), false, stack.hasFoil()));
			model.getHandle().render(matrixStack, vertexBuilder, combinedLight, combinedOverlay);
			model.getMainPlate().render(matrixStack, vertexBuilder, combinedLight, combinedOverlay);
			if(flag)
			{
				List<Pair<BannerPattern, DyeColor>> list = BannerTileEntity.createPatterns(ShieldItem.getColor(stack), BannerTileEntity.getItemPatterns(stack));
				// Render the main plate of the shield with the banner applied to it?
				//BannerTileEntityRenderer.func_230180_a_(matrixStack, renderTypeBuffer, combinedLight, combinedOverlay, model.getMainPlate(), material, false, list);
				renderBanner(matrixStack, vertexBuilder, buffer, combinedLight, combinedOverlay, model.getMainPlate(), material, false, list);
			}
			model.renderExtraParts(matrixStack, vertexBuilder, combinedLight, combinedOverlay);
			matrixStack.popPose();
		}
	}
	
	private void renderBanner(MatrixStack matrixStack, IVertexBuilder vertexBuilder, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay, ModelRenderer modelRenderer, RenderMaterial modelMaterial, boolean isBanner, List<Pair<BannerPattern, DyeColor>> bannerList)
	{
		for(int i = 0; i < 17 && i < bannerList.size(); ++i) 
		{
			Pair<BannerPattern, DyeColor> pair = bannerList.get(i);
			float[] afloat = pair.getSecond().getTextureDiffuseColors();
			RenderMaterial material = new RenderMaterial(isBanner ? Atlases.BANNER_SHEET : Atlases.SHIELD_SHEET, pair.getFirst().location(isBanner));
			modelRenderer.render(matrixStack, material.buffer(renderTypeBuffer, RenderType::entityNoOutline), combinedLight, combinedOverlay, afloat[0], afloat[1], afloat[2], 1.0F);
		}
	}
	
}
