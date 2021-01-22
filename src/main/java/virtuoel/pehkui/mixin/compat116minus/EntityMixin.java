package virtuoel.pehkui.mixin.compat116minus;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(Entity.class)
public abstract class EntityMixin
{
	@Shadow World world;
	@Shadow @Final @Mutable EntityType<?> type;
	@Shadow abstract void move(MovementType type, Vec3d movement);
	
	@Inject(method = "calculateDimensions", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/entity/Entity;setBoundingBox(Lnet/minecraft/util/math/Box;)V"))
	private void onCalculateDimensions(CallbackInfo info, EntityDimensions previous, EntityPose pose, EntityDimensions current, Box box)
	{
		if (this.world.isClient && type == EntityType.PLAYER && current.width > previous.width)
		{
			final float scale = ScaleUtils.getWidthScale(this);
			final float dist = (previous.width - current.width) / 2.0F;
			
			move(MovementType.SELF, new Vec3d(dist / scale, 0.0D, dist / scale));
		}
	}
}
