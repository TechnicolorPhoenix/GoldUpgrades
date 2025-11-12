package com.titanhex.goldupgrades.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

/**
 * Defines the Curse of Rust Mob Effect.
 * The damage logic is handled separately in a Forge Event listener.
 */
public class CurseOfRustEffect extends Effect {

    // Define the curse effect as negative and choose a color (e.g., rust brown/green)
    public CurseOfRustEffect() {
        super(EffectType.HARMFUL, 0x854f3a);
    }

    // Since the effect is timed by an event listener, we don't need continuous tick logic here.
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
