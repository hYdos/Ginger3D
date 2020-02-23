package com.github.hydos.ginger.engine.obj;

import com.github.hydos.ginger.engine.math.vectors.*;

public class Vertex {
		
	private Vector3f position;
	private Vector2f textureIndex = null;
	private Vector3f normalIndex = null;
	private Vertex duplicateVertex = null;
	private int index;
	private float length;
	
	public Vertex(int index,Vector3f position){
		this.index = index;
		this.position = position;
		this.length = position.length();
	}
	
	public Vertex(Vector3f position, Vector3f normal, Vector2f textureCoord) {
		this.position = position;
		this.normalIndex = normal;
		this.textureIndex = textureCoord;
	}
	
	public int getIndex(){
		return index;
	}
	
	public float getLength(){
		return length;
	}
	
	public boolean isSet(){
		return textureIndex!=null && normalIndex!=null;
	}
	
	public boolean hasSameTextureAndNormal(Vector2f textureIndexOther,Vector3f normalIndexOther){
		return textureIndexOther==textureIndex && normalIndexOther==normalIndex;
	}
	
	public void setTextureIndex(Vector2f textureIndex){
		this.textureIndex = textureIndex;
	}
	
	public void setNormalIndex(Vector3f normalIndex){
		this.normalIndex = normalIndex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector2f getTextureIndex() {
		return textureIndex;
	}

	public Vector3f getNormalIndex() {
		return normalIndex;
	}

	public Vertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(Vertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

}
