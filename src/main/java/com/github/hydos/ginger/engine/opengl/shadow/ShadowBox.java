package com.github.hydos.ginger.engine.opengl.shadow;

import com.github.hydos.ginger.engine.common.cameras.Camera;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.opengl.render.GLRenderManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Represents the 3D cuboidal area of the world in which objects will cast
 * shadows (basically represents the orthographic projection area for the shadow
 * render pass). It is updated each frame to optimise the area, making it as
 * small as possible (to allow for optimal shadow map resolution) while not
 * being too small to avoid objects not having shadows when they should.
 * Everything inside the cuboidal area represented by this object will be
 * rendered to the shadow map in the shadow render pass. Everything outside the
 * area won't be.
 */
public class ShadowBox {
    private static final float OFFSET = 10;
    private static final Vector4f UP = new Vector4f(0, 1, 0, 0);
    private static final Vector4f FORWARD = new Vector4f(0, 0, -1, 0);
    private static final float SHADOW_DISTANCE = 100;
    private float minX, maxX;
    private float minY, maxY;
    private float minZ, maxZ;
    private Matrix4f lightViewMatrix;
    private Camera cam;
    private float farHeight, farWidth, nearHeight, nearWidth;

    /**
     * Creates a new shadow box and calculates some initial values relating to
     * the camera's view frustum, namely the width and height of the near plane
     * and (possibly adjusted) far plane.
     *
     * @param lightViewMatrix - basically the "view matrix" of the light. Can be used to
     *                        transform a point from world space into "light" space (i.e.
     *                        changes a point's coordinates from being in relation to the
     *                        world's axis to being in terms of the light's local axis).
     * @param camera          - the in-game camera.
     */
    protected ShadowBox(Matrix4f lightViewMatrix, Camera camera) {
        this.lightViewMatrix = lightViewMatrix;
        this.cam = camera;
        calculateWidthsAndHeights();
    }

    /**
     * @return The rotation of the camera represented as a matrix.
     */
    private Matrix4f calculateCameraRotationMatrix() {
        Matrix4f rotation = new Matrix4f();
        rotation.rotate((float) Math.toRadians(-cam.getYaw()), new Vector3f(0, 1, 0));
        rotation.rotate((float) Math.toRadians(-cam.getPitch()), new Vector3f(1, 0, 0));
        return rotation;
    }

