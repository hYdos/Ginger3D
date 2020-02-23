package io.github.hydos.ginger.engine.obj;

import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVector3D.Buffer;
import org.lwjgl.assimp.Assimp;

import io.github.hydos.ginger.engine.math.vectors.Vector2f;
import io.github.hydos.ginger.engine.math.vectors.Vector3f;


public class OBJFileLoader {
	
	public static String resourceLocation = "/models/";
	
	public static ModelData loadModel(String filePath, String texturePath) {
		AIScene scene = null;
		try {
			scene = Assimp.aiImportFile(resourceLocation + filePath, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate);
			
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
					meshTextureCoord.setX(texture.x());
					meshTextureCoord.setY(texture.y());
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
		}catch(Exception e) {
			System.err.println("Couldnt load scene file!");
			e.printStackTrace();
		}
		
		
		return new ModelData(new float[0], new float[0], new float[0], new int[0], 1F);
	}

	private static ModelData parseMeshData(Vertex[] vertexList, int[] indicesList, Buffer normals) {
		float[] verticies = new float[vertexList.length];
		float[] textureCoords = new float[vertexList.length];
		//texture coords where stored in the vertices so there should be as many as there are vertices
		
		int j = 0;
		int i = 0;
		for(Vertex vertex: vertexList) {
			float x = vertex.getPosition().x;
			float y = vertex.getPosition().y;
			float z = vertex.getPosition().z;
			verticies[i] = x;
			i++;
			verticies[i] = y;
			i++;			
			verticies[i] = z;
			i++;
			textureCoords[j] = vertex.getTextureIndex().x;
			j++;
			textureCoords[j] = vertex.getTextureIndex().y;
		}
		
		return new ModelData(verticies, textureCoords, null, indicesList, i);
	}

}