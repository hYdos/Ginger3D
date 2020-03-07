package com.github.fulira.litecraft.types.entity;

import org.joml.Vector3f;

import com.github.hydos.ginger.engine.common.elements.objects.GLRenderObject;
import com.github.hydos.ginger.engine.opengl.render.models.GLTexturedModel;

public abstract class Entity extends GLRenderObject
{
	public Entity(GLTexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale)
	{ super(model, position, rotX, rotY, rotZ, scale); }
}
