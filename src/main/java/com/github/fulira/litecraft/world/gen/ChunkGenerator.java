package com.github.fulira.litecraft.world.gen;

import com.github.fulira.litecraft.world.*;

public interface ChunkGenerator {
	Chunk generateChunk(World world, int chunkX, int chunkY, int chunkZ);
}
