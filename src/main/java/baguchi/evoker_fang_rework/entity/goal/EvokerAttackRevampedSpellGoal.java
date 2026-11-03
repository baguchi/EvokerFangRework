package baguchi.evoker_fang_rework.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EvokerAttackRevampedSpellGoal extends EvokerUseSpellGoal {
    public final Evoker evoker;
    public int checkingTick;
    public boolean checkEnemyNear;


    public EvokerAttackRevampedSpellGoal(Evoker evoker) {
        super(evoker);
        this.evoker = evoker;
    }


    @Override
    public boolean canUse() {
        LivingEntity target = this.evoker.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        } else {
            if(this.checkEnemyNear && this.checkingTick > 0){
                this.checkingTick--;
            }else if(this.checkEnemyNear){
                this.checkEnemyNear = false;
            }
            return super.canUse() || this.checkEnemyNear && this.evoker.distanceToSqr(target) < (double)9.0F;
        }
    }

    protected int getCastingTime() {
        return 40;
    }

    protected int getCastingInterval() {
        return 100;
    }

    protected void performSpellCasting() {
        LivingEntity target = this.evoker.getTarget();
        double minY = Math.min(target.getY(), this.evoker.getY());
        double maxY = Math.max(target.getY(), this.evoker.getY()) + (double)1.0F;
        float angleTowardsTarget = (float) Mth.atan2(target.getZ() - this.evoker.getZ(), target.getX() - this.evoker.getX());
        if (this.evoker.distanceToSqr(target) < (double)9.0F || this.checkEnemyNear) {
            int extraDelay = this.checkEnemyNear ? 6: 0;

            for(int i = 0; i < 5; ++i) {
                float angle = angleTowardsTarget + (float)i * (float)Math.PI * 0.4F;
                this.createSpellEntity(this.evoker.getX() + (double)Mth.cos((double)angle) * (double)1.5F, this.evoker.getZ() + (double)Mth.sin((double)angle) * (double)1.5F, minY, maxY, angle, extraDelay);
            }

            for(int i = 0; i < 8; ++i) {
                float angle = angleTowardsTarget + (float)i * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                this.createSpellEntity(this.evoker.getX() + (double)Mth.cos((double)angle) * (double)2.5F, this.evoker.getZ() + (double)Mth.sin((double)angle) * (double)2.5F, minY, maxY, angle, 3 + extraDelay);
            }
            if(this.checkEnemyNear){
                this.checkEnemyNear = false;
            }
        } else {
            for(int i = 0; i < 16; ++i) {
                double reach = (double)1.25F * (double)(i + 1);
                int spellSpeed = 1 * i;
                this.createSpellEntity(this.evoker.getX() + (double)Mth.cos((double)angleTowardsTarget) * reach, this.evoker.getZ() + (double)Mth.sin((double)angleTowardsTarget) * reach, minY, maxY, angleTowardsTarget, spellSpeed);
            }
            this.checkEnemyNear = true;
            this.checkingTick = 40;
        }

    }

    private void createSpellEntity(double x, double z, double minY, double maxY, float angle, int delayTicks) {
        BlockPos pos = BlockPos.containing(x, maxY, z);
        boolean success = false;
        double topOffset = (double)0.0F;

        do {
            BlockPos below = pos.below();
            BlockState belowState = this.evoker.level().getBlockState(below);
            if (belowState.isFaceSturdy(this.evoker.level(), below, Direction.UP)) {
                if (!this.evoker.level().isEmptyBlock(pos)) {
                    BlockState blockState = this.evoker.level().getBlockState(pos);
                    VoxelShape shape = blockState.getCollisionShape(this.evoker.level(), pos);
                    if (!shape.isEmpty()) {
                        topOffset = shape.max(Direction.Axis.Y);
                    }
                }

                success = true;
                break;
            }

            pos = pos.below();
        } while(pos.getY() >= Mth.floor(minY) - 1);

        if (success) {
            this.evoker.level().addFreshEntity(new EvokerFangs(this.evoker.level(), x, (double)pos.getY() + topOffset, z, angle, delayTicks, this.evoker));
            this.evoker.level().gameEvent(GameEvent.ENTITY_PLACE, new Vec3(x, (double)pos.getY() + topOffset, z), GameEvent.Context.of(this.evoker));
        }

    }

    protected SoundEvent getSpellPrepareSound() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    protected SpellcasterIllager.IllagerSpell getSpell() {
        return SpellcasterIllager.IllagerSpell.FANGS;
    }
}