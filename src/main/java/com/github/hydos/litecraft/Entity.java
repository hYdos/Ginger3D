package com.github.hydos.litecraft;

import com.github.hydos.ginger.engine.elements.objects.RenderObject;
import com.github.hydos.ginger.engine.math.vectors.Vector3f;
import com.github.hydos.ginger.engine.render.models.TexturedModel;

public abstract class Entity extends RenderObject{

	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}



}
