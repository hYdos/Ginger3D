package com.github.hydos.ginger.engine.common.elements.objects;

import com.github.hydos.ginger.engine.common.elements.RenderObject;
import com.github.hydos.ginger.engine.opengl.render.models.GLTexturedModel;
import org.joml.Vector3f;

public class GLRenderObject extends RenderObject {
    private TexturedModel model;

    public GLRenderObject(GLTexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public TexturedModel getModel() {
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

}
