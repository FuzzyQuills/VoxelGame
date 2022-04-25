package voxelgame;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.Version;
import voxelgame.engine.AssetLoader;
import voxelgame.engine.Identifier;
import voxelgame.engine.registry.Registers;
import voxelgame.engine.world.Region;
import voxelgame.engine.world.World;
import voxelgame.rendering.*;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.memUTF8;


public class VoxelGame {
    public static float fpsTimer = 0;
    public static final String MODID = "voxelgame";

    public static Window window;

    public static Logger LOGGER = Logger.getLogger("Log");
    public  static Camera CAMERA;

    public static float MOUSE_X, MOUSE_Y, LAST_MOUSE_X, LAST_MOUSE_Y, MOUSE_DELTA_X, MOUSE_DELTA_Y;
    public static float MOUSE_SENSITIVITY = 100f;
    private boolean mouseDirty = true;

    public static int WIDTH = 1280, HEIGHT = 960;

    int polygonMode = GL_FILL;

    Mesh raytracingCanvas;
    World world;

    public void run(){
        LOGGER.info("Using LWJGL " + Version.getVersion());

        init();
        loop();

        glfwFreeCallbacks(window.getWindow());
        glfwDestroyWindow(window.getWindow());


        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void init(){
        AssetLoader.init();

        window = new Window(WIDTH, HEIGHT);

        glfwSetKeyCallback(window.getWindow(), (windowHnd, key, scancode, action, mods) -> {
            if (action != GLFW_RELEASE) {
                return;
            }

            switch(key){
                case GLFW_KEY_F10:
                    if(polygonMode == GL_LINE) {
                        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                        polygonMode = GL_FILL;
                    } else if(polygonMode == GL_FILL){
                        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                        polygonMode = GL_LINE;
                    }
                    break;
                case GLFW_KEY_ESCAPE:
                    if(glfwGetInputMode(window.getWindow(), GLFW_CURSOR) == GLFW_CURSOR_DISABLED){
                        glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                        LAST_MOUSE_X = MOUSE_X;
                        LAST_MOUSE_Y = MOUSE_Y;
                    }
                    else if(glfwGetInputMode(window.getWindow(), GLFW_CURSOR) == GLFW_CURSOR_NORMAL){
                        glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                        LAST_MOUSE_X = MOUSE_X;
                        LAST_MOUSE_Y = MOUSE_Y;
                    }
                    break;
            }
        });

        Registers.SHADERS.register(new Identifier(MODID, "raytracing"));

        glViewport(0, 0, WIDTH, HEIGHT);

        raytracingCanvas = new Mesh(
                new float[]{
                        1.0f, 1.0f, 0.0f,   -1.0f, 1.0f, 0.0f,
                        1.0f, -1.0f, 0.0f,  -1.0f, -1.0f, 0.0f},
                new int[]{0, 1, 2, 1, 3, 2},
                new Identifier(MODID, "raytracing")
        );

        raytracingCanvas.getShader().setUniform("RegionDimensions", Region.DIMENSIONS);

        glfwSetFramebufferSizeCallback(window.getWindow(), (window, width, height) ->{
            glViewport(0, 0, width, height);
            CAMERA.setAspectRatio((float)width / (float)height);
            VoxelGame.window.width = WIDTH = width;
            VoxelGame.window.height = HEIGHT = height;
            mouseDirty = true;
            glfwSetCursorPos(window, WIDTH / 2f, HEIGHT / 2f);
            raytracingCanvas.getShader().setUniform("WindowSize", new Vector2f(width, height));
        });

        glfwSetCursorPosCallback(window.getWindow(), (window, xpos, ypos) -> {
            MOUSE_X = (float) xpos;
            MOUSE_Y = (float) ypos;
        });



        CAMERA = new Camera(90.0f, 0.01f, 1000.0f,(float)WIDTH / (float)HEIGHT);

        world = new World();

        LOGGER.info("Finished Initialization!");
    }

    private void loop(){
        glClearColor(.1f, .3f, .1f, 1.0f);

        int currentFPS = (int)(1.0f / Time.deltaTime);

        while(!glfwWindowShouldClose(window.getWindow())){
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            raytracingCanvas.getShader().setUniform("CameraPosition", CAMERA.getPosition());
            raytracingCanvas.getShader().setUniform("CameraLookAt", CAMERA.getForward());
            raytracingCanvas.getShader().setUniform("FocalLength", 1.0f);
            raytracingCanvas.getShader().setUniform("WindowSize", new Vector2f(WIDTH, HEIGHT));
            raytracingCanvas.getShader().setUniform("CameraView", CAMERA.getViewMatrix());
            raytracingCanvas.getShader().setUniform("RenderDistance", world.renderDistance);
            world.BindVoxelTexture();
            raytracingCanvas.render();

            window.beginHUD();
            window.drawString("FPS: " + currentFPS, 50, 50);
            window.endHUD();

            glfwSwapBuffers(window.getWindow());

            glfwPollEvents();

            CAMERA.update(window.getWindow());

            if(mouseDirty){
                LAST_MOUSE_X = WIDTH / 2f;
                LAST_MOUSE_Y = HEIGHT / 2f;
                mouseDirty = false;
            }

            MOUSE_DELTA_X = MOUSE_X - LAST_MOUSE_X;
            MOUSE_DELTA_Y = MOUSE_Y - LAST_MOUSE_Y;

            LAST_MOUSE_X = MOUSE_X;
            LAST_MOUSE_Y = MOUSE_Y;

            Time.updateTime();
            if(fpsTimer >= 0.5f){
                currentFPS = (int)(1.0f / Time.deltaTime);
                fpsTimer = 0;
            }
            fpsTimer += Time.deltaTime;
        }
    }

    public static void main(String[] args){
        try{
            FileHandler fh = new FileHandler("log.log");
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOGGER.setUseParentHandlers(false);
        } catch (SecurityException | IOException e){
            VoxelGame.LOGGER.severe(e.getMessage());
        }
        new VoxelGame().run();
    }
}
