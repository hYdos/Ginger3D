package com.github.hydos.ginger.engine.opengl.render.shaders;

import org.joml.Matrix4f;

public class GuiShader extends ShaderProgram {
    private static final String VERTEX_FILE = "guiVertexShader.glsl";
    private static final String FRAGMENT_FILE = "guiFragmentShader.glsl";
    private int location_transformationMatrix;

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    public void loadTransformation(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }
}