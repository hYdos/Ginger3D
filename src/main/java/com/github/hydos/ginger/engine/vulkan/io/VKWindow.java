package com.github.hydos.ginger.engine.vulkan.io;

import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.vulkan.VKVariables;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class VKWindow {
    public static void createSurface() {

        try (MemoryStack stack = MemoryStack.stackPush()) {

            LongBuffer pSurface = stack.longs(VK_NULL_HANDLE);

            if (GLFWVulkan.glfwCreateWindowSurface(VKVariables.instance, Window.getWindow(), null, pSurface) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create window surface");
            }

            VKVariables.surface = pSurface.get(0);
        }
    }

}
