package org.voxelgame;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {
    public static float deltaTime;

    private static float lastTime;

    public static void updateTime(){
        float currentTime = (float) glfwGetTime();
        deltaTime = currentTime - lastTime;
        lastTime = currentTime;
    }
}
