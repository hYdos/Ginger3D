package com.github.hydos.ginger.engine.vulkan.managers;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_GRAPHICS;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_CONTENTS_INLINE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkAllocateCommandBuffers;
import static org.lwjgl.vulkan.VK10.vkBeginCommandBuffer;
import static org.lwjgl.vulkan.VK10.vkCmdBeginRenderPass;
import static org.lwjgl.vulkan.VK10.vkCmdBindPipeline;
import static org.lwjgl.vulkan.VK10.vkCmdEndRenderPass;
import static org.lwjgl.vulkan.VK10.vkEndCommandBuffer;
import static org.lwjgl.vulkan.VK10.vkFreeCommandBuffers;
import static org.lwjgl.vulkan.VK10.vkQueueSubmit;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import java.util.ArrayList;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkOffset2D;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;
import org.lwjgl.vulkan.VkSubmitInfo;

import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.vulkan.VKVariables;

public class CommandBufferManager
{
	
	public static void createCommandBuffers() {

		final int commandBuffersCount = VKVariables.swapChainFramebuffers.size();

		VKVariables.commandBuffers = new ArrayList<>(commandBuffersCount);

		try(MemoryStack stack = stackPush()) {

			VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.callocStack(stack);
			allocInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO);
			allocInfo.commandPool(VKVariables.commandPool);
			allocInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
			allocInfo.commandBufferCount(commandBuffersCount);

			PointerBuffer pCommandBuffers = stack.mallocPointer(commandBuffersCount);

			if(vkAllocateCommandBuffers(VKVariables.device, allocInfo, pCommandBuffers) != VK_SUCCESS) {
				throw new RuntimeException("Failed to allocate command buffers");
			}

			for(int i = 0;i < commandBuffersCount;i++) {
				VKVariables.commandBuffers.add(new VkCommandBuffer(pCommandBuffers.get(i), VKVariables.device));
			}

			VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.callocStack(stack);
			beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);

			VkRenderPassBeginInfo renderPassInfo = VkRenderPassBeginInfo.callocStack(stack);
			renderPassInfo.sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO);

			renderPassInfo.renderPass(VKVariables.renderPass);

			VkRect2D renderArea = VkRect2D.callocStack(stack);
			renderArea.offset(VkOffset2D.callocStack(stack).set(0, 0));
			renderArea.extent(VKVariables.swapChainExtent);
			renderPassInfo.renderArea(renderArea);

			VkClearValue.Buffer clearValues = VkClearValue.callocStack(2, stack);
			clearValues.get(0).color().float32(stack.floats(Window.getColour().x / 255, Window.getColour().y / 255, Window.getColour().z / 255, 1.0f)); //The screens clear colour
			clearValues.get(1).depthStencil().set(1.0f, 0);

			renderPassInfo.pClearValues(clearValues);

			for(int i = 0;i < commandBuffersCount;i++) {

				VkCommandBuffer commandBuffer = VKVariables.commandBuffers.get(i);

				if(vkBeginCommandBuffer(commandBuffer, beginInfo) != VK_SUCCESS) {
					throw new RuntimeException("Failed to begin recording command buffer");
				}

				renderPassInfo.framebuffer(VKVariables.swapChainFramebuffers.get(i));


				vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE);
				{
					vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, VKVariables.graphicsPipeline);
					VKVariables.renderManager.render(stack, commandBuffer, i);
				}
				vkCmdEndRenderPass(commandBuffer);


				if(vkEndCommandBuffer(commandBuffer) != VK_SUCCESS) {
					throw new RuntimeException("Failed to record command buffer");
				}

			}

		}
	}
	
	public static VkCommandBuffer beginSingleTimeCommands() {

		try(MemoryStack stack = stackPush()) {

			VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.callocStack(stack);
			allocInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO);
			allocInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
			allocInfo.commandPool(VKVariables.commandPool);
			allocInfo.commandBufferCount(1);

			PointerBuffer pCommandBuffer = stack.mallocPointer(1);
			vkAllocateCommandBuffers(VKVariables.device, allocInfo, pCommandBuffer);
			VkCommandBuffer commandBuffer = new VkCommandBuffer(pCommandBuffer.get(0), VKVariables.device);

			VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.callocStack(stack);
			beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
			beginInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

			vkBeginCommandBuffer(commandBuffer, beginInfo);

			return commandBuffer;
		}
	}

	public static void endSingleTimeCommands(VkCommandBuffer commandBuffer) {

		try(MemoryStack stack = stackPush()) {

			vkEndCommandBuffer(commandBuffer);

			VkSubmitInfo.Buffer submitInfo = VkSubmitInfo.callocStack(1, stack);
			submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
			submitInfo.pCommandBuffers(stack.pointers(commandBuffer));

			vkQueueSubmit(VKVariables.graphicsQueue, submitInfo, VK_NULL_HANDLE);
			vkQueueWaitIdle(VKVariables.graphicsQueue);

			vkFreeCommandBuffers(VKVariables.device, VKVariables.commandPool, commandBuffer);
		}
	}
	
}
