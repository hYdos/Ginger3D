package com.github.hydos.ginger.engine.opengl.postprocessing;

import com.github.hydos.ginger.engine.common.fbo.FBOCallbackHandler;
import com.github.hydos.ginger.engine.common.io.Window;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.system.Callback;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.ARBFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

public class FrameBufferObject {
    public boolean resetFramebuffer;
    /* cool ginger feature which handles fbos once they need to be rendered */
    public FBOCallbackHandler handler;
    /* Multisampled FBO objects */
    public int multisampledColorRenderBuffer;
    /* Single-sampled FBO objects */
    public int colorTexture;
    long window;
    int width = 1024;
    int height = 768;
    boolean destroyed;
    Object lock = new Object();
    int multisampledDepthRenderBuffer;
    int multisampledFbo;
    int samples = 1;
    int fbo;
    GLFWErrorCallback errorCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback fbCallback;
    Callback debugProc;

    public FrameBufferObject(FBOCallbackHandler handler) {
        this.handler = handler;
        this.window = Window.getWindow();
        width = Window.actualWidth;
        height = Window.actualHeight;
        createFBO();
    }

    public void bindFBO() {
        glBindFramebuffer(GL_FRAMEBUFFER, multisampledFbo);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, width, height);
    }

    public void createFBO() {
        this.window = Window.getWindow();
        this.width = Window.getWidth();
        this.height = Window.getHeight();
        /* Create multisampled FBO */
        multisampledColorRenderBuffer = glGenRenderbuffers();
        multisampledDepthRenderBuffer = glGenRenderbuffers();
        multisampledFbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, multisampledFbo);
        glBindRenderbuffer(GL_RENDERBUFFER, multisampledColorRenderBuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_RGBA8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, multisampledColorRenderBuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, multisampledDepthRenderBuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, multisampledDepthRenderBuffer);
        int fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw new AssertionError("Could not create FBO: " + fboStatus);
        }
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        /* Create single-sampled FBO */
        colorTexture = glGenTextures();
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glBindTexture(GL_TEXTURE_2D, colorTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // we also want to sample this texture later
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); // we also want to sample this texture later
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture, 0);
        fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw new AssertionError("Could not create FBO: " + fboStatus);
        }
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void resizeFBOs() {
        /* Delete multisampled FBO objects */
        glDeleteRenderbuffers(multisampledDepthRenderBuffer);
        glDeleteRenderbuffers(multisampledColorRenderBuffer);
        glDeleteFramebuffers(multisampledFbo);
        /* Delete single-sampled FBO objects */
        glDeleteTextures(colorTexture);
        glDeleteFramebuffers(fbo);
        /* Recreate everything */
        createFBO();
    }

    public void unbindFBO() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        /* Resolve by blitting to non-multisampled FBO */
        glBindFramebuffer(GL_READ_FRAMEBUFFER, multisampledFbo);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void update() {
        if (resetFramebuffer) {
            resizeFBOs();
            resetFramebuffer = false;
        }
    }
}
