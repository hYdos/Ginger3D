package com.github.hydos.ginger.engine.common.font;

import com.github.hydos.ginger.engine.common.info.RenderAPI;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.opengl.api.GingerGL;
import com.github.hydos.ginger.engine.opengl.render.renderers.GLFontRenderer;
import com.github.hydos.ginger.engine.opengl.utils.GLLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextMaster {
    private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
    private static GLFontRenderer renderer;

    public static void cleanUp() {
        renderer.cleanUp();
    }

    public static void init() {
        renderer = new GLFontRenderer();
    }

    public static void loadText(GUIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = GLLoader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.get(font);
        if (textBatch == null) {
            textBatch = new ArrayList<GUIText>();
            texts.put(font, textBatch);
        }
        textBatch.add(text);
    }

    public static void removeText(GUIText text) {
        List<GUIText> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if (textBatch.isEmpty()) {
            texts.remove(text.getFont());
        }
    }

    public static void render() {
        renderer.render(texts);
    }

    public static void render(GUIText buildText) {
        // TODO: Add Vulkan font renderer
        if (Window.renderAPI == RenderAPI.OpenGL) {
            Map<FontType, List<GUIText>> oldTexts = texts;
            List<GUIText> oldFontText = texts.get(((GingerGL) GingerGL.getInstance()).globalFont);
            oldFontText.add(buildText);
            texts.clear();
            texts.put(((GingerGL) GingerGL.getInstance()).globalFont, oldFontText);
            texts = oldTexts;
        }
    }
}
