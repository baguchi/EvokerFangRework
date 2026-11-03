package baguchi.evoker_fang_rework.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EvokerFangs.class)
public abstract class EvokerFangsMixin extends Entity implements TraceableEntity {
    @Shadow
    private boolean clientSideAttackStarted;

    public EvokerFangsMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        super.tick();
        if (this.level().isClientSide()) {
            BlockPos pos = this.getOnPosLegacy();
            BlockState blockState = this.level().getBlockState(pos);

            for (int i = 0; i < 4; ++i) {
                double x = this.getX() + (this.random.nextDouble() * (double) 2.0F - (double) 1.0F) * (double) this.getBbWidth() * (double) 0.5F;
                double y = this.getY() + 0.05 + this.random.nextDouble();
                double z = this.getZ() + (this.random.nextDouble() * (double) 2.0F - (double) 1.0F) * (double) this.getBbWidth() * (double) 0.5F;
                double xd = (this.random.nextDouble() * (double) 2.0F - (double) 1.0F) * 0.3;
                double yd = 0.3 + this.random.nextDouble() * 0.3;
                double zd = (this.random.nextDouble() * (double) 2.0F - (double) 1.0F) * 0.3;
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK_CRUMBLE, blockState), x, y, z, xd, yd, zd);
            }

        }
    }
}
