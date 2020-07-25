package com.github.hydos.ginger.engine.common.api.game;

import com.github.hydos.ginger.engine.common.cameras.Camera;
import com.github.hydos.ginger.engine.common.elements.GLGuiTexture;
import com.github.hydos.ginger.engine.common.elements.objects.GLRenderObject;
import com.github.hydos.ginger.engine.common.elements.objects.Light;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for storing essential engine game data so main class isn't messy
 * Also in general used with Game Class
 */
public class GameData {
    public List<GLGuiTexture> guis;
    public List<GLRenderObject> entities;
    public List<Light> lights;
    public List<GLRenderObject> normalMapEntities;
    public GLRenderObject playerObject;
    public Camera camera;
    public Vector4f clippingPlane;
    public boolean handleGuis = true;
    public int tickSpeed = 20;

    public GameData(GLRenderObject playerEntity, Camera camera, int tickSpeed) {
        clippingPlane = new Vector4f(0, -1, 0, 100000);
        guis = new ArrayList<GLGuiTexture>();
        entities = new ArrayList<GLRenderObject>();
        lights = new ArrayList<Light>();
        normalMapEntities = new ArrayList<GLRenderObject>();
        this.playerObject = playerEntity;
        this.camera = camera;
        this.tickSpeed = tickSpeed;
    }
}
