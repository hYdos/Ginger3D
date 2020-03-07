package com.github.hydos.ginger.engine.vulkan.render;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.*;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import com.github.hydos.ginger.VulkanExample;
import com.github.hydos.ginger.engine.vulkan.VKVariables;
import com.github.hydos.ginger.engine.vulkan.swapchain.VKSwapchainManager;
import com.github.hydos.ginger.engine.vulkan.utils.VKUtils;

/**
 * Wraps the needed sync objects for an in flight frame
 *
 * This frame's sync objects must be deleted manually
 * */
public class Frame {

    private final long imageAvailableSemaphore;
    private final long renderFinishedSemaphore;
    private final long fence;

    public Frame(long imageAvailableSemaphore, long renderFinishedSemaphore, long fence) {
        this.imageAvailableSemaphore = imageAvailableSemaphore;
        this.renderFinishedSemaphore = renderFinishedSemaphore;
        this.fence = fence;
    }

    public long imageAvailableSemaphore() {
        return imageAvailableSemaphore;
    }

    public LongBuffer pImageAvailableSemaphore() {
        return stackGet().longs(imageAvailableSemaphore);
    }

    public long renderFinishedSemaphore() {
        return renderFinishedSemaphore;
    }

    public LongBuffer pRenderFinishedSemaphore() {
        return stackGet().longs(renderFinishedSemaphore);
    }

    public long fence() {
        return fence;
    }

    public LongBuffer pFence() {
        return stackGet().longs(fence);
    }
    
	public static void drawFrame() {

		try(MemoryStack stack = stackPush()) {

			Frame thisFrame = VKVariables.inFlightFrames.get(VKVariables.currentFrame);

			vkWaitForFences(VKVariables.device, thisFrame.pFence(), true, VulkanExample.UINT64_MAX);

			IntBuffer pImageIndex = stack.mallocInt(1);


			int vkResult = vkAcquireNextImageKHR(VKVariables.device, VKVariables.swapChain, VulkanExample.UINT64_MAX,
				thisFrame.imageAvailableSemaphore(), VK_NULL_HANDLE, pImageIndex);

			if(vkResult == VK_ERROR_OUT_OF_DATE_KHR) {
				VKSwapchainManager.recreateSwapChain();
				return;
			}

			final int imageIndex = pImageIndex.get(0);
			VKVariables.currentImageIndex = imageIndex;
			VKUtils.updateUniformBuffer(VKVariables.currentImageIndex, null);//TODO: move this to entitiy renderer and update before every entity is drawn

			if(VKVariables.imagesInFlight.containsKey(imageIndex)) {
				vkWaitForFences(VKVariables.device, VKVariables.imagesInFlight.get(imageIndex).fence(), true, VulkanExample.UINT64_MAX);
			}

			VKVariables.imagesInFlight.put(imageIndex, thisFrame);

			VkSubmitInfo submitInfo = VkSubmitInfo.callocStack(stack);
			submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);

			submitInfo.waitSemaphoreCount(1);
			submitInfo.pWaitSemaphores(thisFrame.pImageAvailableSemaphore());
			submitInfo.pWaitDstStageMask(stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT));

			submitInfo.pSignalSemaphores(thisFrame.pRenderFinishedSemaphore());

			submitInfo.pCommandBuffers(stack.pointers(VKVariables.commandBuffers.get(imageIndex)));

			vkResetFences(VKVariables.device, thisFrame.pFence());

			if((vkResult = vkQueueSubmit(VKVariables.graphicsQueue, submitInfo, thisFrame.fence())) != VK_SUCCESS) {
				vkResetFences(VKVariables.device, thisFrame.pFence());
				throw new RuntimeException("Failed to submit draw command buffer: " + vkResult);
			}

			VkPresentInfoKHR presentInfo = VkPresentInfoKHR.callocStack(stack);
			presentInfo.sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR);

			presentInfo.pWaitSemaphores(thisFrame.pRenderFinishedSemaphore());

			presentInfo.swapchainCount(1);
			presentInfo.pSwapchains(stack.longs(VKVariables.swapChain));

			presentInfo.pImageIndices(pImageIndex);

			vkResult = vkQueuePresentKHR(VKVariables.presentQueue, presentInfo);

			if(vkResult == VK_ERROR_OUT_OF_DATE_KHR || vkResult == VK_SUBOPTIMAL_KHR || VKVariables.framebufferResize) {
				VKVariables.framebufferResize = false;
				VKSwapchainManager.recreateSwapChain();
			} else if(vkResult != VK_SUCCESS) {
				throw new RuntimeException("Failed to present swap chain image");
			}

			VKVariables.currentFrame = (VKVariables.currentFrame + 1) % VulkanExample.MAX_FRAMES_IN_FLIGHT;
		}
	}
    
}
