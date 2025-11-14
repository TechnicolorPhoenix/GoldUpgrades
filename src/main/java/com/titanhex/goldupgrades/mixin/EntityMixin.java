package com.titanhex.goldupgrades.mixin;

import com.titanhex.goldupgrades.attribute.ModAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Entity.class)
public abstract class EntityMixin {
//
//    /**
//     * @author GoldUpgrades
//     * @reason Replace the default hardcoded air supply of 300 with the
//     * MAX_OXYGEN attribute value for players.
//     * @param original The original constant value (expected to be 300).
//     * @return The new max air value or the original constant.
//     */
//    @ModifyConstant(
//            method = "getMaxAirSupply",
//            constant = @Constant(intValue = 300)
//    )
//    private int modifyMaxAirSupply(int original) {
//        Object entity = (Object) this;
//
//        if (entity instanceof PlayerEntity) {
//            PlayerEntity targetEntity = (PlayerEntity) (Object) this;
//
//            AttributeModifierManager attributeManager = targetEntity.getAttributes();
//
//            if (attributeManager != null) {
//                ModifiableAttributeInstance maxOxygen = targetEntity.getAttribute(ModAttributes.MAX_OXYGEN);
//
//                if (maxOxygen != null) {
//                    return (int) maxOxygen.getValue();
//                }
//            }
//        }
//
//        return original;
//    }
}