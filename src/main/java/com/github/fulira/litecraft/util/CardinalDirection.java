package com.github.fulira.litecraft.util;

public enum CardinalDirection {
	NORTH(0, 1),
	EAST(1, 0),
	SOUTH(0, -1),
	WEST(-1, 0);

	private CardinalDirection(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public final int x, z;
}
