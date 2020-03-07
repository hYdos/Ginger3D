package com.github.hydos.ginger.engine.opengl.render.renderers;

import java.util.*;

import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import com.github.fulira.litecraft.types.block.BlockInstance;
import com.github.hydos.ginger.engine.common.api.GingerRegister;
import com.github.hydos.ginger.engine.common.elements.objects.GLRenderObject;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.common.math.Maths;
import com.github.hydos.ginger.engine.common.render.Renderer;
import com.github.hydos.ginger.engine.opengl.render.*;
import com.github.hydos.ginger.engine.opengl.render.models.*;
import com.github.hydos.ginger.engine.opengl.render.shaders.GLStaticShader;
import com.github.hydos.ginger.engine.opengl.render.texture.ModelTexture;

public class GLObjectRenderer extends Renderer
{
	private GLStaticShader shader;

	public GLObjectRenderer(GLStaticShader shader, Matrix4f projectionMatrix)
	{
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void prepare()
	{ GL11.glEnable(GL11.GL_DEPTH_TEST); }

	private void prepareInstance(GLRenderObject entity)
	{
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

	private void prepareTexturedModel(GLTexturedModel model)
	{
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		if (texture.isTransparent())
		{
			GLRenderManager.disableCulling();
		}
		else
		{
			GLRenderManager.enableCulling();
		}
		shader.loadFakeLightingVariable(texture.isUseFakeLighting());
		shader.loadShine(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
	}

	public void render(Map<GLTexturedModel, List<GLRenderObject>> entities)
	{
		for (GLTexturedModel model : entities.keySet())
		{
			prepareTexturedModel(model);
			List<GLRenderObject> batch = entities.get(model);
			for (GLRenderObject entity : batch)
			{
				if (entity.isVisible())
				{
					prepareInstance(entity);
					GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				}
			}
			unbindTexturedModel();
		}
	}

	private void unbindTexturedModel()
	{
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	public void render(List<BlockInstance> renderList)
	{
		prepare();
		shader.start();
		shader.loadSkyColour(Window.getColour());
		shader.loadViewMatrix(GingerRegister.getInstance().game.data.camera);
		for (GLRenderObject entity : renderList)
		{
			if (entity != null && entity.getModel() != null)
			{
				GLTexturedModel model = (GLTexturedModel) entity.getModel();
				prepareTexturedModel(model);
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				unbindTexturedModel();
			}
		}
		shader.stop();
	}
}
