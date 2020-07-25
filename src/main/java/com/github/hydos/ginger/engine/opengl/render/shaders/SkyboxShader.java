package com.github.hydos.ginger.engine.opengl.render.shaders;

import com.github.hydos.ginger.engine.common.cameras.Camera;
import com.github.hydos.ginger.engine.common.math.Maths;
import org.joml.Matrix4f;

public class SkyboxShader extends ShaderProgram {
    private int location_projectionMatrix;
    private int location_viewMatrix;

    public SkyboxShader() {
        super("skyboxVertexShader.glsl", "skyboxFragmentShader.glsl");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);
        super.loadMatrix(location_viewMatrix, matrix);
    }
}