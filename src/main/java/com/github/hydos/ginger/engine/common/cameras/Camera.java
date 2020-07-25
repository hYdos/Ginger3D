package com.github.hydos.ginger.engine.common.cameras;

import com.github.hydos.ginger.engine.common.elements.objects.GLRenderObject;
import org.joml.Vector3f;

public abstract class Camera {
    public GLRenderObject player;
    private float pitch, yaw, roll;
    private Vector3f position = new Vector3f(0, 0, 0);

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }

    public abstract void updateMovement();
}
