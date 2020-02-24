package com.github.hydos.ginger.engine.api;

import java.util.*;

import com.github.hydos.ginger.engine.elements.buttons.TextureButton;
import com.github.hydos.ginger.engine.font.GUIText;

/**
 * Used if a game wants to access engine variables safely
 */
public class GingerRegister {
	
	private static GingerRegister INSTANCE;
	
	public List<GUIText> texts;
	public List<TextureButton> guiButtons;

	public Game game;
	
	public GingerRegister() {
		INSTANCE = this;
	}
	
	public void registerText(GUIText guiText) {
		if(texts == null) texts = new ArrayList<GUIText>();
		texts.add(guiText);
	}

	public GUIText retrieveText(String string) {
		GUIText lastText = null;
		for(GUIText text: texts) {
			lastText = text;
			System.out.println(string + ":" + lastText.textID);
			if(string.equalsIgnoreCase(string)) {
				return text;
			}
		}
		return null;
	}
	
	public void removeText(GUIText text) {
		texts.remove(text);
	}

	public void registerButton(TextureButton button) {
		if(guiButtons == null) guiButtons = new ArrayList<TextureButton>();
		guiButtons.add(button);
	}

	public TextureButton retrieveButton(String string) {
		for(TextureButton button: guiButtons) {
			if(button.resourceLocation == string) {
				return button;
			}
		}
		return null;
	}
	
	public static GingerRegister getInstance() {
		return INSTANCE;
	}

	public void registerGame(Game game) {this.game = game;}
	
	
}
