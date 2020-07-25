package com.github.hydos.ginger.engine.opengl.api;

/**
 * make your own resource manager if you want!
 */
public abstract class ResourceManager {
    public abstract boolean getResourceInternally(String path);

    public abstract String getResourcePath(String path);
}
