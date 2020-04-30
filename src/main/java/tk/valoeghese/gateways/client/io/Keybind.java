package tk.valoeghese.gateways.client.io;

import org.lwjgl.glfw.GLFW;

/**
 * Author: Valoeghese
 */
public enum Keybind
{
	MOVE_FORWARD(GLFW.GLFW_KEY_W, false), // Move the player forward relative to its facing direction
	MOVE_BACKWARD(GLFW.GLFW_KEY_S, false), // Move the player backward relative to its facing direction
	STRAFE_LEFT(GLFW.GLFW_KEY_A, false), // Move the player left relative to its facing direction
	STRAFE_RIGHT(GLFW.GLFW_KEY_D, false), // Move the player right relative to its facing direction
	MINECRAFT(GLFW.GLFW_KEY_V, false),
	FLY_UP(GLFW.GLFW_KEY_SPACE, false), // Move the player upward
	FLY_DOWN(GLFW.GLFW_KEY_LEFT_SHIFT, false), // Move the player downward
	SLOT_1(GLFW.GLFW_KEY_1, false),  // Select the first item slot in the toolbar
	SLOT_2(GLFW.GLFW_KEY_2, false),  // Select the second item slot in the toolbar
	SLOT_3(GLFW.GLFW_KEY_3, false),  // Select the third item slot in the toolbar
	SLOT_4(GLFW.GLFW_KEY_4, false),  // Select the fourth item slot in the toolbar
	SLOT_5(GLFW.GLFW_KEY_5, false),  // Select the fifth item slot in the toolbar
	SLOT_6(GLFW.GLFW_KEY_6, false),  // Select the sixth item slot in the toolbar
	SLOT_7(GLFW.GLFW_KEY_7, false),  // Select the seventh item slot in the toolbar
	SLOT_8(GLFW.GLFW_KEY_8, false),  // Select the eighth item slot in the toolbar
	SLOT_9(GLFW.GLFW_KEY_9, false),  // Select the ninth item slot in the toolbar
	SLOT_10(GLFW.GLFW_KEY_0, false), // Select the tenth item slot in the toolbar
	EXIT(GLFW.GLFW_KEY_ESCAPE, false), // Save and exit the game // (Open the pause menu later)
	DEBUG(GLFW.GLFW_KEY_F3, false), // Toggle debug text onscreen
	FULLSCREEN(GLFW.GLFW_KEY_F11, false), // Toggle fullscreen mode
	WIREFRAME(GLFW.GLFW_KEY_TAB, false); // Toggle wireframe

	public int value;
	public boolean mouse;

	Keybind(int initValue, boolean isMouse)
	{
		this.value = initValue;
		this.mouse = isMouse;
	}

	public boolean isActive()
	{ return mouse ? MouseCallbackHandler.buttons[value] : KeyCallbackHandler.keys[value]; }
}
