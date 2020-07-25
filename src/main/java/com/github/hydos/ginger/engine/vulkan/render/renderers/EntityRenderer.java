package com.github.hydos.ginger.engine.vulkan.render.renderers;

import com.github.hydos.ginger.engine.common.render.Renderer;
import com.github.hydos.ginger.engine.vulkan.VKVariables;
import com.github.hydos.ginger.engine.vulkan.elements.VKRenderObject;
import com.github.hydos.ginger.engine.vulkan.model.VKModelLoader.VKMesh;
import com.github.hydos.ginger.engine.vulkan.model.VKVertex;
import com.github.hydos.ginger.engine.vulkan.render.VKBufferMesh;
import com.github.hydos.ginger.engine.vulkan.utils.VKUtils;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

public class EntityRenderer extends Renderer {
    public List<VKRenderObject> entities;//TODO: batch rendering

    public EntityRenderer() {
        priority = 1;
        entities = new ArrayList<>();
    }

    public void processEntity(VKRenderObject entity) {
        VKMesh mesh = entity.getRawModel();
        VKBufferMesh processedMesh = new VKBufferMesh();
        processedMesh.vkMesh = mesh;
        int vertexCount = mesh.positions.size();

        processedMesh.vertices = new VKVertex[vertexCount];

        Vector3f color = new Vector3f(1.0f, 1.0f, 1.0f);

        for (int i = 0; i < vertexCount; i++) {
            processedMesh.vertices[i] = new VKVertex(
                    mesh.positions.get(i),
                    color,
                    mesh.texCoords.get(i));
        }

        processedMesh.indices = new int[mesh.indices.size()];

        for (int i = 0; i < processedMesh.indices.length; i++) {
            processedMesh.indices[i] = mesh.indices.get(i);
        }

        processedMesh = VKUtils.createVertexBuffer(processedMesh);
        processedMesh = VKUtils.createIndexBuffer(processedMesh);
        entity.setModel(processedMesh);
        entities.add(entity);
    }

    @Override
    public void VKRender(MemoryStack stack, VkCommandBuffer commandBuffer, int index) {

        for (VKRenderObject entity : entities) {
            VKBufferMesh mesh = entity.getModel();

            LongBuffer vertexBuffers = stack.longs(mesh.vertexBuffer);
            LongBuffer offsets = stack.longs(0);
            vkCmdBindVertexBuffers(commandBuffer, 0, vertexBuffers, offsets);

            vkCmdBindIndexBuffer(commandBuffer, mesh.indexBuffer, 0, VK_INDEX_TYPE_UINT32);

            vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
                    VKVariables.pipelineLayout,
                    0, stack.longs(
                            entity.descriptorSets.get(index)
                    ),
                    null);

            vkCmdDrawIndexed(commandBuffer, mesh.vkMesh.indices.size(), 1, 0, 0, 0);
        }

    }


}
