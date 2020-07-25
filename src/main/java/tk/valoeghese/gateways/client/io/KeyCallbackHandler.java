package tk.valoeghese.gateways.client.io;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

/**
 * Author: Valoeghese
 */
public class KeyCallbackHandler extends GLFWKeyCallback {
    private static final KeyCallbackHandler INSTANCE = new KeyCallbackHandler();
    public static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];

    private KeyCallbackHandler() {
    }

    public static void trackWindow(long window) {
        GLFW.glfwSetKeyCallback(window, INSTANCE);
    }

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        try {
            keys[key] = action != GLFW.GLFW_RELEASE;
        } catch (ArrayIndexOutOfBoundsException e) {
            // Probably just changing the volume
        }
    }
}
