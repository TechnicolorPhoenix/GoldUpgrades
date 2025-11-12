package com.titanhex.goldupgrades.item;

import com.titanhex.goldupgrades.item.tool.FireToolItems;
import com.titanhex.goldupgrades.item.tool.ObsidianToolItems;
import com.titanhex.goldupgrades.item.tool.SeaToolItems;
import com.titanhex.goldupgrades.item.tool.StormToolItems;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModToolItems {
    public static void register(IEventBus eventBus) {
        FireToolItems.ITEMS.register(eventBus);
        SeaToolItems.ITEMS.register(eventBus);
        StormToolItems.ITEMS.register(eventBus);
        ObsidianToolItems.ITEMS.register(eventBus);
    }
}
