package com.github.hydos.ginger.engine.opengl.postprocessing;

import com.github.hydos.ginger.engine.common.fbo.FBOCallbackHandler;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class ContrastChanger extends FBOCallbackHandler {
    private ImageRenderer renderer;
    private ContrastShader shader;

    public ContrastChanger() {
        shader = new ContrastShader();
        renderer = new ImageRenderer();
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        shader.cleanUp();
    }

    @Override
    public void render(int texture) {
        shader.start();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        renderer.renderQuad();
        shader.stop();
    }
}
