package com.github.hydos.ginger.engine.vulkan.utils;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.shaderc.Shaderc.*;
import static org.lwjgl.vulkan.EXTDebugReport.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.NVRayTracing.*;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

import java.io.IOException;
import java.nio.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.shaderc.*;
import org.lwjgl.vulkan.*;

import com.github.hydos.ginger.engine.opengl.render.tools.IOUtil;

/** @author hydos06
 *         a util library for Vulkan */
public class VKUtils
{
	public static final int VK_FLAGS_NONE = 0;

	public static long startVulkanDebugging(VkInstance instance, int flags, VkDebugReportCallbackEXT callback)
	{
		VkDebugReportCallbackCreateInfoEXT dbgCreateInfo = VkDebugReportCallbackCreateInfoEXT.calloc()
			.sType(VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT)
			.pfnCallback(callback)
			.flags(flags);
		LongBuffer pCallback = memAllocLong(1);
		int err = vkCreateDebugReportCallbackEXT(instance, dbgCreateInfo, null, pCallback);
		long callbackHandle = pCallback.get(0);
		memFree(pCallback);
		dbgCreateInfo.free();
		if (err != VK_SUCCESS)
		{ throw new AssertionError("Failed to create VkInstance: " + VKUtils.translateVulkanResult(err)); }
		return callbackHandle;
	}
	
	private static int vulkanStageToShaderc(int stage)
	{
		switch (stage)
		{
		case VK10.VK_SHADER_STAGE_VERTEX_BIT:
			return shaderc_vertex_shader;
		case VK10.VK_SHADER_STAGE_FRAGMENT_BIT:
			return shaderc_fragment_shader;
		case VK_SHADER_STAGE_RAYGEN_BIT_NV:
			return shaderc_raygen_shader;
		case VK_SHADER_STAGE_CLOSEST_HIT_BIT_NV:
			return shaderc_closesthit_shader;
		case VK_SHADER_STAGE_MISS_BIT_NV:
			return shaderc_miss_shader;
		case VK_SHADER_STAGE_ANY_HIT_BIT_NV:
			return shaderc_anyhit_shader;
		default:
			throw new IllegalArgumentException("Shader stage: " + stage);
		}
	}

	public static ByteBuffer glslToSpirv(String classPath, int vulkanStage) throws IOException
	{
		System.out.println("Converting shader: " + classPath + " to SPIRV");
		ByteBuffer src = IOUtil.ioResourceToByteBuffer(classPath, 1024);
		long compiler = shaderc_compiler_initialize();
		long options = shaderc_compile_options_initialize();
		ShadercIncludeResolve resolver;
		ShadercIncludeResultRelease releaser;
		shaderc_compile_options_set_optimization_level(options, shaderc_optimization_level_performance);
		shaderc_compile_options_set_include_callbacks(options, resolver = new ShadercIncludeResolve()
		{
			public long invoke(long user_data, long requested_source, int type, long requesting_source, long include_depth)
			{
				ShadercIncludeResult res = ShadercIncludeResult.calloc();
				try
				{
					String src = classPath.substring(0, classPath.lastIndexOf('/')) + "/" + memUTF8(requested_source);
					res.content(IOUtil.ioResourceToByteBuffer(src, 1024));
					res.source_name(memUTF8(src));
					return res.address();
				}
				catch (IOException e)
				{
					throw new AssertionError("Failed to resolve include: " + src);
				}
			}
		}, releaser = new ShadercIncludeResultRelease()
		{
			public void invoke(long user_data, long include_result)
			{
				ShadercIncludeResult result = ShadercIncludeResult.create(include_result);
				memFree(result.source_name());
				result.free();
			}
		}, 0L);
		long res;
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			res = shaderc_compile_into_spv(compiler, src, vulkanStageToShaderc(vulkanStage),
				stack.UTF8(classPath), stack.UTF8("main"), options);
			if (res == 0L)
				throw new AssertionError("Internal error during compilation!");
		}
		if (shaderc_result_get_compilation_status(res) != shaderc_compilation_status_success)
		{ throw new AssertionError("Shader compilation failed: " + shaderc_result_get_error_message(res)); }
		int size = (int) shaderc_result_get_length(res);
		ByteBuffer resultBytes = BufferUtils.createByteBuffer(size);
		resultBytes.put(shaderc_result_get_bytes(res));
		resultBytes.flip();
		shaderc_compiler_release(res);
		shaderc_compiler_release(compiler);
		releaser.free();
		resolver.free();
		return resultBytes;
	}

	public static String translateVulkanResult(int vulkanResult)
	{
		switch (vulkanResult)
		{
		case VK10.VK_SUCCESS:
			return "Command successfully completed.";
		case VK10.VK_NOT_READY:
			return "A query has not yet been completed.";
		case VK10.VK_TIMEOUT:
			return "A wait operation has timed out.";
		case VK10.VK_INCOMPLETE:
			return "A return array was too small for the result.";
		case KHRSwapchain.VK_SUBOPTIMAL_KHR:
			return "A swapchain no longer matches the surface properties exactly, but can still be used to present to the surface successfully.";
		case VK10.VK_ERROR_OUT_OF_HOST_MEMORY:
			return "A host memory allocation has failed.";
		case VK10.VK_ERROR_OUT_OF_DEVICE_MEMORY:
			return "A device memory allocation has failed.";
		case VK10.VK_ERROR_INITIALIZATION_FAILED:
			return "Initialization of an object could not be completed for implementation-specific reasons.";
		case VK10.VK_ERROR_DEVICE_LOST:
			return "The logical or physical device has been lost.";
		case VK10.VK_ERROR_MEMORY_MAP_FAILED:
			return "Mapping of a memory object has failed.";
		case VK10.VK_ERROR_LAYER_NOT_PRESENT:
			return "A requested layer is not present or could not be loaded.";
		case VK10.VK_ERROR_EXTENSION_NOT_PRESENT:
			return "A requested extension is not supported.";
		case VK10.VK_ERROR_FEATURE_NOT_PRESENT:
			return "A requested feature is not supported.";
		case VK10.VK_ERROR_INCOMPATIBLE_DRIVER:
			return "The requested version of Vulkan is not supported by the driver or is otherwise incompatible for implementation-specific reasons.";
		case VK10.VK_ERROR_TOO_MANY_OBJECTS:
			return "Too many objects of the same type have already been created.";
		case VK10.VK_ERROR_FORMAT_NOT_SUPPORTED:
			return "The requested format is not supported.";
		case VK_ERROR_SURFACE_LOST_KHR:
			return "The window is no longer available.";
		case VK_ERROR_NATIVE_WINDOW_IN_USE_KHR:
			return "The requested window is already connected to a VkSurfaceKHR, or to some other non-Vulkan API.";
		case KHRDisplaySwapchain.VK_ERROR_INCOMPATIBLE_DISPLAY_KHR:
			return "The display used by a swapchain does not use the same presentable image layout, or is incompatible in a way that prevents sharing an" + " image.";
		case EXTDebugReport.VK_ERROR_VALIDATION_FAILED_EXT:
			return "A validation layer found an error.";
		default:
			return String.format("%s [%d]", "Is an unknown vulkan result", Integer.valueOf(vulkanResult));
		}
	}
}