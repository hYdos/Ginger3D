package com.github.fulira.litecraft.world.dimension;

import com.github.fulira.litecraft.world.gen.EarthChunkGenerator;
import com.github.fulira.litecraft.world.gen.modifier.CavesModifier;

public final class Dimensions {
	public static final Dimension<EarthChunkGenerator> OVERWORLD = new EarthDimension(0, "earth")
			.addWorldModifier(new CavesModifier());
}
