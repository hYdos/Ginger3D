package tk.valoeghese.gateways.client.io;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

/**
 * Author: Valoeghese
 */
public class MouseCallbackHandler extends GLFWMouseButtonCallback {
    private static final MouseCallbackHandler INSTANCE = new MouseCallbackHandler();
    public static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

    private MouseCallbackHandler() {
    }

    public static void trackWindow(long window) {
        GLFW.glfwSetMouseButtonCallback(window, INSTANCE);
    }

    @Override
    public void invoke(long window, int button, int action, int mods) {
        buttons[button] = action != GLFW.GLFW_RELEASE;
    }
}
