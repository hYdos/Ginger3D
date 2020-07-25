package com.github.hydos.ginger.engine.common.obj.normals;

import com.github.hydos.ginger.engine.opengl.render.models.RawModel;
import com.github.hydos.ginger.engine.opengl.utils.GLLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NormalMappedObjLoader {
    private static void calculateTangents(VertexNM v0, VertexNM v1, VertexNM v2,
                                          List<Vector2f> textures) {
        Vector3f delatPos1 = v1.getPosition().sub(v0.getPosition());
        Vector3f delatPos2 = v2.getPosition().sub(v0.getPosition());
        Vector2f uv0 = textures.get(v0.getTextureIndex());
        Vector2f uv1 = textures.get(v1.getTextureIndex());
        Vector2f uv2 = textures.get(v2.getTextureIndex());
        Vector2f deltaUv1 = uv1.sub(uv0);
        Vector2f deltaUv2 = uv2.sub(uv0);
        float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
        delatPos1.mul(deltaUv2.y);
        delatPos2.mul(deltaUv1.y);
        Vector3f tangent = delatPos1.sub(delatPos2);
        tangent.mul(r);
        v0.addTangent(tangent);
        v1.addTangent(tangent);
        v2.addTangent(tangent);
    }

    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }

    private static VertexNM dealWithAlreadyProcessedVertex(VertexNM previousVertex, int newTextureIndex,
                                                           int newNormalIndex, List<Integer> indices, List<VertexNM> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
            return previousVertex;
        } else {
            VertexNM anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex,
                        newNormalIndex, indices, vertices);
            } else {
                VertexNM duplicateVertex = previousVertex.duplicate(vertices.size());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
                return duplicateVertex;
            }
        }
    }

    public static RawModel loadOBJ(String objFileName) {
        BufferedReader isr = null;
        isr = new BufferedReader(new InputStreamReader(NormalMappedObjLoader.class.getResourceAsStream("/models/" + objFileName)));
        BufferedReader reader = new BufferedReader(isr);
        String line;
        List<VertexNM> vertices = new ArrayList<VertexNM>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> indices = new ArrayList<Integer>();
        try {
            while (true) {
                line = reader.readLine();
                if (line.startsWith("v ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f vertex = new Vector3f(Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]),
                            Float.valueOf(currentLine[3]));
                    VertexNM newVertex = new VertexNM(vertices.size(), vertex);
                    vertices.add(newVertex);
                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split(" ");
                    Vector2f texture = new Vector2f(Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f normal = new Vector3f(Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]),
                            Float.valueOf(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    break;
                }
            }
            while (line != null && line.startsWith("f ")) {
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                VertexNM v0 = processVertex(vertex1, vertices, indices);
                VertexNM v1 = processVertex(vertex2, vertices, indices);
                VertexNM v2 = processVertex(vertex3, vertices, indices);
                calculateTangents(v0, v1, v2, textures);//NEW
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading the file");
        }
        removeUnusedVertices(vertices);
        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float[] tangentsArray = new float[vertices.size() * 3];
        int[] indicesArray = convertIndicesListToArray(indices);
        return GLLoader.loadToVAO(verticesArray, indicesArray, normalsArray, tangentsArray, texturesArray);
    }

    private static VertexNM processVertex(String[] vertex, List<VertexNM> vertices,
                                          List<Integer> indices) {
        int index = Integer.parseInt(vertex[0]) - 1;
        VertexNM currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
            return currentVertex;
        } else {
            return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
                    vertices);
        }
    }

    private static void removeUnusedVertices(List<VertexNM> vertices) {
        for (VertexNM vertex : vertices) {
            vertex.averageTangents();
            if (!vertex.isSet()) {
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }
}