package baguchi.evoker_fang_rework.entity.goal;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public abstract class EvokerUseSpellGoal extends Goal {
    protected int attackWarmupDelay;
    protected int nextAttackTickCount;
    public final Evoker evoker;

    public EvokerUseSpellGoal(Evoker evoker) {
        super();
        this.evoker = evoker;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.evoker.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        } else {
            return this.evoker.isCastingSpell() ? false : this.evoker.tickCount >= this.nextAttackTickCount;
        }
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.evoker.getTarget();
        return target != null && target.isAlive() && this.attackWarmupDelay > 0;
    }

    @Override
    public void start() {
        this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
        this.evoker.spellCastingTickCount = this.getCastingTime();
        this.nextAttackTickCount = this.evoker.tickCount + this.getCastingInterval();
        SoundEvent spellPrepareSound = this.getSpellPrepareSound();
        if (spellPrepareSound != null) {
            this.evoker.playSound(spellPrepareSound, 1.0F, 1.0F);
        }

        this.evoker.setIsCastingSpell(this.getSpell());
    }

    @Override
    public void tick() {
        this.attackWarmupDelay--;
        if (this.attackWarmupDelay == 0) {
            this.performSpellCasting();
            this.evoker.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
        }
    }

    protected abstract void performSpellCasting();

    protected int getCastWarmupTime() {
        return 20;
    }

    protected abstract int getCastingTime();

    protected abstract int getCastingInterval();

    protected abstract @Nullable SoundEvent getSpellPrepareSound();

    protected abstract SpellcasterIllager.IllagerSpell getSpell();
}