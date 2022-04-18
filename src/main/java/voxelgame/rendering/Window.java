package voxelgame.rendering;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import voxelgame.VoxelGame;

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

    int width;
    int height;

    public Window(int width, int height){
        this.width = width;
        this.height = height;
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            VoxelGame.LOGGER.severe("Unable to initialize GLFW");
        }

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

        glfwSwapInterval(1);

        glfwShowWindow(window);

        GL.createCapabilities();

        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEPTH_TEST);

        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> VoxelGame.LOGGER.info("GL CALLBACK: source: " + source + " type: " + (type == GL_DEBUG_TYPE_ERROR ? "** GL ERROR **" : "") + " severity: " + severity + " message: " + memUTF8(message, length)), 0);
    }
}
