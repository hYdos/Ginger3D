package com.github.hydos.ginger;

import com.github.hydos.ginger.engine.common.info.RenderAPI;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.vulkan.VKRegister;
import com.github.hydos.ginger.engine.vulkan.VKVariables;
import com.github.hydos.ginger.engine.vulkan.elements.VKRenderObject;
import com.github.hydos.ginger.engine.vulkan.io.VKWindow;
import com.github.hydos.ginger.engine.vulkan.managers.VKTextureManager;
import com.github.hydos.ginger.engine.vulkan.model.VKModelLoader;
import com.github.hydos.ginger.engine.vulkan.model.VKModelLoader.VKMesh;
import com.github.hydos.ginger.engine.vulkan.render.Frame;
import com.github.hydos.ginger.engine.vulkan.swapchain.VKSwapchainManager;
import com.github.hydos.ginger.engine.vulkan.utils.VKDeviceManager;
import com.github.hydos.ginger.engine.vulkan.utils.VKUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.lwjgl.assimp.Assimp.aiProcess_DropNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_FlipUVs;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.vkDeviceWaitIdle;

public class VulkanExample {

    public static final int UINT32_MAX = 0xFFFFFFFF;
    public static final long UINT64_MAX = 0xFFFFFFFFFFFFFFFFL;

    public static final int MAX_FRAMES_IN_FLIGHT = 2;

    public static final Set<String> DEVICE_EXTENSIONS = Stream.of(VK_KHR_SWAPCHAIN_EXTENSION_NAME).collect(toSet());

    public static void main(String[] args) {
        VulkanExample app = new VulkanExample();

        app.run();
    }

    public void run() {
        initWindow();
        initVulkan();
        mainLoop();
        GingerVK.getInstance().cleanup();
        VKUtils.cleanup();
    }

    private void loadModels() {

        File modelFile = new File(ClassLoader.getSystemClassLoader().getResource("models/chalet.obj").getFile());

        VKMesh model = VKModelLoader.loadModel(modelFile, aiProcess_FlipUVs | aiProcess_DropNormals);
        VKRenderObject object = new VKRenderObject(model, new Vector3f(), 1, 1, 1, new Vector3f());
        GingerVK.getInstance().entityRenderer.processEntity(object);
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
        VKUtils.createUBODescriptorSetLayout();
        loadModels();
        VKSwapchainManager.createSwapChainObjects(GingerVK.getInstance().entityRenderer);
        VKUtils.createSyncObjects();
    }

    private void mainLoop() {

        while (!Window.closed()) {
            if (Window.shouldRender()) {
                Frame.drawFrame();
            }
            glfwPollEvents();
        }

        // Wait for the device to complete all operations before release resources
        vkDeviceWaitIdle(VKVariables.device);
    }

    public static class QueueFamilyIndices {

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

    public static class UniformBufferObject {

        public static final int SIZEOF = 3 * 16 * Float.BYTES;

        public Matrix4f model;
        public Matrix4f view;
        public Matrix4f proj;

        public UniformBufferObject() {
            model = new Matrix4f();
            view = new Matrix4f();
            proj = new Matrix4f();
        }

        public UniformBufferObject(VKRenderObject renderObject) {
            model = new Matrix4f();
            view = new Matrix4f();
            proj = new Matrix4f();
            if(renderObject != null){
                model.translate(renderObject.position);
            }
        }
    }

}
