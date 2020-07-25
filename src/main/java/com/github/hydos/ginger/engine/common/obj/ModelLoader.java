package com.github.hydos.ginger.engine.common.obj;

import com.github.hydos.ginger.engine.common.obj.shapes.StaticCube;

public class ModelLoader {

    public static Mesh getCubeMesh() {
        return StaticCube.getCube();
    }

    public static Mesh loadMesh(String meshPath) {
        Mesh data = OBJFileLoader.loadModel(meshPath);
        return data;
    }
}
