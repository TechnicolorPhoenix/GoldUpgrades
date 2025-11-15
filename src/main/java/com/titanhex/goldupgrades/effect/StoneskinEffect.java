package com.titanhex.goldupgrades.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class StoneskinEffect extends Effect {
    protected StoneskinEffect() {
        // Define the curse effect as negative and choose a color (e.g., rust brown/green)
        super(EffectType.BENEFICIAL, 0x928E85);
    }

    // Since the effect is passive (damage reduction),
    // we don't need logic here. The event listener handles it.
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
