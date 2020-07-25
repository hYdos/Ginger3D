package com.github.hydos.ginger.engine.common.obj.shapes;

import com.github.hydos.ginger.engine.common.obj.Mesh;

public class StaticCube {
    //@formatter:off
    public static float[] vertices =
            {
                    //North (back) face
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    //South (front) face
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
                    //Top face
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, 0.5f,
                    //Bottom face
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f
            };
    public static float[] textureCoords =
            {
                    0, 0,
                    0, 1,
                    1, 1,
                    1, 0,
                    0, 0,
                    0, 1,
                    1, 1,
                    1, 0,
                    0, 0,
                    0, 1,
                    1, 1,
                    1, 0,
                    0, 0,
                    0, 1,
                    1, 1,
                    1, 0,
                    0, 0,
                    0, 1,
                    1, 1,
                    1, 0,
                    0, 0,
                    0, 1,
                    1, 1,
                    1, 0
            };
    public static int[] indices =
            {
                    0, 1, 3,
                    3, 1, 2,
                    4, 5, 7,
                    7, 5, 6,
                    8, 9, 11,
                    11, 9, 10,
                    12, 13, 15,
                    15, 13, 14,
                    16, 17, 19,
                    19, 17, 18,
                    20, 21, 23,
                    23, 21, 22
            };
    //@formatter:on
    private static Mesh mesh = null;

    public static Mesh getCube() {
        if (mesh == null)
            mesh = new Mesh(vertices, textureCoords, new float[vertices.length], indices, vertices.length);
        return mesh;
    }
}
