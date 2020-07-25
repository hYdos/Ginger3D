package com.github.hydos.ginger.engine.opengl.render.texture;

import com.github.hydos.ginger.engine.common.tools.IOUtil;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Image {
    private ByteBuffer image;
    private int width, height;
    private IntBuffer comp;
    private String location;
    Image(int width, int heigh, ByteBuffer image, IntBuffer comp, String location) {
        this.image = image;
        this.height = heigh;
        this.width = width;
        this.comp = comp;
        this.location = location;
    }

    public static Image createImage(String imagePath) {
        ByteBuffer img;
        ByteBuffer imageBuffer = null;
        try {
            imageBuffer = IOUtil.ioResourceToByteBuffer(imagePath, 8 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            }
            img = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
            if (img == null) {
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            }
            return new Image(w.get(0), h.get(0), img, comp, imagePath);
        }
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public IntBuffer getComp() {
        return comp;
    }

    public String getLocation() {
        return location;
    }
}