package com.github.fulira.litecraft.screens;

import java.util.ArrayList;

import org.joml.*;

import com.github.fulira.litecraft.Litecraft;
import com.github.fulira.litecraft.save.LitecraftSave;
import com.github.fulira.litecraft.world.dimension.Dimensions;
import com.github.hydos.ginger.engine.common.api.GingerEngine;
import com.github.hydos.ginger.engine.common.elements.GLGuiTexture;
import com.github.hydos.ginger.engine.common.elements.buttons.TextureButton;
import com.github.hydos.ginger.engine.common.font.GUIText;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.common.screen.Screen;
import com.github.hydos.ginger.engine.opengl.api.GingerGL;

/**
 * YeS
 */
public class TitleScreen extends Screen {
	private GUIText debugText;
	// TODO: Add Vulkan text renderer
	private GingerEngine engine = GingerGL.getInstance();
	private TextureButton playButton;
	private Litecraft litecraft = Litecraft.getInstance();

	public TitleScreen() {
		elements = new ArrayList<GLGuiTexture>();
		playButton = ((GingerGL) engine).registerButton("/textures/guis/playbutton.png", new Vector2f(0, 0),
				new Vector2f(0.25f, 0.1f));
		playButton.show(Litecraft.getInstance().data.guis);
		debugText = ((GingerGL) engine).registerText("Loading...", 2, new Vector2f(0, 0), 1f, true, "debugInfo");
		debugText.setBorderWidth(0.5f);
	}

	@Override
	public void render() {
	}

	@Override
	public void tick() {
		Vector4i dbg = litecraft.dbgStats;
		debugText.setText("FPS: " + dbg.x() + " UPS: " + dbg.y() + " TPS: " + dbg.z() + " TWL: " + dbg.w());
		playButton.update();
		if (playButton.isClicked()) {
			Window.lockMouse();

			if (Litecraft.getInstance().getWorld() == null) {
				Litecraft.getInstance().setSave(new LitecraftSave("SegregatedOrdinalData", false));
				Litecraft.getInstance()
						.changeWorld(Litecraft.getInstance().getSave().getWorldOrCreate(Dimensions.OVERWORLD));
				((GingerGL) engine).setGingerPlayer(Litecraft.getInstance().getWorld().playerEntity);
			}
			if (Litecraft.getInstance().getWorld() != null) {
				((GingerGL) engine).openScreen(new IngameHUD());
				this.cleanup();
			}
			// TODO: add world creation gui so it takes u to world creation place
			// TODO: add a texture to be rendered behind the gui as an option
		}
	}

	@Override
	public void cleanup() {
		this.debugText.remove();
		this.playButton.hide(Litecraft.getInstance().data.guis);
	}
}
