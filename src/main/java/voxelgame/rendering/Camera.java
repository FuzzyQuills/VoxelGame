package voxelgame.rendering;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import voxelgame.Time;
import voxelgame.VoxelGame;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    public float fov;
    public float znear;
    public float zfar;
    public float aspectRatio;
    private Vector3f position;

    public Vector3f getRotation() {
        VoxelGame.LOGGER.info("Camera Rotation: " + rotation.x + " " + rotation.y + " " + rotation.z);
        return rotation;
    }

    private Vector3f rotation;

    public static final float CAMERA_SPEED = 3.0f;

    public Camera(float fovdeg, float znear, float zfar, float aspectRatio){
        this.aspectRatio = aspectRatio;
        this.fov = (float) Math.toRadians(fovdeg);
        this.zfar = zfar;
        this.znear = znear;
        this.position = new Vector3f(0.0f, 0.0f, 0.0f);
        this.rotation = new Vector3f(10f, 0.0f, 0.0f);
    }
    public Camera(float fovdeg, float znear, float zfar, float aspectRatio, Vector3f position, Vector3f rotation){
        this.aspectRatio = aspectRatio;
        this.fov = (float) Math.toRadians(fovdeg);
        this.zfar = zfar;
        this.znear = znear;
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getForward(){
        Matrix4f mat = getViewMatrix().invert();//.invert();
        return new Vector3f(mat.m20(), mat.m21(), mat.m22()).normalize();
    }

    public Matrix4f getProjectionMatrix(){
        return new Matrix4f().perspective(fov, aspectRatio, znear, zfar);
    }

    public Matrix4f getViewMatrix(){
        Matrix4f viewMatrix = new Matrix4f().identity();

        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));

        viewMatrix.translate(-position.x, -position.y, -position.z);
        return viewMatrix;
    }

    public void setFov(float fovdeg){
        this.fov = (float) Math.toRadians(fovdeg);
    }

    public void setAspectRatio(float aspectRatio){
        this.aspectRatio = aspectRatio;
    }

    public void setZnear(float znear){
        this.znear = znear;
    }

    public void setZfar(float zfar){
        this.zfar = zfar;
    }

    public void movePosition(Vector3f offset){
        if(offset.z != 0){
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offset.z;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offset.z;
        }

        if(offset.x != 0){
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offset.x;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offset.x;
        }

        position.y += offset.y;
    }

    public void update(long window){
        if(glfwGetInputMode(window, GLFW_CURSOR) != GLFW_CURSOR_DISABLED)
            return;
        Vector3f offset = new Vector3f(0.0f, 0.0f, 0.0f);
        if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS){
            offset.z = -CAMERA_SPEED * Time.deltaTime;
        }
        if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS){
            offset.z = CAMERA_SPEED * Time.deltaTime;
        }
        if(glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS){
            offset.x = CAMERA_SPEED * Time.deltaTime;
        }
        if(glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS){
            offset.x = -CAMERA_SPEED * Time.deltaTime;
        }
        if(glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS){
            offset.y = CAMERA_SPEED * Time.deltaTime;
        }
        if(glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS){
            offset.y = -CAMERA_SPEED * Time.deltaTime;
        }

        rotation.y += VoxelGame.MOUSE_DELTA_X * VoxelGame.MOUSE_SENSITIVITY * Time.deltaTime;
        rotation.x += VoxelGame.MOUSE_DELTA_Y * VoxelGame.MOUSE_SENSITIVITY * Time.deltaTime;

        movePosition(offset);
    }

    public Vector3f getPosition() {
        return position;
    }
}
