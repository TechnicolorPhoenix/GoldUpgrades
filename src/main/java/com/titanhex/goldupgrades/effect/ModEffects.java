package com.titanhex.goldupgrades.effect;

import com.titanhex.goldupgrades.GoldUpgrades;
import net.minecraft.potion.Effect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects {
    // 1. Create the DeferredRegister for Effects (Potions)
    public static final DeferredRegister<Effect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.POTIONS, GoldUpgrades.MOD_ID);

    // 2. Register the CurseOfRustEffect
    public static final RegistryObject<Effect> CURSE_OF_RUST =
            EFFECTS.register("curse_of_rust", () -> new CurseOfRustEffect());

    public static final RegistryObject<Effect> STONESKIN =
            EFFECTS.register("stoneskin", () -> new StoneskinEffect());

    public static final RegistryObject<Effect> STASIS =
            EFFECTS.register("stasis", () -> new StasisEffect());

    public static final RegistryObject<Effect> DEATH_DELAY =
            EFFECTS.register("death_delay", () -> new DeathDelayEffect());

    /**
     * Call this method from your main mod constructor to register the effects.
     */
    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
