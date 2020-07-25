package com.github.hydos.ginger.engine.opengl.api;

import com.github.hydos.ginger.engine.common.api.GingerEngine;
import com.github.hydos.ginger.engine.common.api.GingerRegister;
import com.github.hydos.ginger.engine.common.api.game.Game;
import com.github.hydos.ginger.engine.common.elements.buttons.TextureButton;
import com.github.hydos.ginger.engine.common.elements.objects.GLRenderObject;
import com.github.hydos.ginger.engine.common.font.FontType;
import com.github.hydos.ginger.engine.common.font.GUIText;
import com.github.hydos.ginger.engine.common.font.TextMaster;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.common.screen.Screen;
import com.github.hydos.ginger.engine.common.tools.MousePicker;
import com.github.hydos.ginger.engine.common.util.Timer;
import com.github.hydos.ginger.engine.opengl.postprocessing.ContrastChanger;
import com.github.hydos.ginger.engine.opengl.postprocessing.FrameBufferObject;
import com.github.hydos.ginger.engine.opengl.postprocessing.PostProcessing;
import com.github.hydos.ginger.engine.opengl.render.GLRenderManager;
import com.github.hydos.ginger.engine.opengl.utils.GLLoader;
import org.joml.Vector2f;

public class GingerGL extends GingerEngine {
    public MousePicker picker;
    public FontType globalFont;
    public FrameBufferObject contrastFbo;

    public void cleanup() {
        Window.stop();
        PostProcessing.cleanUp();
        getRegistry().masterRenderer.cleanUp();
        TextMaster.cleanUp();
        GLLoader.cleanUp();
    }

    public void openScreen(Screen screen) {
        if (getRegistry().currentScreen != null) getRegistry().currentScreen.cleanup();
        getRegistry().currentScreen = screen;
    }

    public void setGingerPlayer(GLRenderObject player) {
        registry.game.data.entities.remove(registry.game.player); // remove the old player
        registry.game.data.playerObject = player; // set all the player variables
        registry.game.player = player;
        registry.game.camera.player = player;
        registry.game.data.entities.add(player); // add the new player
    }

    public TextureButton registerButton(String resourceLocation, Vector2f position, Vector2f scale) {
        TextureButton button = new TextureButton(resourceLocation, position, scale);
        getRegistry().registerButton(button);
        return button;
    }

    public GUIText registerText(String string, int textSize, Vector2f position, float maxLineLength, boolean centered, String id) {
        GUIText text = new GUIText(string, textSize, globalFont, position, maxLineLength, false);
        text.textID = id;
        getRegistry().registerText(text);
        return text;
    }

    public void renderOverlays() {
        getRegistry().masterRenderer.renderGuis(getRegistry().game.data.guis);
        if (getRegistry().currentScreen != null)
            getRegistry().masterRenderer.renderGuis(getRegistry().currentScreen.elements);
        TextMaster.render();
    }

    public void setGlobalFont(FontType font) {
        this.globalFont = font;
    }

    public void setup(GLRenderManager masterRenderer, Game game) {
        INSTANCE = this;
        registry = new GingerRegister();
        getRegistry().registerGame(game);
        timer = new Timer(game.data.tickSpeed);
        timer.addTickListener(gameTickListener);
        contrastFbo = new FrameBufferObject(new ContrastChanger());
        getRegistry().masterRenderer = masterRenderer;
        picker = new MousePicker(game.data.camera, masterRenderer.getProjectionMatrix());
        PostProcessing.init();
    }

    @Override
    public void update() {
        getRegistry().game.update();
        picker.update();
        super.update();
    }

    public GingerRegister getRegistry() {
        return registry;
    }
}