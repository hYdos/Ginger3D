package com.github.hydos.ginger.engine.opengl.postprocessing;

import org.lwjgl.opengl.GL11;

public class ImageRenderer {
    private FrameBufferObject frameBufferObject;

    protected ImageRenderer() {
    }

    protected ImageRenderer(int width, int height) {
        this.frameBufferObject = new FrameBufferObject(new ContrastChanger());
    }

    protected void cleanUp() {
    }

    protected int getOutputTexture() {
        return frameBufferObject.colorTexture;
    }

    protected void renderQuad() {
        if (frameBufferObject != null) {
            frameBufferObject.bindFBO();
        }
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        if (frameBufferObject != null) {
            frameBufferObject.unbindFBO();
        }
    }
}
