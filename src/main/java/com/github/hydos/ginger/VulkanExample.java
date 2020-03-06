package com.github.hydos.ginger;

import static java.util.stream.Collectors.toSet;
import static org.lwjgl.assimp.Assimp.aiProcess_DropNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_FlipUVs;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_SUBOPTIMAL_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkAcquireNextImageKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkQueuePresentKHR;
import static org.lwjgl.vulkan.VK10.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.joml.Matrix4f;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Pointer;
import org.lwjgl.vulkan.VkAttachmentDescription;
import org.lwjgl.vulkan.VkAttachmentReference;
import org.lwjgl.vulkan.VkBufferCopy;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkBufferImageCopy;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkExtent3D;
import org.lwjgl.vulkan.VkFenceCreateInfo;
import org.lwjgl.vulkan.VkFormatProperties;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.lwjgl.vulkan.VkImageBlit;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.lwjgl.vulkan.VkOffset2D;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;
import org.lwjgl.vulkan.VkRenderPassCreateInfo;
import org.lwjgl.vulkan.VkSamplerCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.lwjgl.vulkan.VkSubpassDependency;
import org.lwjgl.vulkan.VkSubpassDescription;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import com.github.hydos.ginger.engine.common.info.RenderAPI;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.vulkan.VKRegister;
import com.github.hydos.ginger.engine.vulkan.VKVariables;
import com.github.hydos.ginger.engine.vulkan.io.VKWindow;
import com.github.hydos.ginger.engine.vulkan.managers.CommandBufferManager;
import com.github.hydos.ginger.engine.vulkan.managers.VKTextureManager;
import com.github.hydos.ginger.engine.vulkan.misc.AlignmentUtils;
import com.github.hydos.ginger.engine.vulkan.misc.Frame;
import com.github.hydos.ginger.engine.vulkan.misc.VKModelLoader;
import com.github.hydos.ginger.engine.vulkan.misc.VKModelLoader.VKMesh;
import com.github.hydos.ginger.engine.vulkan.render.VKBufferMesh;
import com.github.hydos.ginger.engine.vulkan.render.VKRenderManager;
import com.github.hydos.ginger.engine.vulkan.render.renderers.EntityRenderer;
import com.github.hydos.ginger.engine.vulkan.swapchain.VKSwapchainManager;
import com.github.hydos.ginger.engine.vulkan.utils.VKBufferUtils;
import com.github.hydos.ginger.engine.vulkan.utils.VKDeviceManager;
import com.github.hydos.ginger.engine.vulkan.utils.VKUtils;

public class VulkanExample {

	public static final int UINT32_MAX = 0xFFFFFFFF;
	public static final long UINT64_MAX = 0xFFFFFFFFFFFFFFFFL;

	public static final int MAX_FRAMES_IN_FLIGHT = 2;
		
	public static final Set<String> DEVICE_EXTENSIONS = Stream.of(VK_KHR_SWAPCHAIN_EXTENSION_NAME)
		.collect(toSet());



	public static class QueueFamilyIndices {

		// We use Integer to use null as the empty value
		public Integer graphicsFamily;
		public Integer presentFamily;

		public boolean isComplete() {
			return graphicsFamily != null && presentFamily != null;
		}

		public int[] unique() {
			return IntStream.of(graphicsFamily, presentFamily).distinct().toArray();
		}
	}

	public static class SwapChainSupportDetails {

		public VkSurfaceCapabilitiesKHR capabilities;
		public VkSurfaceFormatKHR.Buffer formats;
		public IntBuffer presentModes;

	}

	private static class UniformBufferObject {

		private static final int SIZEOF = 3 * 16 * Float.BYTES;

		private Matrix4f model;
		private Matrix4f view;
		private Matrix4f proj;

		public UniformBufferObject() {
			model = new Matrix4f();
			view = new Matrix4f();
			proj = new Matrix4f();
		}
	}

	public static class VKVertex {

		private static final int SIZEOF = (3 + 3 + 2) * Float.BYTES;
		private static final int OFFSETOF_POS = 0;
		private static final int OFFSETOF_COLOR = 3 * Float.BYTES;
		private static final int OFFSETOF_TEXTCOORDS = (3 + 3) * Float.BYTES;

		private Vector3fc pos;
		private Vector3fc color;
		private Vector2fc texCoords;

		public VKVertex(Vector3fc pos, Vector3fc color, Vector2fc texCoords) {
			this.pos = pos;
			this.color = color;
			this.texCoords = texCoords;
		}

		public static VkVertexInputBindingDescription.Buffer getBindingDescription() {

			VkVertexInputBindingDescription.Buffer bindingDescription =
				VkVertexInputBindingDescription.callocStack(1);

			bindingDescription.binding(0);
			bindingDescription.stride(VKVertex.SIZEOF);
			bindingDescription.inputRate(VK_VERTEX_INPUT_RATE_VERTEX);

			return bindingDescription;
		}

		public static VkVertexInputAttributeDescription.Buffer getAttributeDescriptions() {

			VkVertexInputAttributeDescription.Buffer attributeDescriptions =
				VkVertexInputAttributeDescription.callocStack(3);

			// Position
			VkVertexInputAttributeDescription posDescription = attributeDescriptions.get(0);
			posDescription.binding(0);
			posDescription.location(0);
			posDescription.format(VK_FORMAT_R32G32B32_SFLOAT);
			posDescription.offset(OFFSETOF_POS);

			// Color
			VkVertexInputAttributeDescription colorDescription = attributeDescriptions.get(1);
			colorDescription.binding(0);
			colorDescription.location(1);
			colorDescription.format(VK_FORMAT_R32G32B32_SFLOAT);
			colorDescription.offset(OFFSETOF_COLOR);

			// Texture coordinates
			VkVertexInputAttributeDescription texCoordsDescription = attributeDescriptions.get(2);
			texCoordsDescription.binding(0);
			texCoordsDescription.location(2);
			texCoordsDescription.format(VK_FORMAT_R32G32_SFLOAT);
			texCoordsDescription.offset(OFFSETOF_TEXTCOORDS);

			return attributeDescriptions.rewind();
		}

	}

