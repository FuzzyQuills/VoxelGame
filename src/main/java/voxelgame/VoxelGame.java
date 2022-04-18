package voxelgame;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.Version;
import voxelgame.rendering.*;
import voxelgame.rendering.lighting.Attenuation;
import voxelgame.rendering.lighting.PointLight;

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

    private Window window;

    public static AssetLoader ASSET_LOADER = new AssetLoader();
    public static Logger LOGGER = Logger.getLogger("Log");
    public  static Camera CAMERA;

    public static float MOUSE_X, MOUSE_Y, LAST_MOUSE_X, LAST_MOUSE_Y, MOUSE_DELTA_X, MOUSE_DELTA_Y;
    public static float MOUSE_SENSITIVITY = 5f;
    private boolean mouseDirty = true;

    public static int WIDTH = 1280, HEIGHT = 960;

    Mesh testMesh;

    int polygonMode = GL_FILL;


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
        /*
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        //glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 4);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "Voxel Game", NULL, NULL);
        if(window == NULL)
            VoxelGame.LOGGER.severe("Failed to create the GLFW window");

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert vidmode != null;
            glfwSetWindowPos(window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);

        glfwSwapInterval(1);

        glfwShowWindow(window);

        //glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        GL.createCapabilities();

        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEPTH_TEST);
        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> LOGGER.info("GL CALLBACK: source: " + source + " type: " + (type == GL_DEBUG_TYPE_ERROR ? "** GL ERROR **" : "") + " severity: " + severity + " message: " + memUTF8(message, length)), 0);
        */

        window = new Window(WIDTH, HEIGHT);

        glfwSetKeyCallback(window.getWindow(), (windowHnd, key, scancode, action, mods) -> {
            if (action != GLFW_RELEASE)
                return;

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

        LOGGER.info("creating shader");
        Shader shader = new Shader("voxelgame", "test");
        LOGGER.info("creating mesh");



        float[] vertices = {
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f
        };

        int[] indices =
                {
                        4, 2, 0,
                        2, 7, 3,
                        6, 5, 7,
                        1, 7, 5,
                        0, 3, 1,
                        4, 1 ,5,
                        4, 6, 2,
                        2, 6, 7,
                        6, 4, 5,
                        1, 3, 7,
                        0, 2, 3,
                        4, 0, 1
                };

        testMesh = new Mesh(vertices, indices, shader);
        testMesh.setPosition(new Vector3f(0.0f, 0.0f, -2.0f));



        Material mat = new Material();
        mat.setReflectance(0.0f);
        mat.setHasTexture(false);
        mat.setAmbient(new Vector4f(1.0f, 0.5f, 0.2f, 1.0f));
        mat.setDiffuse(new Vector4f(1.0f, 0.5f, 0.2f, 1.0f));
        mat.setSpecular(new Vector4f(1.0f, 0.5f, 0.2f, 1.0f));

        testMesh.setMaterialUniform("material", mat);
        testMesh.getShader().setUniform("ambientLight", new Vector3f(0.3f, 0.3f, 0.3f));
        testMesh.getShader().setUniform("specularPower", 1.0f);

        glViewport(0, 0, WIDTH, HEIGHT);

        glfwSetFramebufferSizeCallback(window.getWindow(), (window, width, height) ->{
            glViewport(0, 0, width, height);
            CAMERA.setAspectRatio((float)width / (float)height);
            WIDTH = width;
            HEIGHT = height;
            mouseDirty = true;
            glfwSetCursorPos(window, WIDTH / 2f, HEIGHT / 2f);
        });

        glfwSetCursorPosCallback(window.getWindow(), (window, xpos, ypos) -> {
            MOUSE_X = (float) xpos;
            MOUSE_Y = (float) ypos;
        });



        CAMERA = new Camera(90.0f, 0.01f, 1000.0f,(float)WIDTH / (float)HEIGHT);

        LOGGER.info("Finished Initialization!");
    }

    private void loop(){


        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        PointLight pointLight = new PointLight();
        pointLight.setPosition(new Vector3f(0.0f, 3.0f, -2.0f));
        pointLight.setAtt(new Attenuation(0.0f, 0.0f, 1.0f));
        pointLight.setColor(new Vector3f(1.0f, 1.0f, 1.0f));
        pointLight.setIntensity(1.0f);

        while(!glfwWindowShouldClose(window.getWindow())){
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            testMesh.getShader().setUniform("projection", CAMERA.getProjectionMatrix());
            testMesh.getShader().setUniform("view", CAMERA.getViewMatrix());
            testMesh.getShader().setUniform("camera_pos", CAMERA.getPosition());

            pointLight.setPosition(new Vector3f((float) Math.sin(glfwGetTime()) * 3f, (float) Math.cos(glfwGetTime()) * 3f, -2.0f));
            testMesh.setPointLightUniform("pointLight", pointLight);


            testMesh.render();

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