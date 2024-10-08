package project.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Status effects can modify the stats of {@link Monster}s.
 * Note, when applied, status effects only start to take effect and count down on the next frame.
 */
@Entity(name="StatusEffect")
public class StatusEffect {
    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The multiplier by which the speed is multiplied when a monster is slowed.
     */
    public static double SLOW_MULTIPLIER = 0.2;
    
    /**
     * An enum to categorize different types of status effects.
     */
    public enum EffectType { Slow };

    /**
     * The type of status effect.
     */
    @NotNull
    private EffectType effectType;

    /**
     * The remaining duration of the status effects in number of frames.
     */
    private int duration;

    /**
     * Default constructor.
     */
    public StatusEffect() {}

    /**
     * Default constructor for StatusEffect class.
     * @param effectType The type of effect.
     * @param duration The duration of the effect in number of frames, which should be greater than zero.
     */
    public StatusEffect(EffectType effectType, int duration) {
        if (duration <= 0) throw new IllegalArgumentException("Duration should be greater than zero.");
        this.effectType = effectType;
        this.duration = duration;
    }

    /**
     * Copy constructor for StatusEffect class.
     * @param other The other object to copy from.
     */
    public StatusEffect(StatusEffect other) {
        this.effectType = other.effectType;
        this.duration = other.duration;
    }

    /**
     * Accesses the type of the status effect.
     * @return The type of the status effect.
     */
    public EffectType getEffectType() { return effectType; }

    /**
     * Accesses the remaining duration of the status effect.
     * @return The remaining duration of the status effect.
     */
    public int getDuration() { return duration; }

    /**
     * Counts down the status effect by one frame.
     */
    public void countDown() { duration -= 1; }
}