package com.github.fulira.litecraft.world.gen.modifier;

import java.util.Random;

import com.github.fulira.litecraft.world.BlockAccess;

public interface WorldModifier {
	void modifyWorld(BlockAccess world, Random rand, int chunkStartX, int chunkStartY, int chunkStartZ);

	void initialize(long seed);
}
