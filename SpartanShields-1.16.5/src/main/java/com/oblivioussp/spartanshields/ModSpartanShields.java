package com.oblivioussp.spartanshields;

import com.oblivioussp.spartanshields.client.ClientHelper;
import com.oblivioussp.spartanshields.client.ModKeyBinds;
import com.oblivioussp.spartanshields.network.NetworkHandler;
import com.oblivioussp.spartanshields.util.Config;
import com.oblivioussp.spartanshields.util.Log;

import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModSpartanShields.ID)
@Mod.EventBusSubscriber
public class ModSpartanShields
{
    public static final String ID = "spartanshields";
    public static final String NAME = "Spartan Shields";
	
    public ModSpartanShields()
    {
        Log.info("Constructing Mod: " + NAME);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC);
        MinecraftForge.EVENT_BUS.register(this);
        
    }

    private void onSetup(FMLCommonSetupEvent ev)
    {
        Log.info("Setting up " + NAME + "!");
        ev.enqueueWork(() ->
        {
        	NetworkHandler.init();
        });
    }

    private void onClientSetup(FMLClientSetupEvent ev)
    {
        Log.info("Setting up Client for " + NAME + "!");
        ClientHelper.registerItemColors();
        ModKeyBinds.registerKeyBinds();
    }
   
    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent ev)
    {
    	if(Config.hasEventErrorOccured())
    		ev.getEntityLiving().sendMessage(new TranslationTextComponent("message." + ID + ".event_bus_shutdown_error").withStyle(TextFormatting.RED), Util.NIL_UUID);
    }
}
