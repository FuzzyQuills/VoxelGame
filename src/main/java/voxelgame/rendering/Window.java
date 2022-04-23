package voxelgame.rendering;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import voxelgame.VoxelGame;
import voxelgame.rendering.hud.HUD;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

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

    public Window(int width, int height){
        this.width = width;
        this.height = height;
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            VoxelGame.LOGGER.severe("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 4);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(width, height, "Voxel Game", NULL, NULL);
        if(window == NULL){
            VoxelGame.LOGGER.severe(new RuntimeException("Failed to create the GLFW window").getMessage());
        }

        try(MemoryStack stack = stackPush()){
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert vidMode != null;
            glfwSetWindowPos(window,
                    (vidMode.width() - pWidth.get()) / 2,
                    (vidMode.height() - pHeight.get()) / 2);
        }

        glfwMakeContextCurrent(window);

        glfwSwapInterval(0);

        glfwShowWindow(window);

        GL.createCapabilities();

        //glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> VoxelGame.LOGGER.info("GL CALLBACK: source: " + source + " type: " + (type == GL_DEBUG_TYPE_ERROR ? "** GL ERROR **" : "") + " severity: " + severity + " message: " + memUTF8(message, length)), 0);

        this.hud = new HUD();
        hud.init(this);
    }

    public Matrix4f getOrthographicView(){
        return new Matrix4f().ortho(0.0f, (float)height, (float)width, 0.0f, 0.5f, 100.0f);
    }

    public void restoreState(){
        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_STENCIL_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
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
