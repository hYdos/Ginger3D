package com.github.hydos.ginger.engine.common.obj.normals;

public class ModelDataNM {
    private float[] vertices;
    private float[] textureCoords;
    private float[] normals;
    private float[] tangents;
    private int[] indices;
    private float furthestPoint;

    public ModelDataNM(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices,
                       float furthestPoint) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.furthestPoint = furthestPoint;
        this.tangents = tangents;
    }

    public float getFurthestPoint() {
        return furthestPoint;
    }

    public int[] getIndices() {
        return indices;
    }

    public float[] getNormals() {
        return normals;
    }

    public float[] getTangents() {
        return tangents;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public float[] getVertices() {
        return vertices;
    }
}
