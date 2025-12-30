package com.titanhex.goldupgrades.item.interfaces;

public interface IJumpBoostArmor {
    double getJumpBoostModifier();

    default float getFallDamageReductionFraction() {
        return 0.0f;
    }
}
