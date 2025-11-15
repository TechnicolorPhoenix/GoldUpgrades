package com.titanhex.goldupgrades.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModEvents {

    public static void register(IEventBus bus){
        bus.register(new JumpEventHandler());
        bus.register(new CurseOfRustEventHandler());
        bus.register(new RaidArmorEventHandler());
        bus.register(new StoneskinEventHandler());
        bus.register(new StasisHandler());
    }
}
