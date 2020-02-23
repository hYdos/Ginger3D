package com.github.hydos.ginger.engine.api;

import java.util.*;

import org.joml.Vector4f;

import com.github.hydos.ginger.engine.cameras.Camera;
import com.github.hydos.ginger.engine.elements.GuiTexture;
import com.github.hydos.ginger.engine.elements.objects.*;
import com.github.hydos.ginger.engine.terrain.Terrain;

/*
 * Used for storing essential engine game data so main class isn't messy
 * Also in general used with Game Class
 */
public class GameData {
	
	public List<GuiTexture> guis;
	public List<RenderObject> entities;
	public List<Light> lights;
	public List<RenderObject> normalMapEntities;
	public List<Terrain> flatTerrains;
	public Player player;
	public Camera camera;
	public Vector4f clippingPlane;
	public boolean handleGuis = true;
	
	public GameData(Player player, Camera camera) {
		clippingPlane = new Vector4f(0, -1, 0, 100000);
		guis = new ArrayList<GuiTexture>();
		entities = new ArrayList<RenderObject>();
		lights = new ArrayList<Light>();
		normalMapEntities = new ArrayList<RenderObject>();
		flatTerrains = new ArrayList<Terrain>();
		this.player = player;
		this.camera = camera;
	}

}
