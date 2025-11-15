package com.titanhex.goldupgrades.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class StasisEffect extends Effect {
    protected StasisEffect() {
        super(EffectType.NEUTRAL, 0x1A0000);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false; // Only use removeAttributeModifiers for cleanup.
    }
}
