package com.github.hydos.ginger.engine.font;

import java.util.*;

import com.github.hydos.ginger.engine.render.renderers.FontRenderer;
import com.github.hydos.ginger.engine.utils.Loader;

public class TextRegister {
	
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;
	
	public static void init() {
		renderer = new FontRenderer();
	}
	
	public static void loadText(GUIText text) {
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = Loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		List<GUIText> textBatch = texts.get(font);
		if(textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}
	
	public static void removeText(GUIText text) {
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		if(textBatch.isEmpty()) {
			texts.remove(text.getFont());
		}
	}
	
	public static void cleanUp() {
		renderer.cleanUp();
	}
	
	public static void render() {
		renderer.render(texts);
	}
	
}
