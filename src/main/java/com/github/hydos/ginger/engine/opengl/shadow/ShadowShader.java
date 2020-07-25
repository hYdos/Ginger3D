package com.github.hydos.ginger.engine.opengl.shadow;

import com.github.hydos.ginger.engine.opengl.render.shaders.ShaderProgram;
import org.joml.Matrix4f;

public class ShadowShader extends ShaderProgram {
    private static final String VERTEX_FILE = "shadowVertexShader.glsl";
    private static final String FRAGMENT_FILE = "shadowFragmentShader.glsl";
    private int location_mvpMatrix;

    protected ShadowShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "in_position");
        super.bindAttribute(1, "in_textureCoords");
    }

    @Override
    protected void getAllUniformLocations() {
        location_mvpMatrix = super.getUniformLocation("mvpMatrix");
    }

    protected void loadMvpMatrix(Matrix4f mvpMatrix) {
        super.loadMatrix(location_mvpMatrix, mvpMatrix);
    }
}
