package project.arena.monsters;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.ArenaInstance;
import project.arena.Coordinates;

/**
 * Stub that is a basic implementation of {@link Monster}.
 */
class TestMonster extends Monster {
    TestMonster(ArenaInstance arena, Coordinates start, Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, imageView, difficulty);
    }

    TestMonster(ArenaInstance arena, TestMonster other) {
        super(arena, other);
    }

    @Override
    public String getClassName() { return "TestMonster"; }

    @Override
    public TestMonster deepCopy(ArenaInstance arena) { return new TestMonster(arena, this); }
}