package com.github.fulira.litecraft.world;

import com.github.fulira.litecraft.types.block.Block;

public interface BlockAccess {
	Block getBlock(int x, int y, int z);

	void setBlock(int x, int y, int z, Block block);
}