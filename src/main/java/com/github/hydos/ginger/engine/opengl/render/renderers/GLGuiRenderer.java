package com.github.hydos.ginger.engine.opengl.render.renderers;

import com.github.hydos.ginger.engine.common.elements.GLGuiTexture;
import com.github.hydos.ginger.engine.common.math.Maths;
import com.github.hydos.ginger.engine.common.render.Renderer;
import com.github.hydos.ginger.engine.opengl.render.models.RawModel;
import com.github.hydos.ginger.engine.opengl.render.shaders.GuiShader;
import com.github.hydos.ginger.engine.opengl.utils.GLLoader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class GLGuiRenderer extends Renderer {
    private final RawModel quad;
    private GuiShader shader;

    public GLGuiRenderer(GuiShader shader) {
        this.shader = shader;
        float[] positions =
                {
                        -1, 1, -1, -1, 1, 1, 1, -1
                };
        quad = GLLoader.loadToVAO(positions, 2);
    }

    public void cleanUp() {
        shader.cleanUp();
    }

    public void render(List<GLGuiTexture> guis) {
        if (guis != null) {
            shader.start();
            GL30.glBindVertexArray(quad.getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            for (GLGuiTexture gui : guis) {
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
                Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
                shader.loadTransformation(matrix);
                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
            }
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
            shader.stop();
        }
    }
}
