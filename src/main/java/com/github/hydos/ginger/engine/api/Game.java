package com.github.hydos.ginger.engine.api;

public abstract class Game {
	
	public GameData data;
	
	public Game() {}
	
	public abstract void update();

	public abstract void exit();

}
