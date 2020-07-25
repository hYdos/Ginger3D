package com.github.hydos.ginger.engine.common.cameras;

import com.github.hydos.ginger.engine.common.elements.objects.GLRenderObject;
import com.github.hydos.ginger.engine.common.io.Window;
import org.joml.Vector3f;

public class FirstPersonCamera extends Camera {
    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch, yaw;
    private float roll;

    public FirstPersonCamera(GLRenderObject playerEntity) {
        this.player = playerEntity;
        playerEntity.setVisible(false);
    }

    public float getPitch() {
        return pitch;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRoll() {
        return roll;
    }

    public float getYaw() {
        return yaw;
    }

    public void updateMovement() {
        position.x = player.getPosition().x;
        position.z = player.getPosition().z;
        position.y = player.getPosition().y;
        roll = player.getRotX();
        yaw = -player.getRotY() + 180 + Window.getNormalizedMouseCoordinates().x() * 70;
        pitch = player.getRotZ() + -Window.getNormalizedMouseCoordinates().y() * 70;
    }
}
