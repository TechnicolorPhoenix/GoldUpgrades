package com.titanhex.goldupgrades.item;

import com.titanhex.goldupgrades.item.armor.*;

import net.minecraftforge.eventbus.api.IEventBus;

public class ModArmorItems {

    public static void register(IEventBus eventBus) {
        ObsidianArmorItems.ITEMS.register(eventBus);
        FireArmorItems.ITEMS.register(eventBus);
        StormArmorItems.ITEMS.register(eventBus);
        SeaArmorItems.ITEMS.register(eventBus);
        RaidArmorItems.ITEMS.register(eventBus);
    }
}
