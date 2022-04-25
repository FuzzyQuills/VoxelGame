package voxelgame.rendering;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import voxelgame.VoxelGame;
import voxelgame.engine.AssetLoader;
import voxelgame.engine.Identifier;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    int program;

    public Shader(String modid, String shaderName){

        GL.getCapabilities();

        String vertexSource = AssetLoader.LoadVertexShaderFile(modid, shaderName);
        String fragmentSource = AssetLoader.LoadFragmentShaderFile(modid, shaderName);


        int vertexShader, fragmentShader;

        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertexShader, vertexSource);
        glShaderSource(fragmentShader, fragmentSource);

        glCompileShader(vertexShader);
        glCompileShader(fragmentShader);

        IntBuffer success = BufferUtils.createIntBuffer(1);
        glGetShaderiv(vertexShader, GL_COMPILE_STATUS, success);

        if(success.get(0) == 0){
            VoxelGame.LOGGER.severe(glGetShaderInfoLog(vertexShader));
        }
        glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, success);
        if(success.get(0) == 0){
            VoxelGame.LOGGER.severe(glGetShaderInfoLog(fragmentShader));
        }

        program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        glGetProgramiv(program, GL_LINK_STATUS, success);
        if(success.get(0) == 0){
            VoxelGame.LOGGER.severe(glGetProgramInfoLog(program));
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public Shader(Identifier identifier){
        this(identifier.getModid(), identifier.getName());
    }

    public void use(){
        glUseProgram(program);
    }

    public void setUniform(String name, int value){
        use();
        int loc = glGetUniformLocation(program, name);
        glUniform1i(loc, value);
    }

    public void setUniform(String name, float value){
        use();
        int loc = glGetUniformLocation(program, name);
        glUniform1f(loc, value);
    }

    public void setUniform(String name, Matrix4f value){
        use();
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            int loc = glGetUniformLocation(program, name);
            glUniformMatrix4fv(loc, false, fb);
        }
    }

    public void setUniform(String name, Matrix3f value){
        use();
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer fb = stack.mallocFloat(9);
            value.get(fb);
            int loc = glGetUniformLocation(program, name);
            glUniformMatrix4fv(loc, false, fb);
        }
    }

    public void setUniform(String name, Vector2f value){
        use();
        int loc = glGetUniformLocation(program, name);
        glUniform2f(loc, value.x, value.y);
    }

    public void setUniform(String name, Vector3f value){
        use();
        int loc = glGetUniformLocation(program, name);
        glUniform3f(loc, value.x, value.y, value.z);
    }

    public void setUniform(String name, Vector4f value){
        use();
        int loc = glGetUniformLocation(program, name);
        glUniform4f(loc, value.x, value.y, value.z, value.w);
    }
}
