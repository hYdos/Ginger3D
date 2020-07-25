package com.github.hydos.ginger.engine.opengl.render.models;

import com.github.hydos.ginger.engine.common.elements.objects.TexturedModel;
import com.github.hydos.ginger.engine.opengl.render.texture.ModelTexture;

public class GLTexturedModel extends TexturedModel {
    private RawModel rawModel;
    private ModelTexture texture;

    public GLTexturedModel(RawModel model, ModelTexture texture) {
        this.rawModel = model;
        this.texture = texture;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public ModelTexture getTexture() {
        return texture;
    }
}
