package com.oblivioussp.spartanshields.client.render.item;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.util.Log;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@SuppressWarnings("deprecation")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TextureStitcher 
{
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_WOOD = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_wood_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_wood_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_STONE = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_stone_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_stone_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_IRON = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_iron_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_iron_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_GOLD = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_gold_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_gold_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_DIAMOND = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_diamond_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_diamond_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_NETHERITE = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_netherite_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_netherite_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_OBSIDIAN = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_obsidian_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_obsidian_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_COPPER = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_copper_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_copper_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_TIN = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_tin_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_tin_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_BRONZE = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_bronze_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_bronze_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_STEEL = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_steel_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_steel_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_SILVER = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_silver_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_silver_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_LEAD = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_lead_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_lead_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_NICKEL = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_nickel_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_nickel_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_INVAR = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_invar_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_invar_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_CONSTANTAN = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_constantan_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_constantan_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_PLATINUM = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_platinum_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_platinum_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_ELECTRUM = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_electrum_nopattern"),
															new ResourceLocation(ModSpartanShields.ID, "entity/shield_tower_electrum_pattern"));
	public static final TowerShieldRenderInfo RENDER_INFO_TOWER_SHIELD_DRAGONBONE = new TowerShieldRenderInfo(new ResourceLocation(ModSpartanShields.ID, "entity/iceandfire/shield_tower_dragonbone_nopattern"),
			new ResourceLocation(ModSpartanShields.ID, "entity/iceandfire/shield_tower_dragonbone_nopattern"));

	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre ev)
	{
		if(ev.getMap().location() == AtlasTexture.LOCATION_BLOCKS)
		{
			Log.info("Adding Tower Shield textures to Block Texture Atlas!");
			RENDER_INFO_TOWER_SHIELD_WOOD.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_STONE.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_IRON.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_GOLD.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_DIAMOND.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_NETHERITE.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_OBSIDIAN.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_COPPER.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_TIN.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_BRONZE.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_STEEL.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_SILVER.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_LEAD.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_NICKEL.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_INVAR.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_CONSTANTAN.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_PLATINUM.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_ELECTRUM.stitchTextures(ev);
			RENDER_INFO_TOWER_SHIELD_DRAGONBONE.stitchTextures(ev);
//			Log.info("Finished adding textures!");
		}
	}
}
