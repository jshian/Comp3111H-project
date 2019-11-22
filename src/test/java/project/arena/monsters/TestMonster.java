package project.arena.monsters;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.Arena;
import project.arena.Coordinates;

/**
 * Stub that is a basic implementation of {@link Monster}.
 */
class TestMonster extends Monster {
    TestMonster(@NonNull Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, difficulty);
        this.imageView = imageView;
        this.coordinates.bindByImage(imageView);
    }

    TestMonster(@NonNull Arena arena, @NonNull TestMonster other) {
        super(arena, other);
    }

    @Override
    public String getClassName() { return "TestMonster"; }

    @Override
    public TestMonster deepCopy(@NonNull Arena arena) { return new TestMonster(arena, this); }

    @Override
    public void loadImage() {};
}