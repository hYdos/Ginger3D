package com.github.hydos.ginger.main;

import com.github.hydos.ginger.engine.font.TextMaster;
import com.github.hydos.ginger.engine.obj.ModelLoader;
import com.github.hydos.ginger.engine.obj.normals.NormalMappedObjLoader;
import com.github.hydos.ginger.engine.render.MasterRenderer;
import com.github.hydos.ginger.engine.render.models.*;
import com.github.hydos.ginger.engine.render.texture.ModelTexture;

public class GingerMain {
		
	public static void init() {
        TextMaster.init();
	}
	
	public static TexturedModel createTexturedModel(String texturePath, String modelPath) {
		TexturedModel model = ModelLoader.loadModel(modelPath, texturePath);
		return model;
	}
	
	public static TexturedModel createTexturedModel(String texturePath, String modelPath, String normalMapPath) {
		RawModel model = NormalMappedObjLoader.loadOBJ(modelPath);
		TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(texturePath));
		return texturedModel;
	}
	
	public static void update() {
	}
	
	public static void preRenderScene(MasterRenderer renderer) {
	}
}
