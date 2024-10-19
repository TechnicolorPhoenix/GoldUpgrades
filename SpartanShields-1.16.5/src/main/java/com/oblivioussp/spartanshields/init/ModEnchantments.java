package com.oblivioussp.spartanshields.init;

import com.oblivioussp.spartanshields.enchantment.FirebrandEnchantment;
import com.oblivioussp.spartanshields.enchantment.PaybackEnchantment;
import com.oblivioussp.spartanshields.enchantment.SpikesEnchantment;
import com.oblivioussp.spartanshields.util.Log;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEnchantments
{
    public static Enchantment spikes, firebrand, payback;

    @SubscribeEvent
    public static void onRegister(RegistryEvent.Register<Enchantment> ev)
    {
        IForgeRegistry<Enchantment> reg = ev.getRegistry();

        EquipmentSlotType[] shieldSlotType = {EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND};
        spikes = new SpikesEnchantment(Enchantment.Rarity.UNCOMMON, shieldSlotType);
        firebrand = new FirebrandEnchantment(Enchantment.Rarity.RARE, shieldSlotType);
        payback = new PaybackEnchantment(Enchantment.Rarity.VERY_RARE, shieldSlotType);

        Log.info("Registering Enchantments");
        reg.registerAll(spikes, firebrand, payback);
        Log.info("Registered Enchantments successfully!");
    }
}
