package com.github.fulira.litecraft.world.gen.modifier;

import java.util.Random;

import com.github.fulira.litecraft.types.block.Block;
import com.github.fulira.litecraft.world.BlockAccess;
import com.github.fulira.litecraft.world.gen.WorldGenConstants;

public class OreModifier implements WorldModifier, WorldGenConstants {
	public OreModifier(int minY, int maxY, int boxSize, int veinSizeMin, int veinSizeMax, Block ore, OreStarter veinStarter) {
		this.minY = minY;
		this.maxY = maxY;
		this.boxSize = boxSize;
		this.veinSizeMin = veinSizeMin;
		this.veinSizeDelta = veinSizeMax - veinSizeMin;
		this.ore = ore;
		this.veinStarter = veinStarter;
	}

	private final int minY, maxY, boxSize, veinSizeMin, veinSizeDelta;
	private final OreStarter veinStarter;
	private final Block ore;

	@Override
	public void initialize(long seed) {
	}

	@Override
	public void modifyWorld(BlockAccess world, Random rand, int chunkStartX, int chunkStartY, int chunkStartZ) {
		final int chunkEndY = chunkStartY + MAX_POS;
		if (chunkStartY < this.maxY || chunkEndY > this.minY) {
			return;
		}

		int veinsToGenerate = this.veinStarter.veinCount(chunkStartX, chunkStartY, chunkStartZ, rand);

		// loop down to zero
		while (veinsToGenerate --> 0) {
			int startY = rand.nextInt(CHUNK_SIZE) + chunkStartY;

			if (startY > this.maxY) {
				startY = this.maxY;
			} else if (startY < this.minY) {
				startY = this.minY;
			}

			int startX = rand.nextInt(CHUNK_SIZE) + chunkStartX;
			int startZ = rand.nextInt(CHUNK_SIZE) + chunkStartZ;

			this.generateVein(world, startX, startY, startZ, rand);
		}
	}

	private void generateVein(BlockAccess world, int startX, int startY, int startZ, Random rand) {
		final int veinSize = rand.nextInt(this.veinSizeDelta) + this.veinSizeMin;

		int randBound = this.boxSize * this.boxSize * this.boxSize - veinSize;
		int blocksToPlace = veinSize;

		for (int xOffset = 0; xOffset < this.boxSize; ++xOffset) {
			int x = startX + xOffset;
	
			for (int yOffset = 0; yOffset < this.boxSize; ++yOffset) {
				int y = startY + yOffset;

				for (int zOffset = 0; zOffset < this.boxSize; ++zOffset) {
					int z = startZ + zOffset;

					if (rand.nextInt(randBound--) == 0) {
						world.setBlock(x, y, z, this.ore);
						blocksToPlace--;
						randBound++;
					}

					if (blocksToPlace == 0) {
						return;
					}
				}
			}
		}
	}

	@FunctionalInterface
	public static interface OreStarter {
		public int veinCount(int chunkStartX, int chunkStartY, int chunkStartZ, Random rand);
	}
}
