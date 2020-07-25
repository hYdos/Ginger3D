package com.github.hydos.ginger.engine.vulkan.render;

import com.github.hydos.ginger.engine.vulkan.VKVariables;
import com.github.hydos.ginger.engine.vulkan.model.VKModelLoader.VKMesh;
import com.github.hydos.ginger.engine.vulkan.model.VKVertex;

import static org.lwjgl.vulkan.VK10.vkDestroyBuffer;
import static org.lwjgl.vulkan.VK10.vkFreeMemory;

public class VKBufferMesh {

    public long vertexBuffer;
    public long indexBuffer;
    public VKMesh vkMesh;
    public int[] indices;
    public VKVertex[] vertices;
    public long vertexBufferMemory;
    public long indexBufferMemory;

    public void cleanup() {
        vkDestroyBuffer(VKVariables.device, indexBuffer, null);
        vkFreeMemory(VKVariables.device, indexBufferMemory, null);

        vkDestroyBuffer(VKVariables.device, vertexBuffer, null);
        vkFreeMemory(VKVariables.device, vertexBufferMemory, null);
    }

}
