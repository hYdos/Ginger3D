package com.github.fulira.litecraft.types.block;

import org.joml.Vector3f;

import com.github.fulira.litecraft.world.Chunk;
import com.github.hydos.ginger.engine.common.elements.objects.GLRenderObject;

public class BlockInstance extends GLRenderObject
{
	public BlockInstance(Block block, Vector3f position)
	{ super(block.model, position, 0, 0, 0, new Vector3f(1f, 1f, 1f)); }

	public void processCulling(Chunk chunk)
	{
		Vector3f southNeighbourBlockLocation = this.getPosition();
		southNeighbourBlockLocation.x--;
	}
}
