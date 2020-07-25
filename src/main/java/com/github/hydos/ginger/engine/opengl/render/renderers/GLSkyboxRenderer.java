package com.github.hydos.ginger.engine.opengl.render.renderers;

import com.github.hydos.ginger.engine.common.cameras.Camera;
import com.github.hydos.ginger.engine.common.render.Renderer;
import com.github.hydos.ginger.engine.opengl.render.models.RawModel;
import com.github.hydos.ginger.engine.opengl.render.shaders.SkyboxShader;
import com.github.hydos.ginger.engine.opengl.utils.GLLoader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class GLSkyboxRenderer extends Renderer {
    //TODO: use these vertices for cubes
    private static final float SIZE = 50f;
    private static final float[] VERTICES =
            {
                    -SIZE, SIZE, -SIZE,
                    -SIZE, -SIZE, -SIZE,
                    SIZE, -SIZE, -SIZE,
                    SIZE, -SIZE, -SIZE,
                    SIZE, SIZE, -SIZE,
                    -SIZE, SIZE, -SIZE,
                    -SIZE, -SIZE, SIZE,
                    -SIZE, -SIZE, -SIZE,
                    -SIZE, SIZE, -SIZE,
                    -SIZE, SIZE, -SIZE,
                    -SIZE, SIZE, SIZE,
                    -SIZE, -SIZE, SIZE,
                    SIZE, -SIZE, -SIZE,
                    SIZE, -SIZE, SIZE,
                    SIZE, SIZE, SIZE,
                    SIZE, SIZE, SIZE,
                    SIZE, SIZE, -SIZE,
                    SIZE, -SIZE, -SIZE,
                    -SIZE, -SIZE, SIZE,
                    -SIZE, SIZE, SIZE,
                    SIZE, SIZE, SIZE,
                    SIZE, SIZE, SIZE,
                    SIZE, -SIZE, SIZE,
                    -SIZE, -SIZE, SIZE,
                    -SIZE, SIZE, -SIZE,
                    SIZE, SIZE, -SIZE,
                    SIZE, SIZE, SIZE,
                    SIZE, SIZE, SIZE,
                    -SIZE, SIZE, SIZE,
                    -SIZE, SIZE, -SIZE,
                    -SIZE, -SIZE, -SIZE,
                    -SIZE, -SIZE, SIZE,
                    SIZE, -SIZE, -SIZE,
                    SIZE, -SIZE, -SIZE,
                    -SIZE, -SIZE, SIZE,
                    SIZE, -SIZE, SIZE
            };
    private static String[] TEXTURE_FILES =
            {
                    "right.png", "left.png", "up.png", "down.png", "back.png", "front.png"
            };
    private RawModel cube;
    private int texture;
    private SkyboxShader shader;

    public GLSkyboxRenderer(Matrix4f projectionMatrix) {
        cube = GLLoader.loadToVAO(VERTICES, 3);
        texture = GLLoader.loadCubeMap(TEXTURE_FILES);
        shader = new SkyboxShader();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

    public void render(Camera camera) {
        shader.start();
        shader.loadViewMatrix(camera);
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }
}
