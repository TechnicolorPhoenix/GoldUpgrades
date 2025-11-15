package com.titanhex.goldupgrades.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;

public class DeathDelayEffect extends Effect {
    protected DeathDelayEffect() {
        super(EffectType.BENEFICIAL, 0x47B4B4);
    }

    // Prevents the effect from being dispelled by milk, but allows a creative mode player to remove it.
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }

    @Override
    public boolean shouldRender(EffectInstance effect) {
        return false;
    }
}
