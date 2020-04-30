package com.github.hydos.ginger.engine.common.api;

import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.common.screen.Screen;
import com.github.hydos.ginger.engine.common.util.Timer;
import com.github.hydos.ginger.engine.common.util.Timer.TickListener;
import com.github.hydos.ginger.engine.opengl.utils.GLUtils;

public abstract class GingerEngine
{
	protected static GingerEngine INSTANCE;
	protected GingerRegister registry;
	
	public static GingerEngine getInstance()
	{
		return INSTANCE;
	}
	
	public Timer timer;
	protected TickListener gameTickListener = new TickListener()
	{
		@Override
		public void onTick(float deltaTime)
		{
			if (GingerRegister.getInstance().game != null) GingerRegister.getInstance().game.tick();
			if (GingerRegister.getInstance().currentScreen != null) GingerRegister.getInstance().currentScreen.tick();
		};
	};
	
	public void startGameLoop()
	{
		while (Window.closed())
		{
			update(); // Run this regardless, (so as fast as possible)
			timer.tick(); // Run this only [ticklimit] times per second (This invokes gameTickListener.onTick!)
			if (Window.shouldRender()) GingerRegister.getInstance().game.render(); // Run this only [framelimit] times per second
		}
		GingerRegister.getInstance().game.exit();
	}

	// Things that should be run as often as possible, without limits
	public void update()
	{
		GLUtils.update();
	}

	public abstract void cleanup();

	public abstract void openScreen(Screen screen);

	public abstract void renderOverlays();
}
