package com.github.fulira.litecraft.world.dimension;

import com.github.fulira.litecraft.world.gen.ChunkGenerator;
import com.github.fulira.litecraft.world.gen.modifier.WorldModifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.ArrayList;
import java.util.List;

public abstract class Dimension<T extends ChunkGenerator> {
    private static final Int2ObjectMap<Dimension<?>> ID_TO_DIMENSION = new Int2ObjectArrayMap<>();
    public final int id;
    public final String saveIdentifier;
    public List<WorldModifier> worldModifiers = new ArrayList<>();

    public Dimension(int id, String saveIdentifier) {
        this.id = id;
        this.saveIdentifier = saveIdentifier;
        ID_TO_DIMENSION.put(id, this);
    }

    public static Dimension<?> getById(int id) {
        return ID_TO_DIMENSION.get(id);
    }

    public Dimension<T> addWorldModifier(WorldModifier modifier) {
        this.worldModifiers.add(modifier);
        return this;
    }

    public WorldModifier[] getWorldModifierArray() {
        return this.worldModifiers.toArray(new WorldModifier[0]);
    }

    public abstract T createChunkGenerator(long seed);
}
