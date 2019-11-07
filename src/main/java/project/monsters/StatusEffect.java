package project.monsters;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Status effects can modify the stats of Monsters.
 * @see Monster
 */
@Entity
class StatusEffect {
    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    
    /**
     * An enum to categorize different types of status effects.
     */
    enum EffectType { Slow };

    /**
     * The type of status effect.
     */
    @NotNull
    private EffectType effectType;

    /**
     * The remaining duration of the status effects in number of frames.
     */
    @NotNull
    private int duration;

    /**
     * Default constructor for StatusEffect class.
     * @param effectType The type of effect.
     * @param duration The duration of the effect in number of frames.
     */
    StatusEffect(@NonNull EffectType effectType, int duration) {
        this.effectType = effectType;
        this.duration = duration;
    }

    /**
     * Copy constructor for StatusEffect class.
     * @param other The other object to copy from.
     */
    StatusEffect(StatusEffect other) {
        this.effectType = other.effectType;
        this.duration = other.duration;
    }

    /**
     * Counts down the status effect by one frame.
     */
    void countDown() { duration -= 1; }
}