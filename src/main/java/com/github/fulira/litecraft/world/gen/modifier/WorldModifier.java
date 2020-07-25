package com.github.fulira.litecraft.world.gen.modifier;

import com.github.fulira.litecraft.world.BlockAccess;

import java.util.Random;

public interface WorldModifier {
    void modifyWorld(BlockAccess world, Random rand, int chunkStartX, int chunkStartY, int chunkStartZ);

    void initialize(long seed);
}
