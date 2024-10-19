package com.oblivioussp.spartanshields.client;

import com.oblivioussp.spartanshields.ModSpartanShields;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModKeyBinds 
{
	public static final KeyBinding KEY_ALT_SHIELD_BASH = new KeyBinding("key." + ModSpartanShields.ID + ".alt_shield_bash", -1, "key." + ModSpartanShields.ID + ".category");
	
	public static void registerKeyBinds()
	{
		ClientRegistry.registerKeyBinding(KEY_ALT_SHIELD_BASH);
	}
}
