package voxelgame.rendering;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import voxelgame.VoxelGame;
import voxelgame.rendering.Vulkan.ValidationLayers;
import voxelgame.rendering.hud.HUD;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryStack.create;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

public class Window {
    public long getWindow() {
        return window;
    }

    private final long window;
    GLFWVidMode vidMode;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int width;
    public int height;
    public HUD hud;

    private VkInstance instance;

    public Window(int width, int height){
        this.width = width;
        this.height = height;

        if(!glfwInit()){
            VoxelGame.LOGGER.severe("Cannot initialize GLFW!");
        }

        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        createInstance();

        window = glfwCreateWindow(width, height, "VoxelGame", NULL, NULL);
    }

    private void createInstance(){
        try(MemoryStack stack = stackPush()){
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack);

            appInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            appInfo.pApplicationName(stack.UTF8Safe("Voxel Game"));
            appInfo.applicationVersion(VK_MAKE_VERSION(1, 0, 0));
            appInfo.pEngineName(stack.UTF8Safe("No Engine"));
            appInfo.engineVersion(VK_MAKE_VERSION(1, 0,0 ));
            appInfo.apiVersion(VK_API_VERSION_1_0);

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack);
            createInfo.pApplicationInfo(appInfo);
            createInfo.ppEnabledExtensionNames(glfwGetRequiredInstanceExtensions());
            createInfo.ppEnabledLayerNames(null);

            PointerBuffer instancePtr = stack.mallocPointer(1);

            if(vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS){
                VoxelGame.LOGGER.severe("Failed to create vulkan instance!");
                return;
            }

            instance = new VkInstance(instancePtr.get(0), createInfo);
        }
    }

    public void cleanup(){
        glfwDestroyWindow(window);
        glfwTerminate();
        vkDestroyInstance(instance, null);
    }

    public Matrix4f getOrthographicView(){
        return new Matrix4f().ortho(0.0f, (float)height, (float)width, 0.0f, 0.5f, 100.0f);
    }

    public void restoreState(){
    }

    public void beginHUD(){
        hud.beginFrame(this.width, this.height);
    }

    public void endHUD(){
        hud.endFrame();
        restoreState();
    }

    /**
     * Draw a String on the screen at the specified position
     * IMPORTANT: before drawing anything call the function beginHUD and after drawing everything call endHUD
     * @param str the string to be displayed
     * @param position the position on the screen where the string should be displayed
     */
    public void drawString(String str, Vector2f position){
        drawString(str, (int) position.x, (int) position.y);
    }

    /**
     * Draw a String on the screen at the specified position
     * IMPORTANT: before drawing anything call the function beginHUD and after drawing everything call endHUD
     * @param str the string to be displayed
     * @param x the x coordinate on the screen where the string should be displayed
     * @param y the y coordinate on the screen where the string should be displayed
     */
    public void drawString(String str, int x, int y){
        this.hud.drawString(str, x, y);
    }
}
