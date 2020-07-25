package com.github.hydos.ginger.engine.common.obj;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;
import org.lwjgl.assimp.AIVector3D.Buffer;

public class OBJFileLoader {
    public static String resourceLocation = "C:/Users/Hayden/Desktop/Litecraft3D/src/main/resources/models/";

    public static Mesh loadModel(String filePath) {
        AIScene scene = null;
        try {
            scene = Assimp.aiImportFile(resourceLocation + filePath, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate);
            if (scene == null) {
                System.err.println("The model " + resourceLocation + filePath + " has failed to load");
                return new Mesh(new float[0], new float[0], new float[0], new int[0], 1F);
            }
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));
            int vertexCount = mesh.mNumVertices();
            AIVector3D.Buffer vertices = mesh.mVertices();
            AIVector3D.Buffer normals = mesh.mNormals();
            Vertex[] vertexList = new Vertex[vertexCount];
            for (int i = 0; i < vertexCount; i++) {
                AIVector3D vertex = vertices.get(i);
                Vector3f meshVertex = new Vector3f(vertex.x(), vertex.y(), vertex.z());
                AIVector3D normal = normals.get(i);
                Vector3f meshNormal = new Vector3f(normal.x(), normal.y(), normal.z());
                Vector2f meshTextureCoord = new Vector2f(0, 0);
                if (mesh.mNumUVComponents().get(0) != 0) {
                    AIVector3D texture = mesh.mTextureCoords(0).get(i);
                    meshTextureCoord.set(texture.x(), texture.y());
                }
                vertexList[i] = new Vertex(meshVertex, meshNormal, meshTextureCoord);
            }
            int faceCount = mesh.mNumFaces();
            AIFace.Buffer indices = mesh.mFaces();
            int[] indicesList = new int[faceCount * 3];
            for (int i = 0; i < faceCount; i++) {
                AIFace face = indices.get(i);
                indicesList[i * 3 + 0] = face.mIndices().get(0);
                indicesList[i * 3 + 1] = face.mIndices().get(1);
                indicesList[i * 3 + 2] = face.mIndices().get(2);
            }
            return parseMeshData(vertexList, indicesList, normals);
        } catch (Exception e) {
            System.err.println("Couldnt load scene file!");
            e.printStackTrace();
        }
        return new Mesh(new float[0], new float[0], new float[0], new int[0], 1F);
    }

    private static Mesh parseMeshData(Vertex[] vertexList, int[] indicesList, Buffer normals) {
        float[] verticies = new float[vertexList.length * 3];
        float[] textureCoords = new float[vertexList.length * 2];
        //texture cords where stored in the vertices so there should be as many as there are vertices
        int j = 0;
        int i = 0;
        for (Vertex vertex : vertexList) {
            float x = vertex.getPosition().x;
            float y = vertex.getPosition().y;
            float z = vertex.getPosition().z;
            verticies[i++] = x;
            verticies[i++] = y;
            verticies[i++] = z;
            textureCoords[j * 2] = vertex.getTextureIndex().x;
            textureCoords[j * 2 + 1] = 1 - vertex.getTextureIndex().y;
            j++;
        }
        Mesh mesh = new Mesh(verticies, textureCoords, new float[normals.sizeof()], indicesList, i);
        mesh.vkVertices = vertexList;
        return mesh;
    }
}