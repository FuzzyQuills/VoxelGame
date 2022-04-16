package org.voxelgame.rendering;


import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {
    float[] vertices;
    int[] indices;
    int VBO;
    int VAO;
    int EBO;

    Shader shader;

    private boolean isDirty;

    public Vector3f rotation;
    public Vector3f position;
    public Vector3f scale;

    public Mesh(float[] vertices, int[] indices, Shader shader){
        this.shader = shader;
        this.vertices = vertices;
        this.indices = indices;
        VBO = glGenBuffers();
        EBO = glGenBuffers();
        VAO = glGenVertexArrays();
        rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        position = new Vector3f(0.0f, 0.0f, 0.0f);
        scale = new Vector3f(1.0f, 1.0f, 1.0f);
        this.setDirty();
    }

    public Mesh(Shader shader){
        this.shader = shader;
        setDirty();
    }

    private void cleanDirty(){
        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        isDirty = false;
    }

    public void render(){
        if(isDirty) cleanDirty();

        shader.use();

        shader.setUniform("model", getModelMatrix());

        glBindVertexArray(VAO);

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public Matrix4f getModelMatrix(){
        Matrix4f modelMat = new Matrix4f().identity();
        modelMat.translate(position);
        modelMat.scale(scale);
        modelMat.rotateX((float)Math.toRadians(-rotation.x))
                .rotateY((float)Math.toRadians(-rotation.y))
                .rotateZ((float)Math.toRadians(-rotation.z));
        return modelMat;
    }

    public void setDirty(){isDirty = true;}

    public Shader getShader(){
        return shader;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}