	// ======= METHODS ======= //

	public void run() {
		initWindow();
		initVulkan();
		mainLoop();
		cleanup();
	}

	private void initWindow() {
		Window.create(1200, 800, "Vulkan Ginger2", 60, RenderAPI.Vulkan);
		glfwSetFramebufferSizeCallback(Window.getWindow(), this::framebufferResizeCallback);
	}

	private void framebufferResizeCallback(long window, int width, int height) {
		VKVariables.framebufferResize = true;
	}

	private void initVulkan() {
		VKRegister.createInstance();
		VKWindow.createSurface();
		GingerVK.init();
		GingerVK.getInstance().createRenderers();
		VKDeviceManager.pickPhysicalDevice();
		VKDeviceManager.createLogicalDevice();
		VKUtils.createCommandPool();
		VKTextureManager.createTextureImage();
		VKTextureManager.createTextureImageView();
		VKTextureManager.createTextureSampler();
		loadModel();
		createDescriptorSetLayout();
		VKSwapchainManager.createSwapChainObjects();
		createSyncObjects();
	}

	private void mainLoop() {

		while(!Window.closed()) {
			if(Window.shouldRender()) {
				Frame.drawFrame();
			}
			glfwPollEvents();
		}

		// Wait for the device to complete all operations before release resources
		vkDeviceWaitIdle(VKVariables.device);
	}

	private void cleanup() {
		VKSwapchainManager.cleanupSwapChain();

		vkDestroySampler(VKVariables.device, VKVariables.textureSampler, null);
		vkDestroyImageView(VKVariables.device, VKVariables.textureImageView, null);
		vkDestroyImage(VKVariables.device, VKVariables.textureImage, null);
		vkFreeMemory(VKVariables.device, VKVariables.textureImageMemory, null);

		vkDestroyDescriptorSetLayout(VKVariables.device, VKVariables.descriptorSetLayout, null);

		vkDestroyBuffer(VKVariables.device, VKVariables.indexBuffer, null);
		vkFreeMemory(VKVariables.device, VKVariables.indexBufferMemory, null);

		vkDestroyBuffer(VKVariables.device, VKVariables.vertexBuffer, null);
		vkFreeMemory(VKVariables.device, VKVariables.vertexBufferMemory, null);

		VKVariables.inFlightFrames.forEach(frame -> {

			vkDestroySemaphore(VKVariables.device, frame.renderFinishedSemaphore(), null);
			vkDestroySemaphore(VKVariables.device, frame.imageAvailableSemaphore(), null);
			vkDestroyFence(VKVariables.device, frame.fence(), null);
		});
		VKVariables.inFlightFrames.clear();

		vkDestroyCommandPool(VKVariables.device, VKVariables.commandPool, null);

		vkDestroyDevice(VKVariables.device, null);

		vkDestroySurfaceKHR(VKVariables.instance, VKVariables.surface, null);

		vkDestroyInstance(VKVariables.instance, null);
		
		Window.destroy();
	}

	public static void createImageViews() {

		VKVariables.swapChainImageViews = new ArrayList<>(VKVariables.swapChainImages.size());

		for(long swapChainImage : VKVariables.swapChainImages) {
			VKVariables.swapChainImageViews.add(createImageView(swapChainImage, VKVariables.swapChainImageFormat, VK_IMAGE_ASPECT_COLOR_BIT, 1));
		}
	}

	private void createDescriptorSetLayout() {

		try(MemoryStack stack = stackPush()) {

			VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.callocStack(2, stack);

			VkDescriptorSetLayoutBinding uboLayoutBinding = bindings.get(0);
			uboLayoutBinding.binding(0);
			uboLayoutBinding.descriptorCount(1);
			uboLayoutBinding.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
			uboLayoutBinding.pImmutableSamplers(null);
			uboLayoutBinding.stageFlags(VK_SHADER_STAGE_VERTEX_BIT);

			VkDescriptorSetLayoutBinding samplerLayoutBinding = bindings.get(1);
			samplerLayoutBinding.binding(1);
			samplerLayoutBinding.descriptorCount(1);
			samplerLayoutBinding.descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
			samplerLayoutBinding.pImmutableSamplers(null);
			samplerLayoutBinding.stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT);

			VkDescriptorSetLayoutCreateInfo layoutInfo = VkDescriptorSetLayoutCreateInfo.callocStack(stack);
			layoutInfo.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO);
			layoutInfo.pBindings(bindings);

			LongBuffer pDescriptorSetLayout = stack.mallocLong(1);

