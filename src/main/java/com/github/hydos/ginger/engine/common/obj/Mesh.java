package com.github.hydos.ginger.engine.common.obj;

public class Mesh {
    public Vertex[] vkVertices;//may not be set only used for vulkan
    private float[] vertices;
    private float[] textureCoords;
    private float[] normals;
    private int[] indices;
    private float furthestPoint;

    public Mesh(float[] vertices, float[] textureCoords, float[] normals, int[] indices,
                float furthestPoint) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.furthestPoint = furthestPoint;
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

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public float[] getVertices() {
        return vertices;
    }
}
