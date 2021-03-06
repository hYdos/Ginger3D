package com.github.hydos.ginger.engine.vulkan.utils;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateDevice;
import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;
import static org.lwjgl.vulkan.VK10.vkGetDeviceQueue;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceFeatures;

import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkQueue;

import com.github.hydos.ginger.VulkanExample;
import com.github.hydos.ginger.VulkanExample.QueueFamilyIndices;
import com.github.hydos.ginger.VulkanExample.SwapChainSupportDetails;
import com.github.hydos.ginger.engine.vulkan.VKVariables;

public class VKDeviceManager
{
	public static void createLogicalDevice() {

		try(MemoryStack stack = stackPush()) {

			QueueFamilyIndices indices = VKUtils.findQueueFamilies(VKVariables.physicalDevice);

			int[] uniqueQueueFamilies = indices.unique();

			VkDeviceQueueCreateInfo.Buffer queueCreateInfos = VkDeviceQueueCreateInfo.callocStack(uniqueQueueFamilies.length, stack);

			for(int i = 0;i < uniqueQueueFamilies.length;i++) {
				VkDeviceQueueCreateInfo queueCreateInfo = queueCreateInfos.get(i);
				queueCreateInfo.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO);
				queueCreateInfo.queueFamilyIndex(uniqueQueueFamilies[i]);
				queueCreateInfo.pQueuePriorities(stack.floats(1.0f));
			}

			VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.callocStack(stack);
			deviceFeatures.samplerAnisotropy(true);
			deviceFeatures.sampleRateShading(true); // Enable sample shading feature for the device

			VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.callocStack(stack);

			createInfo.sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO);
			createInfo.pQueueCreateInfos(queueCreateInfos);
			// queueCreateInfoCount is automatically set

			createInfo.pEnabledFeatures(deviceFeatures);

			createInfo.ppEnabledExtensionNames(VKUtils.asPointerBuffer(VulkanExample.DEVICE_EXTENSIONS));

			PointerBuffer pDevice = stack.pointers(VK_NULL_HANDLE);

			if(vkCreateDevice(VKVariables.physicalDevice, createInfo, null, pDevice) != VK_SUCCESS) {
				throw new RuntimeException("Failed to create logical device");
			}

			VKVariables.device = new VkDevice(pDevice.get(0), VKVariables.physicalDevice, createInfo);

			PointerBuffer pQueue = stack.pointers(VK_NULL_HANDLE);

			vkGetDeviceQueue(VKVariables.device, indices.graphicsFamily, 0, pQueue);
			VKVariables.graphicsQueue = new VkQueue(pQueue.get(0), VKVariables.device);

			vkGetDeviceQueue(VKVariables.device, indices.presentFamily, 0, pQueue);
			VKVariables.presentQueue = new VkQueue(pQueue.get(0), VKVariables.device);
		}
	}

	public static void pickPhysicalDevice()
	{
		try(MemoryStack stack = stackPush()) {

			IntBuffer deviceCount = stack.ints(0);

			vkEnumeratePhysicalDevices(VKVariables.instance, deviceCount, null);

			if(deviceCount.get(0) == 0) {
				throw new RuntimeException("Failed to find GPUs with Vulkan support");
			}

			PointerBuffer ppPhysicalDevices = stack.mallocPointer(deviceCount.get(0));

			vkEnumeratePhysicalDevices(VKVariables.instance, deviceCount, ppPhysicalDevices);

			VkPhysicalDevice device = null;

			for(int i = 0;i < ppPhysicalDevices.capacity();i++) {

				device = new VkPhysicalDevice(ppPhysicalDevices.get(i), VKVariables.instance);

				if(isDeviceSuitable(device)) {
					break;
				}
			}

			if(device == null) {
				throw new RuntimeException("Failed to find a suitable GPU");
			}

			VKVariables.physicalDevice = device;
			VKVariables.msaaSamples = VKUtils.getMaxUsableSampleCount();
		}
	}
	
	private static boolean isDeviceSuitable(VkPhysicalDevice device) {

		QueueFamilyIndices indices = VKUtils.findQueueFamilies(device);

		boolean extensionsSupported = VKUtils.checkDeviceExtensionSupport(device);
		boolean swapChainAdequate = false;
		boolean anisotropySupported = false;

		if(extensionsSupported) {
			try(MemoryStack stack = stackPush()) {
				SwapChainSupportDetails swapChainSupport = VKUtils.querySwapChainSupport(device, stack);
				swapChainAdequate = swapChainSupport.formats.hasRemaining() && swapChainSupport.presentModes.hasRemaining();
				VkPhysicalDeviceFeatures supportedFeatures = VkPhysicalDeviceFeatures.mallocStack(stack);
				vkGetPhysicalDeviceFeatures(device, supportedFeatures);
				anisotropySupported = supportedFeatures.samplerAnisotropy();
			}
		}

		return indices.isComplete() && extensionsSupported && swapChainAdequate && anisotropySupported;
	}
	
	
	
}
