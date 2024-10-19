package com.titanhex.thex.init;

import com.titanhex.thex.enchantment.*;
import com.titanhex.thex.util.Log;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEnchantments
{
    public static Enchantment spikes, weakness, slowness, firebrand;

    @SubscribeEvent
    public static void onRegister(RegistryEvent.Register<Enchantment> ev)
    {
        IForgeRegistry<Enchantment> reg = ev.getRegistry();

        EquipmentSlotType[] shieldSlotType = {EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND};
        spikes = new SpikesEnchantment(Enchantment.Rarity.UNCOMMON, shieldSlotType);
        weakness = new WeaknessEnchantment(Enchantment.Rarity.UNCOMMON, shieldSlotType);
        slowness = new SlownessEnchantment(Enchantment.Rarity.UNCOMMON, shieldSlotType);
        firebrand = new FirebrandEnchantment(Enchantment.Rarity.RARE, shieldSlotType);

        Log.info("Registering Enchantments");
        reg.registerAll(spikes, firebrand, weakness, slowness);
        Log.info("Registered Enchantments successfully!");
    }
}
