package com.github.hydos.ginger.engine.common.tools;

import com.github.hydos.ginger.engine.common.cameras.Camera;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.common.math.Maths;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MousePicker {
    private static final int RECURSION_COUNT = 200;
    private static final float RAY_RANGE = 30;
    private Vector3f currentRay = new Vector3f();
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Camera camera;
    private Vector3f blockLocation;

    public MousePicker(Camera cam, Matrix4f projection) {
        camera = cam;
        projectionMatrix = projection;
        viewMatrix = Maths.createViewMatrix(camera);
    }

    private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            return null;
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }

    private Vector3f calculateMouseRay() {
        float mouseX = (float) Window.getMouseX();
        float mouseY = (float) (Window.getHeight() - Window.getMouseY());
        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);
        Vector3f worldRay = toWorldCoords(eyeCoords);
        return worldRay;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public Vector3f getCurrentTerrainPoint() {
        return blockLocation;
    }

    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / Window.getWidth() - 1f;
        float y = (2.0f * mouseY) / Window.getHeight() - 1f;
        return new Vector2f(x, y);
    }

    private Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f camPos = camera.getPosition();
        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return scaledRay.add(start);
    }

    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
        Vector3f startPoint = getPointOnRay(ray, start);
        Vector3f endPoint = getPointOnRay(ray, finish);
        if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isUnderGround(Vector3f testPoint) {
        return false;
    } //uuuh

    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Matrix4f invertedProjection = projectionMatrix.invert();
        Vector4f eyeCoords = invertedProjection.transform(clipCoords);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Matrix4f invertedView = viewMatrix.invert();
        Vector4f rayWorld = invertedView.transform(eyeCoords);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalize();
        return mouseRay;
    }

    public void update() {
        viewMatrix = Maths.createViewMatrix(camera);
        currentRay = calculateMouseRay();
        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
            blockLocation = binarySearch(0, 0, RAY_RANGE, currentRay);
        } else {
            blockLocation = new Vector3f();
        }
    }
}