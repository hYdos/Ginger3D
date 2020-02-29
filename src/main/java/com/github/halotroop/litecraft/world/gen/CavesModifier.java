package com.github.halotroop.litecraft.world.gen;

import java.util.Random;

import com.github.halotroop.litecraft.types.block.Blocks;
import com.github.halotroop.litecraft.util.noise.OctaveSimplexNoise;
import com.github.halotroop.litecraft.world.BlockAccess;
import com.github.halotroop.litecraft.world.gen.modifier.WorldModifier;

public class CavesModifier implements WorldModifier, WorldGenConstants
{
	private OctaveSimplexNoise caveNoise;
	private static final double THRESHOLD = 0.3;
	//
	@Override
	public void initialize(long seed)
	{
		Random rand = new Random(seed);
		this.caveNoise = new OctaveSimplexNoise(rand, 2, 45.0, 1.0, 1.0);
	}

	@Override
	public void modifyWorld(BlockAccess world, Random rand, int chunkStartX, int chunkStartY, int chunkStartZ)
	{
		final int subChunks = CHUNK_SIZE >> 2; // in 4x4x4 blocks

		for (int subChunkX = 0; subChunkX < subChunks; subChunkX++)
		{
			int scOffsetX = subChunkX << 2; // sub chunk offset x
			int scTotalX = scOffsetX + chunkStartX;
			for (int subChunkZ = 0; subChunkZ < subChunks; subChunkZ++)
			{
				int scOffsetZ = subChunkZ << 2; // sub chunk offset z
				int scTotalZ = scOffsetZ + chunkStartZ;
				for (int subChunkY = 0; subChunkY < subChunks; subChunkY++)
				{
					int scOffsetY = subChunkY << 2; // sub chunk offset y
					int scTotalY = scOffsetY + chunkStartY;
					// calculate noise at each corner of the cube [lower|upper][south|north][west|east]
					double noiseLSW = this.caveNoise.sample(subChunkX, subChunkY, subChunkZ); // base = lower south west
					double noiseUSW = this.caveNoise.sample(subChunkX, subChunkY + 1, subChunkZ);
					double noiseLNW = this.caveNoise.sample(subChunkX, subChunkY, subChunkZ + 1);
					double noiseUNW = this.caveNoise.sample(subChunkX, subChunkY + 1, subChunkZ + 1);
					double noiseLSE = this.caveNoise.sample(subChunkX + 1, subChunkY, subChunkZ);
					double noiseUSE = this.caveNoise.sample(subChunkX + 1, subChunkY + 1, subChunkZ);
					double noiseLNE = this.caveNoise.sample(subChunkX + 1, subChunkY, subChunkZ + 1);
					double noiseUNE = this.caveNoise.sample(subChunkX + 1, subChunkY + 1, subChunkZ + 1);
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
					for (int subY = 0; subY < 4; ++subY)
					{
						int totalY = subY + scTotalY;
						// calculate z lerp progresses
						double zpW = 0.25 * (yNW - ySW);
						double zpE = 0.25 * (yNE - ySE);
						// initial Z noises
						double zW = ySW;
						double zE = ySE;
						// loop over z, adding the progress each time
						for (int subZ = 0; subZ < 4; ++subZ)
						{
							int totalZ = subZ + scTotalZ;
							// calculate x lerp progress
							double lerpProg = 0.25 * (zE - zW);
							// initial x noise
							double lerpNoise = zW;
							// loop over x, adding the progress each time
							for (int subX = 0; subX < 4; ++subX)
							{
								int totalX = subX + scTotalX;
								// calculate whether to replace block with air
								// if the noise is within the threshold for caves
								if (-THRESHOLD < lerpNoise && lerpNoise < THRESHOLD)
								{
									// if the cave can carve into the block
									if (world.getBlock(totalX, totalY, totalZ).canCaveCarve())
									{
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