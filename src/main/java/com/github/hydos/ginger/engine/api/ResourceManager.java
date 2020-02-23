package com.github.hydos.ginger.engine.api;

/*
 * make your own resource manager if you want!
 */
public abstract class ResourceManager {
	
	public abstract String getResourcePath(String path);
	
	public abstract boolean getResourceInternally(String path);
	
}
