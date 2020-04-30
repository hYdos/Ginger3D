package tk.valoeghese.gateways.client.io;

import org.lwjgl.glfw.*;

/**
 * Author: Valoeghese
 */
public class MouseCallbackHandler
{
	private static final MouseCallbackHandler INSTANCE = new MouseCallbackHandler();
	public static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];


//	@Override
//	public void invoke(long window, int button, int action, int mods)
//	{ buttons[button] = action != GLFW.GLFW_RELEASE; }
}
