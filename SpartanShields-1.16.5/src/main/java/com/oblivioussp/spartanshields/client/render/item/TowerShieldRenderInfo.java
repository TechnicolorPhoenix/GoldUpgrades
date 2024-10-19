package com.oblivioussp.spartanshields.client.render.item;

import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

@SuppressWarnings("deprecation")
public class TowerShieldRenderInfo
{
	protected final ResourceLocation textureNoPattern;
	protected final ResourceLocation texturePattern;
	
	protected final RenderMaterial materialNoPattern;
	protected final RenderMaterial materialPattern;
	
	public TowerShieldRenderInfo(ResourceLocation texNoPattern, ResourceLocation texPattern)
	{
		textureNoPattern = texNoPattern;
		texturePattern = texPattern;
		
		materialNoPattern = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, textureNoPattern);
		materialPattern = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, texturePattern);
	}
	
	public RenderMaterial getMaterialNoPattern()
	{
		return materialNoPattern;
	}
	
	public RenderMaterial getMaterialWithPattern()
	{
		return materialPattern;
	}
	
	public void stitchTextures(TextureStitchEvent.Pre ev)
	{
		ev.addSprite(textureNoPattern);
		ev.addSprite(texturePattern);
	}
}