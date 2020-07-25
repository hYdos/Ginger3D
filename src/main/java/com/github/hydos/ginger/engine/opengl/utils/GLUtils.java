package com.github.hydos.ginger.engine.opengl.utils;

import com.github.hydos.ginger.engine.common.font.TextMaster;
import com.github.hydos.ginger.engine.common.obj.ModelLoader;
import com.github.hydos.ginger.engine.common.obj.normals.NormalMappedObjLoader;
import com.github.hydos.ginger.engine.opengl.render.GLRenderManager;
import com.github.hydos.ginger.engine.opengl.render.models.GLTexturedModel;
import com.github.hydos.ginger.engine.opengl.render.models.RawModel;
import com.github.hydos.ginger.engine.opengl.render.texture.ModelTexture;

public class GLUtils {
    public static GLTexturedModel createTexturedModel(String texturePath, String modelPath) {
        GLTexturedModel model = ModelLoader.loadModel(modelPath, texturePath);
        return model;
    }

    public static GLTexturedModel createTexturedModel(String texturePath, String modelPath, String normalMapPath) {
        RawModel model = NormalMappedObjLoader.loadOBJ(modelPath);
        GLTexturedModel texturedModel = new GLTexturedModel(model, new ModelTexture(texturePath));
        return texturedModel;
    }

    public static void init() {
        TextMaster.init();
    }

    public static void preRenderScene(GLRenderManager renderer) {
    }

    public static void update() {
    }
}
