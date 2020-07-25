package com.github.hydos.ginger.engine.common.obj;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {
    private Vector3f position;
    private Vector2f textureIndex = null;
    private Vector3f normalIndex = null;
    private Vertex duplicateVertex = null;
    private int index;
    private float length;

    public Vertex(int index, Vector3f position) {
        this.index = index;
        this.position = position;
        this.length = position.length();
    }

    public Vertex(Vector3f position, Vector3f normal, Vector2f textureCoord) {
        this.position = position;
        this.normalIndex = normal;
        this.textureIndex = textureCoord;
    }

    public Vertex getDuplicateVertex() {
        return duplicateVertex;
    }

    public void setDuplicateVertex(Vertex duplicateVertex) {
        this.duplicateVertex = duplicateVertex;
    }

    public int getIndex() {
        return index;
    }

    public float getLength() {
        return length;
    }

    public Vector3f getNormalIndex() {
        return normalIndex;
    }

    public void setNormalIndex(Vector3f normalIndex) {
        this.normalIndex = normalIndex;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getTextureIndex() {
        return textureIndex;
    }

    public void setTextureIndex(Vector2f textureIndex) {
        this.textureIndex = textureIndex;
    }

    public boolean hasSameTextureAndNormal(Vector2f textureIndexOther, Vector3f normalIndexOther) {
        return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
    }

    public boolean isSet() {
        return textureIndex != null && normalIndex != null;
    }
}