			if(vkCreateDescriptorSetLayout(VKVariables.device, layoutInfo, null, pDescriptorSetLayout) != VK_SUCCESS) {
				throw new RuntimeException("Failed to create descriptor set layout");
			}
			VKVariables.descriptorSetLayout = pDescriptorSetLayout.get(0);
		}
	}

	public static void createFramebuffers() {

		VKVariables.swapChainFramebuffers = new ArrayList<>(VKVariables.swapChainImageViews.size());

		try(MemoryStack stack = stackPush()) {

			LongBuffer attachments = stack.longs(VKVariables.colorImageView, VKVariables.depthImageView, VK_NULL_HANDLE);
			LongBuffer pFramebuffer = stack.mallocLong(1);

			// Lets allocate the create info struct once and just update the pAttachments field each iteration
			VkFramebufferCreateInfo framebufferInfo = VkFramebufferCreateInfo.callocStack(stack);
			framebufferInfo.sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO);
			framebufferInfo.renderPass(VKVariables.renderPass);
			framebufferInfo.width(VKVariables.swapChainExtent.width());
			framebufferInfo.height(VKVariables.swapChainExtent.height());
			framebufferInfo.layers(1);

			for(long imageView : VKVariables.swapChainImageViews) {

				attachments.put(2, imageView);

				framebufferInfo.pAttachments(attachments);

				if(vkCreateFramebuffer(VKVariables.device, framebufferInfo, null, pFramebuffer) != VK_SUCCESS) {
					throw new RuntimeException("Failed to create framebuffer");
				}

				VKVariables.swapChainFramebuffers.add(pFramebuffer.get(0));
			}
		}
	}

	public static void createColorResources() {

		try(MemoryStack stack = stackPush()) {

			LongBuffer pColorImage = stack.mallocLong(1);
			LongBuffer pColorImageMemory = stack.mallocLong(1);

			createImage(VKVariables.swapChainExtent.width(), VKVariables.swapChainExtent.height(),
				1,
				VKVariables.msaaSamples,
				VKVariables.swapChainImageFormat,
				VK_IMAGE_TILING_OPTIMAL,
				VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT | VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT,
				VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
				pColorImage,
				pColorImageMemory);

			VKVariables.colorImage = pColorImage.get(0);
			VKVariables.colorImageMemory = pColorImageMemory.get(0);

			VKVariables.colorImageView = createImageView(VKVariables.colorImage, VKVariables.swapChainImageFormat, VK_IMAGE_ASPECT_COLOR_BIT, 1);

			transitionImageLayout(VKVariables.colorImage, VKVariables.swapChainImageFormat, VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, 1);
		}
	}

	public static void createDepthResources() {

		try(MemoryStack stack = stackPush()) {

			int depthFormat = findDepthFormat();

			LongBuffer pDepthImage = stack.mallocLong(1);
			LongBuffer pDepthImageMemory = stack.mallocLong(1);

			createImage(
				VKVariables.swapChainExtent.width(), VKVariables.swapChainExtent.height(),
				1,
				VKVariables. msaaSamples,
				depthFormat,
				VK_IMAGE_TILING_OPTIMAL,
				VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT,
				VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
				pDepthImage,
				pDepthImageMemory);

			VKVariables.depthImage = pDepthImage.get(0);
			VKVariables.depthImageMemory = pDepthImageMemory.get(0);

			VKVariables.depthImageView = createImageView(VKVariables.depthImage, depthFormat, VK_IMAGE_ASPECT_DEPTH_BIT, 1);

			// Explicitly transitioning the depth image
			transitionImageLayout(VKVariables.depthImage, depthFormat,
				VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
				1);

		}
	}

	private static int findSupportedFormat(IntBuffer formatCandidates, int tiling, int features) {

		try(MemoryStack stack = stackPush()) {

			VkFormatProperties props = VkFormatProperties.callocStack(stack);

			for(int i = 0; i < formatCandidates.capacity(); ++i) {

				int format = formatCandidates.get(i);

				vkGetPhysicalDeviceFormatProperties(VKVariables.physicalDevice, format, props);

				if(tiling == VK_IMAGE_TILING_LINEAR && (props.linearTilingFeatures() & features) == features) {
					return format;
				} else if(tiling == VK_IMAGE_TILING_OPTIMAL && (props.optimalTilingFeatures() & features) == features) {
					return format;
				}

			}
		}

		throw new RuntimeException("Failed to find supported format");
	}


	public static int findDepthFormat() {
		return findSupportedFormat(
			stackGet().ints(VK_FORMAT_D32_SFLOAT, VK_FORMAT_D32_SFLOAT_S8_UINT, VK_FORMAT_D24_UNORM_S8_UINT),
			VK_IMAGE_TILING_OPTIMAL,
			VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT);
	}

	private static boolean hasStencilComponent(int format) {
		return format == VK_FORMAT_D32_SFLOAT_S8_UINT || format == VK_FORMAT_D24_UNORM_S8_UINT;
	}

	public static double log2(double n) {
		return Math.log(n) / Math.log(2);
	}
	public static void generateMipmaps(long image, int imageFormat, int width, int height, int mipLevels) {

		try(MemoryStack stack = stackPush()) {

			// Check if image format supports linear blitting
			VkFormatProperties formatProperties = VkFormatProperties.mallocStack(stack);
			vkGetPhysicalDeviceFormatProperties(VKVariables.physicalDevice, imageFormat, formatProperties);

			if((formatProperties.optimalTilingFeatures() & VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT) == 0) {
				throw new RuntimeException("Texture image format does not support linear blitting");
			}

			VkCommandBuffer commandBuffer = CommandBufferManager.beginSingleTimeCommands();

			VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.callocStack(1, stack);
			barrier.sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER);
			barrier.image(image);
			barrier.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
			barrier.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
			barrier.dstAccessMask(VK_QUEUE_FAMILY_IGNORED);
			barrier.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
			barrier.subresourceRange().baseArrayLayer(0);
			barrier.subresourceRange().layerCount(1);
			barrier.subresourceRange().levelCount(1);

			int mipWidth = width;
			int mipHeight = height;

			for(int i = 1;i < mipLevels;i++) {

				barrier.subresourceRange().baseMipLevel(i - 1);
				barrier.oldLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
				barrier.newLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL);
				barrier.srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);
				barrier.dstAccessMask(VK_ACCESS_TRANSFER_READ_BIT);

				vkCmdPipelineBarrier(commandBuffer,
					VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_TRANSFER_BIT, 0,
					null,
					null,
					barrier);

				VkImageBlit.Buffer blit = VkImageBlit.callocStack(1, stack);
				blit.srcOffsets(0).set(0, 0, 0);
				blit.srcOffsets(1).set(mipWidth, mipHeight, 1);
				blit.srcSubresource().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
				blit.srcSubresource().mipLevel(i - 1);
				blit.srcSubresource().baseArrayLayer(0);
				blit.srcSubresource().layerCount(1);
				blit.dstOffsets(0).set(0, 0, 0);
				blit.dstOffsets(1).set(mipWidth > 1 ? mipWidth / 2 : 1, mipHeight > 1 ? mipHeight / 2 : 1, 1);
				blit.dstSubresource().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
				blit.dstSubresource().mipLevel(i);
				blit.dstSubresource().baseArrayLayer(0);
				blit.dstSubresource().layerCount(1);

				vkCmdBlitImage(commandBuffer,
					image, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
					image, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
					blit,
					VK_FILTER_LINEAR);

				barrier.oldLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL);
				barrier.newLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
				barrier.srcAccessMask(VK_ACCESS_TRANSFER_READ_BIT);
				barrier.dstAccessMask(VK_ACCESS_SHADER_READ_BIT);

				vkCmdPipelineBarrier(commandBuffer,
					VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT, 0,
					null,
					null,
					barrier);

				if(mipWidth > 1) {
					mipWidth /= 2;
				}

				if(mipHeight > 1) {
					mipHeight /= 2;
				}
			}

			barrier.subresourceRange().baseMipLevel(mipLevels - 1);
			barrier.oldLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
			barrier.newLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
			barrier.srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);
			barrier.dstAccessMask(VK_ACCESS_SHADER_READ_BIT);

			vkCmdPipelineBarrier(commandBuffer,
				VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT, 0,
				null,
				null,
				barrier);

			CommandBufferManager.endSingleTimeCommands(commandBuffer);
		}
	}

	public static int getMaxUsableSampleCount() {

		try(MemoryStack stack = stackPush()) {

			VkPhysicalDeviceProperties physicalDeviceProperties = VkPhysicalDeviceProperties.mallocStack(stack);
			vkGetPhysicalDeviceProperties(VKVariables.physicalDevice, physicalDeviceProperties);

			int sampleCountFlags = physicalDeviceProperties.limits().framebufferColorSampleCounts()
				& physicalDeviceProperties.limits().framebufferDepthSampleCounts();

			if((sampleCountFlags & VK_SAMPLE_COUNT_64_BIT) != 0) {
				return VK_SAMPLE_COUNT_64_BIT;
			}
			if((sampleCountFlags & VK_SAMPLE_COUNT_32_BIT) != 0) {
				return VK_SAMPLE_COUNT_32_BIT;
			}
			if((sampleCountFlags & VK_SAMPLE_COUNT_16_BIT) != 0) {
				return VK_SAMPLE_COUNT_16_BIT;
			}
			if((sampleCountFlags & VK_SAMPLE_COUNT_8_BIT) != 0) {
				return VK_SAMPLE_COUNT_8_BIT;
			}
			if((sampleCountFlags & VK_SAMPLE_COUNT_4_BIT) != 0) {
				return VK_SAMPLE_COUNT_4_BIT;
			}
			if((sampleCountFlags & VK_SAMPLE_COUNT_2_BIT) != 0) {
				return VK_SAMPLE_COUNT_2_BIT;
			}

			return VK_SAMPLE_COUNT_1_BIT;
		}
	}

	public static long createImageView(long image, int format, int aspectFlags, int mipLevels) {

		try(MemoryStack stack = stackPush()) {

			VkImageViewCreateInfo viewInfo = VkImageViewCreateInfo.callocStack(stack);
			viewInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
			viewInfo.image(image);
			viewInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
			viewInfo.format(format);
			viewInfo.subresourceRange().aspectMask(aspectFlags);
			viewInfo.subresourceRange().baseMipLevel(0);
			viewInfo.subresourceRange().levelCount(mipLevels);
			viewInfo.subresourceRange().baseArrayLayer(0);
			viewInfo.subresourceRange().layerCount(1);

			LongBuffer pImageView = stack.mallocLong(1);

			if(vkCreateImageView(VKVariables.device, viewInfo, null, pImageView) != VK_SUCCESS) {
				throw new RuntimeException("Failed to create texture image view");
			}

			return pImageView.get(0);
		}
	}

	public static void createImage(int width, int height, int mipLevels, int numSamples, int format, int tiling, int usage, int memProperties,
		LongBuffer pTextureImage, LongBuffer pTextureImageMemory) {

		try(MemoryStack stack = stackPush()) {

			VkImageCreateInfo imageInfo = VkImageCreateInfo.callocStack(stack);
			imageInfo.sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO);
			imageInfo.imageType(VK_IMAGE_TYPE_2D);
			imageInfo.extent().width(width);
			imageInfo.extent().height(height);
			imageInfo.extent().depth(1);
			imageInfo.mipLevels(mipLevels);
			imageInfo.arrayLayers(1);
			imageInfo.format(format);
			imageInfo.tiling(tiling);
			imageInfo.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
			imageInfo.usage(usage);
			imageInfo.samples(numSamples);
			imageInfo.sharingMode(VK_SHARING_MODE_EXCLUSIVE);

			if(vkCreateImage(VKVariables.device, imageInfo, null, pTextureImage) != VK_SUCCESS) {
				throw new RuntimeException("Failed to create image");
			}

			VkMemoryRequirements memRequirements = VkMemoryRequirements.mallocStack(stack);
			vkGetImageMemoryRequirements(VKVariables.device, pTextureImage.get(0), memRequirements);

			VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.callocStack(stack);
			allocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
			allocInfo.allocationSize(memRequirements.size());
			allocInfo.memoryTypeIndex(findMemoryType(memRequirements.memoryTypeBits(), memProperties));

			if(vkAllocateMemory(VKVariables.device, allocInfo, null, pTextureImageMemory) != VK_SUCCESS) {
				throw new RuntimeException("Failed to allocate image memory");
			}

			vkBindImageMemory(VKVariables.device, pTextureImage.get(0), pTextureImageMemory.get(0), 0);
		}
	}

	public static void transitionImageLayout(long image, int format, int oldLayout, int newLayout, int mipLevels) {

		try(MemoryStack stack = stackPush()) {

			VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.callocStack(1, stack);
			barrier.sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER);
			barrier.oldLayout(oldLayout);
			barrier.newLayout(newLayout);
			barrier.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
			barrier.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
			barrier.image(image);

			barrier.subresourceRange().baseMipLevel(0);
			barrier.subresourceRange().levelCount(mipLevels);
			barrier.subresourceRange().baseArrayLayer(0);
			barrier.subresourceRange().layerCount(1);

			if(newLayout == VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {

				barrier.subresourceRange().aspectMask(VK_IMAGE_ASPECT_DEPTH_BIT);

				if(hasStencilComponent(format)) {
					barrier.subresourceRange().aspectMask(
						barrier.subresourceRange().aspectMask() | VK_IMAGE_ASPECT_STENCIL_BIT);
				}

			} else {
				barrier.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
			}

			int sourceStage;
			int destinationStage;

			if(oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {

				barrier.srcAccessMask(0);
				barrier.dstAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);

				sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
				destinationStage = VK_PIPELINE_STAGE_TRANSFER_BIT;

			} else if(oldLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL && newLayout == VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {

				barrier.srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);
				barrier.dstAccessMask(VK_ACCESS_SHADER_READ_BIT);

				sourceStage = VK_PIPELINE_STAGE_TRANSFER_BIT;
				destinationStage = VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;

			} else if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {

				barrier.srcAccessMask(0);
				barrier.dstAccessMask(VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT | VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT);

				sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
				destinationStage = VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT;

			} else if(oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL) {

				barrier.srcAccessMask(0);
				barrier.dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);

				sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
				destinationStage = VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;

			} else {
				throw new IllegalArgumentException("Unsupported layout transition");
			}

			VkCommandBuffer commandBuffer = CommandBufferManager.beginSingleTimeCommands();

			vkCmdPipelineBarrier(commandBuffer,
				sourceStage, destinationStage,
				0,
				null,
				null,
				barrier);

			CommandBufferManager.endSingleTimeCommands(commandBuffer);
		}
	}

	public static void copyBufferToImage(long buffer, long image, int width, int height) {

		try(MemoryStack stack = stackPush()) {

			VkCommandBuffer commandBuffer = CommandBufferManager.beginSingleTimeCommands();

			VkBufferImageCopy.Buffer region = VkBufferImageCopy.callocStack(1, stack);
			region.bufferOffset(0);
			region.bufferRowLength(0);   // Tightly packed
			region.bufferImageHeight(0);  // Tightly packed
			region.imageSubresource().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
			region.imageSubresource().mipLevel(0);
			region.imageSubresource().baseArrayLayer(0);
			region.imageSubresource().layerCount(1);
			region.imageOffset().set(0, 0, 0);
			region.imageExtent(VkExtent3D.callocStack(stack).set(width, height, 1));

			vkCmdCopyBufferToImage(commandBuffer, buffer, image, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, region);

			CommandBufferManager.endSingleTimeCommands(commandBuffer);
		}
	}

	public static void memcpy(ByteBuffer dst, ByteBuffer src, long size) {
		src.limit((int)size);
		dst.put(src);
		src.limit(src.capacity()).rewind();
	}

	private void loadModel() {

		File modelFile = new File(ClassLoader.getSystemClassLoader().getResource("models/chalet.obj").getFile());

		VKMesh model = VKModelLoader.loadModel(modelFile, aiProcess_FlipUVs | aiProcess_DropNormals);
		GingerVK.getInstance().entityRenderer.processEntity(model);
	}

	public static VKBufferMesh createVertexBuffer(VKBufferMesh processedMesh) {

		try(MemoryStack stack = stackPush()) {

			long bufferSize = VKVertex.SIZEOF * processedMesh.vertices.length;

			LongBuffer pBuffer = stack.mallocLong(1);
			LongBuffer pBufferMemory = stack.mallocLong(1);
			VKBufferUtils.createBuffer(bufferSize,
				VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
				VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
				pBuffer,
				pBufferMemory);

			long stagingBuffer = pBuffer.get(0);
			long stagingBufferMemory = pBufferMemory.get(0);

			PointerBuffer data = stack.mallocPointer(1);

			vkMapMemory(VKVariables.device, stagingBufferMemory, 0, bufferSize, 0, data);
			{
				memcpy(data.getByteBuffer(0, (int) bufferSize), processedMesh.vertices);
			}
			vkUnmapMemory(VKVariables.device, stagingBufferMemory);

			VKBufferUtils.createBuffer(bufferSize,
				VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_VERTEX_BUFFER_BIT,
				VK_MEMORY_HEAP_DEVICE_LOCAL_BIT,
				pBuffer,
				pBufferMemory);

			processedMesh.vertexBuffer = pBuffer.get(0);
			processedMesh.vertexBufferMemory = pBufferMemory.get(0);

			VKBufferUtils.copyBuffer(stagingBuffer, processedMesh.vertexBuffer, bufferSize);

			vkDestroyBuffer(VKVariables.device, stagingBuffer, null);
			vkFreeMemory(VKVariables.device, stagingBufferMemory, null);
			
			return processedMesh;
		}
	}

	public static VKBufferMesh createIndexBuffer(VKBufferMesh processedMesh) {

		try(MemoryStack stack = stackPush()) {

			long bufferSize = Integer.BYTES * processedMesh.indices.length;

			LongBuffer pBuffer = stack.mallocLong(1);
			LongBuffer pBufferMemory = stack.mallocLong(1);
			VKBufferUtils.createBuffer(bufferSize,
				VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
				VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
				pBuffer,
				pBufferMemory);

			long stagingBuffer = pBuffer.get(0);
			long stagingBufferMemory = pBufferMemory.get(0);

			PointerBuffer data = stack.mallocPointer(1);

			vkMapMemory(VKVariables.device, stagingBufferMemory, 0, bufferSize, 0, data);
			{
				memcpy(data.getByteBuffer(0, (int) bufferSize), processedMesh.indices);
			}
			vkUnmapMemory(VKVariables.device, stagingBufferMemory);

			VKBufferUtils.createBuffer(bufferSize,
				VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_INDEX_BUFFER_BIT,
				VK_MEMORY_HEAP_DEVICE_LOCAL_BIT,
				pBuffer,
				pBufferMemory);

			processedMesh.indexBuffer = pBuffer.get(0);
			processedMesh.indexBufferMemory = pBufferMemory.get(0);

			VKBufferUtils.copyBuffer(stagingBuffer, processedMesh.indexBuffer, bufferSize);

			vkDestroyBuffer(VKVariables.device, stagingBuffer, null);
			vkFreeMemory(VKVariables.device, stagingBufferMemory, null);
			return processedMesh;
		}
	}

	public static void createUniformBuffers() {

		try(MemoryStack stack = stackPush()) {

			VKVariables.uniformBuffers = new ArrayList<>(VKVariables.swapChainImages.size());
			VKVariables.uniformBuffersMemory = new ArrayList<>(VKVariables.swapChainImages.size());

			LongBuffer pBuffer = stack.mallocLong(1);
			LongBuffer pBufferMemory = stack.mallocLong(1);

			for(int i = 0;i < VKVariables.swapChainImages.size();i++) {
				VKBufferUtils.createBuffer(UniformBufferObject.SIZEOF,
					VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT,
					VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
					pBuffer,
					pBufferMemory);

				VKVariables.uniformBuffers.add(pBuffer.get(0));
				VKVariables.uniformBuffersMemory.add(pBufferMemory.get(0));
			}

		}
	}


	public static void createDescriptorPool() {

		try(MemoryStack stack = stackPush()) {

			VkDescriptorPoolSize.Buffer poolSizes = VkDescriptorPoolSize.callocStack(2, stack);

			VkDescriptorPoolSize uniformBufferPoolSize = poolSizes.get(0);
			uniformBufferPoolSize.type(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
			uniformBufferPoolSize.descriptorCount(VKVariables.swapChainImages.size());

			VkDescriptorPoolSize textureSamplerPoolSize = poolSizes.get(1);
			textureSamplerPoolSize.type(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
			textureSamplerPoolSize.descriptorCount(VKVariables.swapChainImages.size());

			VkDescriptorPoolCreateInfo poolInfo = VkDescriptorPoolCreateInfo.callocStack(stack);
			poolInfo.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO);
			poolInfo.pPoolSizes(poolSizes);
			poolInfo.maxSets(VKVariables.swapChainImages.size());

			LongBuffer pDescriptorPool = stack.mallocLong(1);

			if(vkCreateDescriptorPool(VKVariables.device, poolInfo, null, pDescriptorPool) != VK_SUCCESS) {
				throw new RuntimeException("Failed to create descriptor pool");
			}

			VKVariables.descriptorPool = pDescriptorPool.get(0);
		}
	}

	public static void createDescriptorSets() {

		try(MemoryStack stack = stackPush()) {

			LongBuffer layouts = stack.mallocLong(VKVariables.swapChainImages.size());
			for(int i = 0;i < layouts.capacity();i++) {
				layouts.put(i, VKVariables.descriptorSetLayout);
			}

			VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.callocStack(stack);
			allocInfo.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO);
			allocInfo.descriptorPool(VKVariables.descriptorPool);
			allocInfo.pSetLayouts(layouts);

			LongBuffer pDescriptorSets = stack.mallocLong(VKVariables.swapChainImages.size());

			if(vkAllocateDescriptorSets(VKVariables.device, allocInfo, pDescriptorSets) != VK_SUCCESS) {
				throw new RuntimeException("Failed to allocate descriptor sets");
			}

			VKVariables.descriptorSets = new ArrayList<>(pDescriptorSets.capacity());

			VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.callocStack(1, stack);
			bufferInfo.offset(0);
			bufferInfo.range(UniformBufferObject.SIZEOF);

			VkDescriptorImageInfo.Buffer imageInfo = VkDescriptorImageInfo.callocStack(1, stack);
			imageInfo.imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
			imageInfo.imageView(VKVariables.textureImageView);
			imageInfo.sampler(VKVariables.textureSampler);

			VkWriteDescriptorSet.Buffer descriptorWrites = VkWriteDescriptorSet.callocStack(2, stack);

			VkWriteDescriptorSet uboDescriptorWrite = descriptorWrites.get(0);
			uboDescriptorWrite.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET);
			uboDescriptorWrite.dstBinding(0);
			uboDescriptorWrite.dstArrayElement(0);
			uboDescriptorWrite.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
			uboDescriptorWrite.descriptorCount(1);
			uboDescriptorWrite.pBufferInfo(bufferInfo);

			VkWriteDescriptorSet samplerDescriptorWrite = descriptorWrites.get(1);
			samplerDescriptorWrite.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET);
			samplerDescriptorWrite.dstBinding(1);
			samplerDescriptorWrite.dstArrayElement(0);
			samplerDescriptorWrite.descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
			samplerDescriptorWrite.descriptorCount(1);
			samplerDescriptorWrite.pImageInfo(imageInfo);

			for(int i = 0;i < pDescriptorSets.capacity();i++) {

				long descriptorSet = pDescriptorSets.get(i);

				bufferInfo.buffer(VKVariables.uniformBuffers.get(i));

				uboDescriptorWrite.dstSet(descriptorSet);
				samplerDescriptorWrite.dstSet(descriptorSet);

				vkUpdateDescriptorSets(VKVariables.device, descriptorWrites, null);

				VKVariables.descriptorSets.add(descriptorSet);
			}
		}
	}

	private static void memcpy(ByteBuffer buffer, VKVertex[] vertices) {
		for(VKVertex vertex : vertices) {
			buffer.putFloat(vertex.pos.x());
			buffer.putFloat(vertex.pos.y());
			buffer.putFloat(vertex.pos.z());

			buffer.putFloat(vertex.color.x());
			buffer.putFloat(vertex.color.y());
			buffer.putFloat(vertex.color.z());

			buffer.putFloat(vertex.texCoords.x());
			buffer.putFloat(vertex.texCoords.y());
		}
	}

	private static void memcpy(ByteBuffer buffer, int[] indices) {

		for(int index : indices) {
			buffer.putInt(index);
		}

		buffer.rewind();
	}

	private static void memcpy(ByteBuffer buffer, UniformBufferObject ubo) {

		final int mat4Size = 16 * Float.BYTES;

		ubo.model.get(0, buffer);
		ubo.view.get(AlignmentUtils.alignas(mat4Size, AlignmentUtils.alignof(ubo.view)), buffer);
		ubo.proj.get(AlignmentUtils.alignas(mat4Size * 2, AlignmentUtils.alignof(ubo.view)), buffer);
	}

	public static int findMemoryType(int typeFilter, int properties) {

		VkPhysicalDeviceMemoryProperties memProperties = VkPhysicalDeviceMemoryProperties.mallocStack();
		vkGetPhysicalDeviceMemoryProperties(VKVariables.physicalDevice, memProperties);

		for(int i = 0;i < memProperties.memoryTypeCount();i++) {
			if((typeFilter & (1 << i)) != 0 && (memProperties.memoryTypes(i).propertyFlags() & properties) == properties) {
				return i;
			}
		}

		throw new RuntimeException("Failed to find suitable memory type");
	}

	

	private void createSyncObjects() {

		VKVariables.inFlightFrames = new ArrayList<>(MAX_FRAMES_IN_FLIGHT);
		VKVariables.imagesInFlight = new HashMap<>(VKVariables.swapChainImages.size());

		try(MemoryStack stack = stackPush()) {

			VkSemaphoreCreateInfo semaphoreInfo = VkSemaphoreCreateInfo.callocStack(stack);
			semaphoreInfo.sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);

			VkFenceCreateInfo fenceInfo = VkFenceCreateInfo.callocStack(stack);
			fenceInfo.sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO);
			fenceInfo.flags(VK_FENCE_CREATE_SIGNALED_BIT);

			LongBuffer pImageAvailableSemaphore = stack.mallocLong(1);
			LongBuffer pRenderFinishedSemaphore = stack.mallocLong(1);
			LongBuffer pFence = stack.mallocLong(1);

			for(int i = 0;i < MAX_FRAMES_IN_FLIGHT;i++) {

				if(vkCreateSemaphore(VKVariables.device, semaphoreInfo, null, pImageAvailableSemaphore) != VK_SUCCESS
					|| vkCreateSemaphore(VKVariables.device, semaphoreInfo, null, pRenderFinishedSemaphore) != VK_SUCCESS
					|| vkCreateFence(VKVariables.device, fenceInfo, null, pFence) != VK_SUCCESS) {

					throw new RuntimeException("Failed to create synchronization objects for the frame " + i);
				}

				VKVariables.inFlightFrames.add(new Frame(pImageAvailableSemaphore.get(0), pRenderFinishedSemaphore.get(0), pFence.get(0)));
			}

		}
	}

	public static void updateUniformBuffer(int currentImage) {

		try(MemoryStack stack = stackPush()) {

			UniformBufferObject ubo = new UniformBufferObject();

			ubo.model.rotate((float) (glfwGetTime() * Math.toRadians(90)), 0.0f, 0.0f, 1.0f);
			ubo.view.lookAt(2.0f, 2.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
			ubo.proj.perspective((float) Math.toRadians(45),
				(float)VKVariables.swapChainExtent.width() / (float)VKVariables.swapChainExtent.height(), 0.1f, 10.0f);
			ubo.proj.m11(ubo.proj.m11() * -1);

			PointerBuffer data = stack.mallocPointer(1);
			vkMapMemory(VKVariables.device, VKVariables.uniformBuffersMemory.get(currentImage), 0, UniformBufferObject.SIZEOF, 0, data);
			{
				memcpy(data.getByteBuffer(0, UniformBufferObject.SIZEOF), ubo);
			}
			vkUnmapMemory(VKVariables.device, VKVariables.uniformBuffersMemory.get(currentImage));
		}
	}

	public static VkSurfaceFormatKHR chooseSwapSurfaceFormat(VkSurfaceFormatKHR.Buffer availableFormats) {
		return availableFormats.stream()
			.filter(availableFormat -> availableFormat.format() == VK_FORMAT_B8G8R8_SRGB)
			.filter(availableFormat -> availableFormat.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)
			.findAny()
			.orElse(availableFormats.get(0));
	}

	public static int chooseSwapPresentMode(IntBuffer availablePresentModes) {

		for(int i = 0;i < availablePresentModes.capacity();i++) {
			if(availablePresentModes.get(i) == VK_PRESENT_MODE_MAILBOX_KHR) {
				return availablePresentModes.get(i);
			}
		}

		return VK_PRESENT_MODE_FIFO_KHR;
	}

	public static VkExtent2D chooseSwapExtent(VkSurfaceCapabilitiesKHR capabilities) {

		if(capabilities.currentExtent().width() != UINT32_MAX) {
			return capabilities.currentExtent();
		}

		IntBuffer width = stackGet().ints(0);
		IntBuffer height = stackGet().ints(0);

		glfwGetFramebufferSize(Window.getWindow(), width, height);

		VkExtent2D actualExtent = VkExtent2D.mallocStack().set(width.get(0), height.get(0));

		VkExtent2D minExtent = capabilities.minImageExtent();
		VkExtent2D maxExtent = capabilities.maxImageExtent();

		actualExtent.width(clamp(minExtent.width(), maxExtent.width(), actualExtent.width()));
		actualExtent.height(clamp(minExtent.height(), maxExtent.height(), actualExtent.height()));

		return actualExtent;
	}

	private static int clamp(int min, int max, int value) {
		return Math.max(min, Math.min(max, value));
	}

	public static boolean checkDeviceExtensionSupport(VkPhysicalDevice device) {

		try(MemoryStack stack = stackPush()) {

			IntBuffer extensionCount = stack.ints(0);

			vkEnumerateDeviceExtensionProperties(device, (String)null, extensionCount, null);

			VkExtensionProperties.Buffer availableExtensions = VkExtensionProperties.mallocStack(extensionCount.get(0), stack);

			return availableExtensions.stream().collect(toSet()).containsAll(DEVICE_EXTENSIONS);
		}
	}

	public static SwapChainSupportDetails querySwapChainSupport(VkPhysicalDevice device, MemoryStack stack) {

		SwapChainSupportDetails details = new SwapChainSupportDetails();

		details.capabilities = VkSurfaceCapabilitiesKHR.mallocStack(stack);
		vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, VKVariables.surface, details.capabilities);

		IntBuffer count = stack.ints(0);

		vkGetPhysicalDeviceSurfaceFormatsKHR(device, VKVariables.surface, count, null);

		if(count.get(0) != 0) {
			details.formats = VkSurfaceFormatKHR.mallocStack(count.get(0), stack);
			vkGetPhysicalDeviceSurfaceFormatsKHR(device, VKVariables.surface, count, details.formats);
		}

		vkGetPhysicalDeviceSurfacePresentModesKHR(device,VKVariables.surface, count, null);

		if(count.get(0) != 0) {
			details.presentModes = stack.mallocInt(count.get(0));
			vkGetPhysicalDeviceSurfacePresentModesKHR(device, VKVariables.surface, count, details.presentModes);
		}

		return details;
	}

	public static QueueFamilyIndices findQueueFamilies(VkPhysicalDevice device) {

		QueueFamilyIndices indices = new QueueFamilyIndices();

		try(MemoryStack stack = stackPush()) {

			IntBuffer queueFamilyCount = stack.ints(0);

			vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);

			VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.mallocStack(queueFamilyCount.get(0), stack);

			vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilies);

			IntBuffer presentSupport = stack.ints(VK_FALSE);

			for(int i = 0;i < queueFamilies.capacity() || !indices.isComplete();i++) {

				if((queueFamilies.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
					indices.graphicsFamily = i;
				}

				vkGetPhysicalDeviceSurfaceSupportKHR(device, i, VKVariables.surface, presentSupport);

				if(presentSupport.get(0) == VK_TRUE) {
					indices.presentFamily = i;
				}
			}

			return indices;
		}
	}

	public static PointerBuffer asPointerBuffer(Collection<String> collection) {

		MemoryStack stack = stackGet();

		PointerBuffer buffer = stack.mallocPointer(collection.size());

		collection.stream()
		.map(stack::UTF8)
		.forEach(buffer::put);

		return buffer.rewind();
	}

	public static PointerBuffer asPointerBuffer(List<? extends Pointer> list) {

		MemoryStack stack = stackGet();

		PointerBuffer buffer = stack.mallocPointer(list.size());

		list.forEach(buffer::put);

		return buffer.rewind();
	}

	public static PointerBuffer getRequiredExtensions() {

		PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();

		return glfwExtensions;
	}


	public static void main(String[] args) {

		VulkanExample app = new VulkanExample();

		app.run();
	}

}
