package com.github.hydos.ginger;

import com.github.hydos.ginger.engine.vulkan.VKVariables;
import com.github.hydos.ginger.engine.vulkan.elements.VKRenderObject;
import com.github.hydos.ginger.engine.vulkan.render.*;
import com.github.hydos.ginger.engine.vulkan.render.renderers.EntityRenderer;

public class GingerVK {
	private static GingerVK INSTANCE;

	public EntityRenderer entityRenderer;

	public static void init() {
		INSTANCE = new GingerVK();
		VKVariables.renderManager = new VKRenderManager();
	}

	public static GingerVK getInstance() {
		return INSTANCE;
	}

	public void createRenderers() {
		entityRenderer = new EntityRenderer();
		VKVariables.renderManager.addRenderer(entityRenderer);
	}

	public void cleanup() {
		for (VKRenderObject entity : entityRenderer.entities) {
			VKBufferMesh bufferMesh = entity.getModel();
			bufferMesh.cleanup();
		}
	}

}
