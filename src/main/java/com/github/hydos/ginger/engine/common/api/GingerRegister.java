package com.github.hydos.ginger.engine.common.api;

import com.github.hydos.ginger.engine.common.api.game.Game;
import com.github.hydos.ginger.engine.common.elements.buttons.TextureButton;
import com.github.hydos.ginger.engine.common.font.GUIText;
import com.github.hydos.ginger.engine.common.screen.Screen;
import com.github.hydos.ginger.engine.opengl.postprocessing.FrameBufferObject;
import com.github.hydos.ginger.engine.opengl.render.GLRenderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Used if a game wants to access engine variables safely
 */
public class GingerRegister {
    private static GingerRegister INSTANCE;
    public GLRenderManager masterRenderer;
    public List<GUIText> texts;
    public List<TextureButton> guiButtons;
    public List<FrameBufferObject> frameBufferObjects;
    public Game game;
    public Screen currentScreen;
    public boolean wireframe = false;
    public GingerRegister() {
        INSTANCE = this;
    }

    public static GingerRegister getInstance() {
        return INSTANCE;
    }

    public void registerButton(TextureButton button) {
        if (guiButtons == null) guiButtons = new ArrayList<>();
        guiButtons.add(button);
    }

    public void registerGame(Game game) {
        this.game = game;
    }

    public void registerText(GUIText guiText) {
        if (texts == null) texts = new ArrayList<>();
        texts.add(guiText);
    }

    public void removeText(GUIText text) {
        texts.remove(text);
    }

    public TextureButton retrieveButton(String string) {
        for (TextureButton button : guiButtons) {
            if (button.resourceLocation == string) {
                return button;
            }
        }
        return null;
    }

    public void toggleWireframe() {
        this.wireframe = !this.wireframe;
    }

    public GUIText retrieveText(String string) {
        for (GUIText text : texts) {
            if (string.equalsIgnoreCase(string)) {
                return text;
            }
        }
        return null;
    }
}