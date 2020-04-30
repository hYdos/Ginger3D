package com.github.hydos.ginger.engine.common.io;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.joml.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.github.hydos.ginger.engine.common.info.RenderAPI;
import com.github.hydos.ginger.engine.opengl.api.GingerGL;

@Environment(EnvType.CLIENT)
public class Window
{
	public static RenderAPI renderAPI = RenderAPI.OpenGL;
	private static double processedTime;
	private static double fpsCap = 60;

	public static int getWidth()
	{ return MinecraftClient.getInstance().getWindow().getWidth(); }

	public static int getHeight()
	{ return MinecraftClient.getInstance().getWindow().getHeight(); }

	private static final Vector3f backgroundColour = new Vector3f(118f, 215f, 234f);
	public static double dy = 0;
	public static double dx = 0;
	static double oldX = 0;
	static double oldY = 0;
	static double newX = 0;
	static double time = 0;
	static double newY = 0;
	public static GLCapabilities glContext;
	public static int actualWidth, actualHeight;
	private static int oldWindowWidth = Window.getWidth();
	private static int oldWindowHeight = Window.getHeight();

	public static boolean closed()
	{ return !GLFW.glfwWindowShouldClose(getWindow()); }

	public static void create()
	{
		time = getTime();
		getCurrentTime();
		oldWindowWidth = getWidth();
		oldWindowHeight = getHeight();
	}

	public static Vector3f getColour()
	{ return Window.backgroundColour; }

	private static void getCurrentTime()
	{
		GLFW.glfwGetTime();
		GLFW.glfwGetTimerFrequency();
	}

	public static double getMouseX()
	{
		return 0;
	}

	public static double getMouseY()
	{
		return 0;
	}

	public static Vector2f getNormalizedMouseCoordinates()
	{
		float normalX = -1.0f + 2.0f * (float) getMouseX() / getWidth();
		float normalY = 1.0f - 2.0f * (float) getMouseY() / getHeight();
		return new Vector2f(normalX, normalY);
	}

	public static double getTime()
	{
		return (double) System.nanoTime() / (long) 1000000000;
	}

	public static boolean isKeyDown()
	{return false;}

	public static boolean isMouseDown()
	{return true;}

	public static boolean isMousePressed()
	{return true;}


	public static boolean shouldRender()
	{
		double nextTime = getTime();
		double passedTime = nextTime - time;
		processedTime += passedTime;
		time = nextTime;
		if (processedTime > 1.0 / fpsCap)
		{
			processedTime -= 1.0 / fpsCap;
			return true;
		}
		return false;
	}

	public static void lockMouse()
	{}

	public static void stop()
	{}

	public static void update()
	{
			if ((oldWindowHeight != Window.getHeight() || oldWindowWidth != Window.getWidth()) && Window.getHeight() > 10 && Window.getWidth() > 10)
			{
				((GingerGL)GingerGL.getInstance()).contrastFbo.resizeFBOs();
				oldWindowWidth = Window.getWidth();
				oldWindowHeight = Window.getHeight();
			}
			GL11.glViewport(0, 0, getWidth(), getHeight());
			GL11.glClearColor(backgroundColour.x/255, backgroundColour.y/255, backgroundColour.z/255, 1.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		newX = Window.getMouseX();
		newY = Window.getMouseY();
		Window.dx = newX - oldX;
		Window.dy = newY - oldY;
		oldX = newX;
		oldY = newY;
	}


	public static long getWindow()
	{
		return MinecraftClient.getInstance().getWindow().getHandle();
	}

	public static void destroy()
	{
		GLFW.glfwDestroyWindow(Window.getWindow());

		GLFW.glfwTerminate();
	}
}
