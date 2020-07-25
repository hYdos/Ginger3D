package com.github.fulira.litecraft.world.gen.modifier;

import com.github.fulira.litecraft.types.block.Block;
import com.github.fulira.litecraft.types.block.Blocks;
import com.github.fulira.litecraft.util.CardinalDirection;
import com.github.fulira.litecraft.util.noise.OctaveSimplexNoise;
import com.github.fulira.litecraft.world.BlockAccess;
import com.github.fulira.litecraft.world.gen.WorldGenConstants;

import java.util.Random;

public class CavesModifier implements WorldModifier, WorldGenConstants {
    private OctaveSimplexNoise caveNoise;

    @Override
    public void initialize(long seed) {
        Random rand = new Random(seed);
        this.caveNoise = new OctaveSimplexNoise(rand, 2, 65.0, 1.0, 1.0);
    }

    @Override
    public void modifyWorld(BlockAccess world, Random rand, int chunkStartX, int chunkStartY, int chunkStartZ) {
        final int subChunks = CHUNK_SIZE >> 2; // in 4x4x4 blocks

        for (int subChunkX = 0; subChunkX < subChunks; subChunkX++) {
            int scOffsetX = subChunkX << 2; // sub chunk offset x
            int scTotalX = scOffsetX + chunkStartX;
            for (int subChunkZ = 0; subChunkZ < subChunks; subChunkZ++) {
                int scOffsetZ = subChunkZ << 2; // sub chunk offset z
                int scTotalZ = scOffsetZ + chunkStartZ;
                for (int subChunkY = 0; subChunkY < subChunks; subChunkY++) {
                    int scOffsetY = subChunkY << 2; // sub chunk offset y
                    int scTotalY = scOffsetY + chunkStartY;
                    double scSampleY = (double) scTotalY * 1.5; // squish caves along y axis a bit
                    double scUpperYOffset = 4.0 * 1.5;
                    // calculate noise at each corner of the cube
                    // [lower|upper][south|north][west|east]
                    double noiseLSW = this.caveNoise.sample(scTotalX, scSampleY, scTotalZ); // base = lower south west
                    double noiseUSW = this.caveNoise.sample(scTotalX, scSampleY + scUpperYOffset, scTotalZ);
                    double noiseLNW = this.caveNoise.sample(scTotalX, scSampleY, scTotalZ + 4);
                    double noiseUNW = this.caveNoise.sample(scTotalX, scSampleY + scUpperYOffset, scTotalZ + 4);
                    double noiseLSE = this.caveNoise.sample(scTotalX + 4, scSampleY, scTotalZ);
                    double noiseUSE = this.caveNoise.sample(scTotalX + 4, scSampleY + scUpperYOffset, scTotalZ);
                    double noiseLNE = this.caveNoise.sample(scTotalX + 4, scSampleY, scTotalZ + 4);
                    double noiseUNE = this.caveNoise.sample(scTotalX + 4, scSampleY + scUpperYOffset, scTotalZ + 4);
                    // calculate y lerp progresses
                    // lerp = low + progress * (high - low)
                    double ypSW = 0.25 * (noiseUSW - noiseLSW);
                    double ypNW = 0.25 * (noiseUNW - noiseLNW);
                    double ypSE = 0.25 * (noiseUSE - noiseLSE);
                    double ypNE = 0.25 * (noiseUNE - noiseLNE);
                    // initial Y noises
                    double ySW = noiseLSW;
                    double ySE = noiseLSE;
                    double yNW = noiseLNW;
                    double yNE = noiseLNE;
                    // loop over y, adding the progress each time
                    for (int subY = 0; subY < 4; ++subY) {
                        int totalY = subY + scTotalY;
                        // calculate z lerp progresses
                        double zpW = 0.25 * (yNW - ySW);
                        double zpE = 0.25 * (yNE - ySE);
                        // initial Z noises
                        double zW = ySW;
                        double zE = ySE;
                        // loop over z, adding the progress each time
                        for (int subZ = 0; subZ < 4; ++subZ) {
                            int totalZ = subZ + scTotalZ;
                            // calculate x lerp progress
                            double lerpProg = 0.25 * (zE - zW);
                            // initial x noise
                            double lerpNoise = zW;
                            // loop over x, adding the progress each time
                            for (int subX = 0; subX < 4; ++subX) {
                                int totalX = subX + scTotalX;
                                // calculate whether to replace block with air
                                // if the noise is within the threshold for that block for caves
                                float threshold = world.getBlock(totalX, totalY, totalZ).getCaveCarveThreshold();

                                if (-threshold < lerpNoise && lerpNoise < threshold) {
                                    boolean canGenerate = false;

                                    // check for air above. if there is air above, check threshold of nearby blocks to try prevent bad holes
                                    // inb4 this makes caves generate differently based on how the chunk is loaded
                                    // maybe I should change GenerationWorld slightly
                                    if (world.getBlock(totalX, totalY + 1, totalZ) == Blocks.AIR) {
                                        for (CardinalDirection direction : CardinalDirection.values()) {
                                            Block block2 = world.getBlock(totalX + direction.x, totalY, totalZ + direction.z);

                                            if (block2 == Blocks.AIR) {
                                                canGenerate = true;
                                            } else {
                                                float threshold2 = block2.getCaveCarveThreshold();

                                                // check threshold of nearby block against the threshold of this position
                                                if (-threshold2 < lerpNoise && lerpNoise < threshold2) {
                                                    canGenerate = true;
                                                }
                                            }

                                            if (canGenerate) {
                                                world.setBlock(totalX, totalY, totalZ, Blocks.AIR);
                                                break;
                                            }
                                        }
                                    } else { // if it's not an area with air above, no need to check
                                        world.setBlock(totalX, totalY, totalZ, Blocks.AIR);
                                    }
                                }
                                // add progress to the noise
                                lerpNoise += lerpProg;
                            }
                            // add z progresses
                            zW += zpW;
                            zE += zpE;
                        }
                        // add y progresses
                        ySW += ypSW;
                        ySE += ypSE;
                        yNW += ypNW;
                        yNE += ypNE;
                    }
                }
            }
        }
    }
}
