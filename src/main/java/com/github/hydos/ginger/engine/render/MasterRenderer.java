package com.github.hydos.ginger.engine.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.github.hydos.ginger.engine.cameras.Camera;
import com.github.hydos.ginger.engine.elements.GuiTexture;
import com.github.hydos.ginger.engine.elements.objects.*;
import com.github.hydos.ginger.engine.io.Window;
import com.github.hydos.ginger.engine.math.matrixes.Matrix4f;
import com.github.hydos.ginger.engine.render.models.TexturedModel;
import com.github.hydos.ginger.engine.render.renderers.*;
import com.github.hydos.ginger.engine.render.shaders.*;
import com.github.hydos.ginger.engine.shadow.ShadowMapMasterRenderer;
import com.github.hydos.ginger.engine.terrain.Terrain;

public class MasterRenderer {
	
	private StaticShader entityShader;
	private EntityRenderer entityRenderer;
	
	private TerrainShader terrainShader;
	private TerrainRenderer terrainRenderer;
	
	private GuiShader guiShader;
	private GuiRenderer guiRenderer;
	
	private SkyboxRenderer skyboxRenderer;
	
	private NormalMappingRenderer normalRenderer;
			
	private Matrix4f projectionMatrix;
	
	private ShadowMapMasterRenderer shadowMapRenderer;
	
	private Map<TexturedModel, List<RenderObject>> entities = new HashMap<TexturedModel, List<RenderObject>>();
	private Map<TexturedModel, List<RenderObject>> normalMapEntities = new HashMap<TexturedModel, List<RenderObject>>();
	
	public static final float FOV = 80f;
	public static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000f;
	
	public MasterRenderer(Camera camera) {
		createProjectionMatrix();
		entityShader = new StaticShader();
		entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
		
		skyboxRenderer = new SkyboxRenderer(projectionMatrix);
		
		guiShader = new GuiShader();
		guiRenderer = new GuiRenderer(guiShader);
		
		normalRenderer = new NormalMappingRenderer(projectionMatrix);
		
		terrainShader = new TerrainShader();
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		
		shadowMapRenderer = new ShadowMapMasterRenderer(camera);

	}
	
	public static void enableCulling() {
//		GL11.glEnable(GL11.GL_CULL_FACE);
//		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void prepare() {
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapRenderer.getShadowMap());
	}
	
	public void renderScene(List<RenderObject> entities, List<RenderObject> normalEntities, List<Terrain> terrains, List<Light> lights, Camera camera, Vector4f clipPlane) {
		prepare();
		renderEntities(entities, camera, lights);
		renderNormalEntities(normalEntities, lights, camera, clipPlane);
		renderTerrains(terrains, lights, camera);

		skyboxRenderer.render(camera);
		
	}
	
	private void renderNormalEntities(List<RenderObject> normalEntities, List<Light> lights, Camera camera, Vector4f clipPlane) {
		for(RenderObject entity: normalEntities) {
			processEntityWithNormal(entity);
		}
		normalRenderer.render(normalMapEntities, clipPlane, lights, camera);
	}

	public void renderGuis(List<GuiTexture> guis) {
		guiRenderer.render(guis);
	}
	
	private void renderTerrains(List<Terrain> terrains, List<Light> lights, Camera camera) {
		terrainShader.start();
		terrainShader.loadSkyColour(Window.getColour());
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();		
	}

	private void renderEntities(List<RenderObject> entities, Camera camera, List<Light> lights) {
		for(RenderObject entity: entities) {
			processEntity(entity);
		}
		entityRenderer.prepare();
		entityShader.start();
		entityShader.loadSkyColour(Window.getColour());
		entityShader.loadLights(lights);
		entityShader.loadViewMatrix(camera);
		entityRenderer.render(this.entities);		
		entityShader.stop();
		this.entities.clear();
	}
	
	private void processEntity(RenderObject entity) {
		TexturedModel entityModel = entity.getModel();
		List<RenderObject> batch = entities.get(entityModel);
		if(batch!=null) {
			batch.add(entity);
		}else {
			List<RenderObject> newBatch = new ArrayList<RenderObject>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	private void processEntityWithNormal(RenderObject entity) {
		TexturedModel entityModel = entity.getModel();
		List<RenderObject> batch = normalMapEntities.get(entityModel);
		if(batch!=null) {
			batch.add(entity);
		}else {
			List<RenderObject> newBatch = new ArrayList<RenderObject>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
	}
	
	public void renderShadowMap(List<RenderObject> entityList, Light sun) {
		for(RenderObject entity : entityList) {
			processEntity(entity);
		}
		shadowMapRenderer.render(entities, sun);
		entities.clear();
	}
	
	public int getShadowMapTexture() {
		return shadowMapRenderer.getShadowMap();
	}
	
	public void cleanUp() {
		entityShader.cleanUp();
		terrainShader.cleanUp();
		guiRenderer.cleanUp();
		shadowMapRenderer.cleanUp();
		normalRenderer.cleanUp();
		
	}
	
	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}
	
    private void createProjectionMatrix(){
    	projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Window.width / (float) Window.height;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
    }

	public void renderGui(GuiTexture guiTexture) {
		List<GuiTexture> texture = new ArrayList<GuiTexture>();
		texture.add(guiTexture);
		guiRenderer.render(texture);
	}

	public void renderSceneNoTerrain(List<RenderObject> entities, List<RenderObject> normalEntities, List<Light> lights, Camera camera, Vector4f clipPlane) {

		prepare();
		renderEntities(entities, camera, lights);
		renderNormalEntities(normalEntities, lights, camera, clipPlane);

		skyboxRenderer.render(camera);
		
	}
}
