package com.github.hydos.ginger.engine.common.obj;

import com.github.hydos.ginger.engine.common.obj.shapes.StaticCube;
import com.github.hydos.ginger.engine.opengl.render.models.GLTexturedModel;
import com.github.hydos.ginger.engine.opengl.render.texture.ModelTexture;
import com.github.hydos.ginger.engine.opengl.utils.GLLoader;

public class ModelLoader {
    public static GLTexturedModel loadGenericCube(String cubeTexture) {
        Mesh data = StaticCube.getCube();
        GLTexturedModel tm = new GLTexturedModel(GLLoader.loadToVAO(data.getVertices(), data.getIndices(), data.getNormals(), data.getTextureCoords()), new ModelTexture(cubeTexture));
        return tm;
    }

    public static Mesh getCubeMesh() {
        return StaticCube.getCube();
    }

    public static GLTexturedModel loadModel(String objPath, String texturePath) {
        Mesh data = OBJFileLoader.loadModel(objPath);
        return new GLTexturedModel(GLLoader.loadToVAO(data.getVertices(), data.getIndices(), data.getNormals(), data.getTextureCoords()), new ModelTexture(texturePath));
    }

    public static Mesh loadMesh(String meshPath) {
        Mesh data = OBJFileLoader.loadModel(meshPath);
        return data;
    }
}
