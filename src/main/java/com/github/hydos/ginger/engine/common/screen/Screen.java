package com.github.hydos.ginger.engine.common.screen;

import com.github.hydos.ginger.engine.common.elements.GLGuiTexture;

import java.util.List;

public abstract class Screen {
    public List<GLGuiTexture> elements;

    public abstract void render();  // FIXME: This never gets called!!!

    public abstract void tick();

    public abstract void cleanup();
}