    /**
     * Calculates the position of the vertex at each corner of the view frustum
     * in light space (8 vertices in total, so this returns 8 positions).
     *
     * @param rotation      - camera's rotation.
     * @param forwardVector - the direction that the camera is aiming, and thus the
     *                      direction of the frustum.
     * @param centerNear    - the center point of the frustum's near plane.
     * @param centerFar     - the center point of the frustum's (possibly adjusted) far
     *                      plane.
     * @return The positions of the vertices of the frustum in light space.
     */
    private Vector4f[] calculateFrustumVertices(Matrix4f rotation, Vector3f forwardVector,
                                                Vector3f centerNear, Vector3f centerFar) {
        Vector4f upVector4F = rotation.transform(UP);
        Vector3f upVector = new Vector3f(upVector4F.x, upVector4F.y, upVector4F.z);
        Vector3f rightVector = forwardVector.cross(upVector);
        Vector3f downVector = new Vector3f(-upVector.x, -upVector.y, -upVector.z);
        Vector3f leftVector = new Vector3f(-rightVector.x, -rightVector.y, -rightVector.z);
        Vector3f farTop = centerFar.add(new Vector3f(upVector.x * farHeight, upVector.y * farHeight, upVector.z * farHeight));
        Vector3f farBottom = centerFar.add(new Vector3f(downVector.x * farHeight, downVector.y * farHeight, downVector.z * farHeight));
        Vector3f nearTop = centerNear.add(new Vector3f(upVector.x * nearHeight, upVector.y * nearHeight, upVector.z * nearHeight));
        Vector3f nearBottom = centerNear.add(new Vector3f(downVector.x * nearHeight, downVector.y * nearHeight, downVector.z * nearHeight));
        Vector4f[] points = new Vector4f[8];
        points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth);
        points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth);
        points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth);
        points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth);
        points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth);
        points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth);
        points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth);
        points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth);
        return points;
    }

    /**
     * Calculates one of the corner vertices of the view frustum in world space
     * and converts it to light space.
     *
     * @param startPoint - the starting center point on the view frustum.
     * @param direction  - the direction of the corner from the start point.
     * @param width      - the distance of the corner from the start point.
     * @return - The relevant corner vertex of the view frustum in light space.
     */
    private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction,
                                                      float width) {
        Vector3f point = startPoint.add(new Vector3f(direction.x * width, direction.y * width, direction.z * width));
        Vector4f point4f = new Vector4f(point.x, point.y, point.z, 1f);
        lightViewMatrix.transform(point4f);
        return point4f;
    }

    /**
     * Calculates the width and height of the near and far planes of the
     * camera's view frustum. However, this doesn't have to use the "actual" far
     * plane of the view frustum. It can use a shortened view frustum if desired
     * by bringing the far-plane closer, which would increase shadow resolution
     * but means that distant objects wouldn't cast shadows.
     */
    private void calculateWidthsAndHeights() {
        farWidth = (float) (SHADOW_DISTANCE * Math.tan(Math.toRadians(GLRenderManager.FOV)));
        nearWidth = (float) (GLRenderManager.NEAR_PLANE
                * Math.tan(Math.toRadians(GLRenderManager.FOV)));
        farHeight = farWidth / getAspectRatio();
        nearHeight = nearWidth / getAspectRatio();
    }

    /**
     * @return The aspect ratio of the display (width:height ratio).
     */
    private float getAspectRatio() {
        return (float) Window.getWidth() / (float) Window.getHeight();
    }

    /**
     * Calculates the center of the "view cuboid" in light space first, and then
     * converts this to world space using the inverse light's view matrix.
     *
     * @return The center of the "view cuboid" in world space.
     */
    protected Vector3f getCenter() {
        float x = (minX + maxX) / 2f;
        float y = (minY + maxY) / 2f;
        float z = (minZ + maxZ) / 2f;
        Vector4f cen = new Vector4f(x, y, z, 1);
        Matrix4f invertedLight = new Matrix4f();
        lightViewMatrix.invert(invertedLight);
        Vector4f processedCenter = invertedLight.transform(cen);
        return new Vector3f(processedCenter.x, processedCenter.y, processedCenter.z);
    }

    /**
     * @return The height of the "view cuboid" (orthographic projection area).
     */
    protected float getHeight() {
        return maxY - minY;
    }

    /**
     * @return The length of the "view cuboid" (orthographic projection area).
     */
    protected float getLength() {
        return maxZ - minZ;
    }

    /**
     * @return The width of the "view cuboid" (orthographic projection area).
     */
    protected float getWidth() {
        return maxX - minX;
    }

    /**
     * Updates the bounds of the shadow box based on the light direction and the
     * camera's view frustum, to make sure that the box covers the smallest area
     * possible while still ensuring that everything inside the camera's view
     * (within a certain range) will cast shadows.
     */
    protected void update() {
        Matrix4f rotation = calculateCameraRotationMatrix();
        Vector4f forwardVector4F = rotation.transform(FORWARD);
        Vector3f forwardVector = new Vector3f(forwardVector4F.x, forwardVector4F.y, forwardVector4F.z);
        Vector3f toFar = new Vector3f(forwardVector);
        toFar.mul(SHADOW_DISTANCE);
        Vector3f toNear = new Vector3f(forwardVector);
        toNear.mul(GLRenderManager.NEAR_PLANE);
        Vector3f centerNear = toNear.add(cam.getPosition());
        Vector3f centerFar = toFar.add(cam.getPosition());
        Vector4f[] points = calculateFrustumVertices(rotation, forwardVector, centerNear,
                centerFar);
        boolean first = true;
        for (Vector4f point : points) {
            if (first) {
                minX = point.x;
                maxX = point.x;
                minY = point.y;
                maxY = point.y;
                minZ = point.z;
                maxZ = point.z;
                first = false;
                continue;
            }
            if (point.x > maxX) {
                maxX = point.x;
            } else if (point.x < minX) {
                minX = point.x;
            }
            if (point.y > maxY) {
                maxY = point.y;
            } else if (point.y < minY) {
                minY = point.y;
            }
            if (point.z > maxZ) {
                maxZ = point.z;
            } else if (point.z < minZ) {
                minZ = point.z;
            }
        }
        maxZ += OFFSET;
    }
}
