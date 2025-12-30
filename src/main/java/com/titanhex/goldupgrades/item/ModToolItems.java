package com.titanhex.goldupgrades.item;

import com.titanhex.goldupgrades.item.registries.tool.FireToolItems;
import com.titanhex.goldupgrades.item.registries.tool.ObsidianToolItems;
import com.titanhex.goldupgrades.item.registries.tool.SeaToolItems;
import com.titanhex.goldupgrades.item.registries.tool.StormToolItems;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModToolItems {
    public static void register(IEventBus eventBus) {
        FireToolItems.ITEMS.register(eventBus);
        SeaToolItems.ITEMS.register(eventBus);
        StormToolItems.ITEMS.register(eventBus);
        ObsidianToolItems.ITEMS.register(eventBus);
    }
}
