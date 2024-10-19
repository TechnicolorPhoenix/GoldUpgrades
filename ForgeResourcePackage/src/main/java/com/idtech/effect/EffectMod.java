package com.idtech.effect;

import com.idtech.BaseMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectMod {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BaseMod.MODID);

    public static final RegistryObject<MobEffect> BOOM = MOB_EFFECTS.register("freeze", () -> new BoomEffect(MobEffectCategory.HARMFUL, 3124687));

    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }
}
