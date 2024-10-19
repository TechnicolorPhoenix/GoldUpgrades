package com.titanhex.thex.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler 
{
	//public static boolean alphaMessage = false;
	private static RayTraceResult getEntityMouseOverExtended(float reach)
	{
		RayTraceResult result = null;
		Minecraft mc = Minecraft.getInstance();
		Entity viewEntity = mc.getCameraEntity();
		
		if(viewEntity != null && mc.level != null)
		{
			double d0 = (double)reach;
			RayTraceResult rayTrace = viewEntity.pick(d0, 0.0f, false);
			Vector3d eyePos = viewEntity.getEyePosition(0);
			boolean flag = false;
			double d1 = d0;
			
			if(mc.gameMode.hasFarPickRange() && d1 < 6.0D)
			{
				d1 = 6.0D;
				d0 = d1;
			}
			else if(d0 > reach)
				flag = true;
			
			if(rayTrace != null)
				d1 = rayTrace.getLocation().distanceToSqr(eyePos);
			
			Vector3d lookVec = viewEntity.getViewVector(1.0f);
			Vector3d attackVec = eyePos.add(lookVec.x * d0, lookVec.y * d0, lookVec.z * d0);
			
			AxisAlignedBB expBounds = viewEntity.getBoundingBox().expandTowards(lookVec.scale(d0)).inflate(1.0D, 1.0D, 1.0D);
			EntityRayTraceResult entityRayTrace = ProjectileHelper.getEntityHitResult(viewEntity, eyePos, attackVec, expBounds, (entity) -> 
			{ 
				return !entity.isSpectator() && entity.canBeCollidedWith();
			}, d1);
			
			if(entityRayTrace != null)
			{
				Vector3d hitVec = entityRayTrace.getLocation();
				double d2 = eyePos.distanceToSqr(hitVec);
				if(flag && d2 > (reach * reach))
					result = BlockRayTraceResult.miss(hitVec, Direction.getNearest(lookVec.x, lookVec.y, lookVec.z), new BlockPos(hitVec));
				
				else if(d2 < d1 || result == null)
					result = entityRayTrace;
			}
			else
			{
				result = BlockRayTraceResult.miss(attackVec, Direction.getNearest(lookVec.x, lookVec.y, lookVec.z), new BlockPos(attackVec));
			}
		}
		
		return result;
	}
}
