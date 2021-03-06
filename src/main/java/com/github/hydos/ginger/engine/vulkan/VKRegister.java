package com.github.hydos.ginger.engine.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import com.github.hydos.ginger.engine.vulkan.utils.VKUtils;

public class VKRegister
{
	
	public static void createInstance() {

		try(MemoryStack stack = stackPush()) {

			// Use calloc to initialize the structs with 0s. Otherwise, the program can crash due to random values

			VkApplicationInfo appInfo = VkApplicationInfo.callocStack(stack);

			appInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
			appInfo.pApplicationName(stack.UTF8Safe("Hello Triangle"));
			appInfo.applicationVersion(VK_MAKE_VERSION(1, 0, 0));
			appInfo.pEngineName(stack.UTF8Safe("No Engine"));
			appInfo.engineVersion(VK_MAKE_VERSION(1, 0, 0));
			appInfo.apiVersion(VK_API_VERSION_1_0);

			VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.callocStack(stack);

			createInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
			createInfo.pApplicationInfo(appInfo);
			// enabledExtensionCount is implicitly set when you call ppEnabledExtensionNames
			createInfo.ppEnabledExtensionNames(VKUtils.getRequiredExtensions());

			// We need to retrieve the pointer of the created instance
			PointerBuffer instancePtr = stack.mallocPointer(1);

			if(vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS) {
				throw new RuntimeException("Failed to create instance");
			}

			VKVariables.instance = new VkInstance(instancePtr.get(0), createInfo);
		}
	}
	
}
