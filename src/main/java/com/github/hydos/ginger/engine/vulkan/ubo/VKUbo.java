package com.github.hydos.ginger.engine.vulkan.ubo;

import com.github.hydos.ginger.VulkanExample;
import com.github.hydos.ginger.engine.vulkan.VKVariables;
import com.github.hydos.ginger.engine.vulkan.utils.AlignmentUtils;
import com.github.hydos.ginger.engine.vulkan.utils.VKBufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class VKUbo {

    public static List<Long> ubos; //FIXME: may be the answer to all problems
    public static List<Long> ubosMemory;

    public long ubo;
    public long uboMem;

    public VKUbo() {
        try (MemoryStack stack = stackPush()) {
            LongBuffer pBuffer = stack.mallocLong(1);
            LongBuffer pBufferMemory = stack.mallocLong(1);

            for (int i = 0; i < VKVariables.swapChainImages.size(); i++) {
                VKBufferUtils.createBuffer(VulkanExample.UniformBufferObject.SIZEOF,
                        VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT,
                        VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                        pBuffer,
                        pBufferMemory);
                ubo = pBuffer.get(0);
                uboMem = pBufferMemory.get(0);
                ubos.add(ubo);
                ubosMemory.add(uboMem);
            }

        }
    }

    public static void setupUboManagement() {
        ubos = new ArrayList<>(VKVariables.swapChainImages.size());
        ubosMemory = new ArrayList<>(VKVariables.swapChainImages.size());
    }

    public static void putUBOInMemory(ByteBuffer buffer, VulkanExample.UniformBufferObject ubo) {
        final int mat4Size = 16 * Float.BYTES;

        ubo.model.get(0, buffer);
        ubo.view.get(AlignmentUtils.alignas(mat4Size, AlignmentUtils.alignof(ubo.view)), buffer);
        ubo.proj.get(AlignmentUtils.alignas(mat4Size * 2, AlignmentUtils.alignof(ubo.view)), buffer);
    }
}
