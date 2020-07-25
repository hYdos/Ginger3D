package com.github.fulira.litecraft.world.gen;

import com.github.fulira.litecraft.types.block.Block;
import com.github.fulira.litecraft.types.block.Blocks;
import com.github.fulira.litecraft.util.noise.OctaveSimplexNoise;
import com.github.fulira.litecraft.world.Chunk;
import com.github.fulira.litecraft.world.World;

import java.util.Random;

public class EarthChunkGenerator implements ChunkGenerator, WorldGenConstants {
    private final OctaveSimplexNoise noise;
    private final OctaveSimplexNoise stoneNoise;
    private final int dimension;
    public EarthChunkGenerator(long seed, int dimension) {
        Random rand = new Random(seed);
        this.noise = new OctaveSimplexNoise(rand, 3, 250.0, 50.0, 18.0);
        this.stoneNoise = new OctaveSimplexNoise(rand, 1);
        this.dimension = dimension;
    }

    private static Block pickStone(double rockNoise) {
        if (rockNoise < -0.25) {
            return Blocks.ANDESITE;
        } else if (rockNoise < 0) {
            return Blocks.DIORITE;
        } else if (rockNoise < 0.25) {
            return Blocks.GNEISS;
        } else {
            return Blocks.GRANITE;
        }
    }

    @Override
    public Chunk generateChunk(World world, int chunkX, int chunkY, int chunkZ) {
        Chunk chunk = new Chunk(world, chunkX, chunkY, chunkZ, this.dimension);

        for (int x = 0; x < CHUNK_SIZE; x++) {
            double totalX = x + chunk.chunkStartX;

            for (int z = 0; z < CHUNK_SIZE; z++) {
                double totalZ = chunk.chunkStartZ + z;
                int height = (int) this.noise.sample(totalX, totalZ);
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    double rockNoise = this.stoneNoise.sample(totalX / 160.0, (chunk.chunkStartY + y) / 50.0,
                            totalZ / 160.0);
                    int totalY = chunk.chunkStartY + y;
                    Block block = Blocks.AIR;
                    if (totalY < height - 4)
                        block = pickStone(rockNoise);
                    else if (totalY < height - 1)
                        block = Blocks.DIRT;
                    else if (totalY < height)
                        block = Blocks.GRASS;
                    chunk.setBlock(x, y, z, block);
                }
            }
        }
        return chunk;
    }
}
