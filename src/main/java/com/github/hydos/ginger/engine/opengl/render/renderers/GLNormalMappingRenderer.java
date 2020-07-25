package com.github.hydos.ginger.engine.opengl.render.renderers;

import com.github.hydos.ginger.engine.common.cameras.Camera;
import com.github.hydos.ginger.engine.common.elements.objects.GLRenderObject;
import com.github.hydos.ginger.engine.common.elements.objects.Light;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.common.math.Maths;
import com.github.hydos.ginger.engine.common.render.Renderer;
import com.github.hydos.ginger.engine.opengl.render.GLRenderManager;
import com.github.hydos.ginger.engine.opengl.render.models.GLTexturedModel;
import com.github.hydos.ginger.engine.opengl.render.models.RawModel;
import com.github.hydos.ginger.engine.opengl.render.shaders.NormalMappingShader;
import com.github.hydos.ginger.engine.opengl.render.texture.ModelTexture;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

public class GLNormalMappingRenderer extends Renderer {
    private NormalMappingShader shader;

    public GLNormalMappingRenderer(Matrix4f projectionMatrix) {
        this.shader = new NormalMappingShader();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

    private void prepare(Vector4f clipPlane, List<Light> lights, Camera camera) {
        shader.loadClipPlane(clipPlane);
        //need to be public variables in MasterRenderer
        shader.loadSkyColour(Window.getColour());
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        shader.loadLights(lights, viewMatrix);
        shader.loadViewMatrix(viewMatrix);
    }

    private void prepareInstance(GLRenderObject entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
                entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(0, 0);
    }

    private void prepareTexturedModel(GLTexturedModel model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        ModelTexture texture = model.getTexture();
        if (texture.isTransparent()) {
            GLRenderManager.disableCulling();
        }
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getNormalMap());
    }

    public void render(Map<GLTexturedModel, List<GLRenderObject>> entities, Vector4f clipPlane, List<Light> lights, Camera camera) {
        shader.start();
        prepare(clipPlane, lights, camera);
        for (GLTexturedModel model : entities.keySet()) {
            prepareTexturedModel(model);
            List<GLRenderObject> batch = entities.get(model);
            for (GLRenderObject entity : batch) {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
        shader.stop();
        entities.clear();
    }

    private void unbindTexturedModel() {
        GLRenderManager.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL30.glBindVertexArray(0);
    }
}
