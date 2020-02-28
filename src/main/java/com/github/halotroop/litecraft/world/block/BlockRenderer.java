package com.github.halotroop.litecraft.world.block;

import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import com.github.halotroop.litecraft.Litecraft;
import com.github.halotroop.litecraft.types.block.BlockEntity;
import com.github.halotroop.litecraft.world.gen.WorldGenConstants;
import com.github.hydos.ginger.engine.api.GingerRegister;
import com.github.hydos.ginger.engine.elements.objects.RenderObject;
import com.github.hydos.ginger.engine.io.Window;
import com.github.hydos.ginger.engine.math.Maths;
import com.github.hydos.ginger.engine.render.Renderer;
import com.github.hydos.ginger.engine.render.models.*;
import com.github.hydos.ginger.engine.render.shaders.StaticShader;
import com.github.hydos.ginger.engine.render.texture.ModelTexture;

public class BlockRenderer extends Renderer implements WorldGenConstants
{
	private StaticShader shader;

	public BlockRenderer(StaticShader shader, Matrix4f projectionMatrix)
	{
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	private void prepBlockInstance(RenderObject entity)
	{
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

	public void prepareModel(TexturedModel model)
	{
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		Litecraft.getInstance().binds++;
	}

	private void prepTexture(ModelTexture texture, int textureID)
	{
		shader.loadFakeLightingVariable(texture.isUseFakeLighting());
		shader.loadShine(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public void unbindModel()
	{
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	public void render(BlockEntity[] renderList)
	{
		shader.start();
		shader.loadSkyColour(Window.getColour());
		shader.loadViewMatrix(GingerRegister.getInstance().game.data.camera);
		TexturedModel model = renderList[0].getModel();
		if (GingerRegister.getInstance().wireframe)
		{ GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE); }
		for (int x = 0; x < CHUNK_SIZE; x++)
		{
			for (int y = 0; y < CHUNK_SIZE; y++)
			{
				for (int z = 0; z < CHUNK_SIZE; z++)
				{
					BlockEntity entity = renderList[x * CHUNK_SIZE * CHUNK_SIZE + z * CHUNK_SIZE + y];
					if (entity != null && entity.getModel() != null)
					{
						prepTexture(entity.getModel().getTexture(), entity.getModel().getTexture().getTextureID());
						prepBlockInstance(entity);
						GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					}
				}
			}
		}
		if (GingerRegister.getInstance().wireframe)
		{ GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL); }
		shader.stop();
	}
}
