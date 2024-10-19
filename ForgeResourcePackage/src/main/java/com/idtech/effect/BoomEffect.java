package com.idtech.effect;

import com.idtech.Utils;
import net.minecraft.world.effect.AttackDamageMobEffect;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class BoomEffect extends InstantenousMobEffect {

    public BoomEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level.isClientSide()){
            Utils.createExplosion(pLivingEntity.level, pLivingEntity.blockPosition(), pAmplifier);
        }

        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity p_19462_, @Nullable Entity p_19463_, LivingEntity p_19464_, int p_19465_, double p_19466_) {
        super.applyInstantenousEffect(p_19462_, p_19463_, p_19464_, p_19465_, p_19466_);
    }
}
