package com.github.hydos.ginger.engine.common.cameras;

import com.github.hydos.ginger.engine.common.elements.objects.GLRenderObject;
import com.github.hydos.ginger.engine.common.io.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

public class ThirdPersonCamera extends Camera {
    private float distanceFromPlayer = 5;
    private float angleAroundPlayer = 0;
    public ThirdPersonCamera(GLRenderObject playerEntity) {
        this.player = playerEntity;
    }

    private void calculatePitch() {
        if (Window.isMouseDown(1)) {
            float pitchChange = (float) (Window.dy * 0.2f);
            setPitch(getPitch() + pitchChange);
            if (getPitch() < 0) {
                setPitch(0);
            } else if (getPitch() > 90) {
                setPitch(90);
            }
        }
    }

    public void updateMovement() {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.setYaw(180 - (player.getRotY() + angleAroundPlayer));
    }

    private float calculateHorizontalDistance() {
        float hD = (float) (distanceFromPlayer * Math.cos(Math.toRadians(getPitch())));
        if (hD < 0)
            hD = 0;
        return hD;
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(getPitch() + 4)));
    }

    private void calculateZoom() {
        GLFW.glfwSetScrollCallback(Window.getWindow(), new GLFWScrollCallback() {
            @Override
            public void invoke(long win, double dx, double dy) {
                float zoomLevel = (float) dy * 0.1f;
                distanceFromPlayer -= zoomLevel;
            }
        });
    }

    private void calculateAngleAroundPlayer() {
        if (Window.isMouseDown(1)) {
            float angleChange = (float) (Window.dx * 0.3f);
            angleAroundPlayer -= angleChange;
        }
    }

    private void calculateCameraPosition(float horizDistance, float verticDistance) {
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        getPosition().x = player.getPosition().x - offsetX;
        getPosition().z = player.getPosition().z - offsetZ;
        getPosition().y = player.getPosition().y + verticDistance;
    }
}
