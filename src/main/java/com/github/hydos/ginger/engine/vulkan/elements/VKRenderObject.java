package com.github.hydos.ginger.engine.vulkan.elements;

import com.github.hydos.ginger.engine.common.elements.RenderObject;
import com.github.hydos.ginger.engine.vulkan.model.VKModelLoader.VKMesh;
import com.github.hydos.ginger.engine.vulkan.render.VKBufferMesh;
import org.joml.Vector3f;

import java.util.ArrayList;

public class VKRenderObject extends RenderObject {

    public ArrayList<Long> descriptorSets;
    private VKBufferMesh model = null;//until i add VKTextured models
    private VKMesh rawModel;//until i add VKTextured models

    public VKRenderObject(VKMesh rawModel, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
        this.rawModel = rawModel;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public VKBufferMesh getModel() {
        return model;
    }

    public void setModel(VKBufferMesh model) {
        this.model = model;
    }

    public VKMesh getRawModel() {
        return rawModel;
    }

}
