package voxelgame.rendering;


import org.joml.Matrix4f;
import org.joml.Vector3f;
import voxelgame.VoxelGame;
import voxelgame.rendering.lighting.Attenuation;
import voxelgame.rendering.lighting.DirectionalLight;
import voxelgame.rendering.lighting.PointLight;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {
    float[] vertexData;
    int[] indices;
    int VBO;
    int VAO;
    int EBO;

    Shader shader;

    private boolean isDirty;

    public Vector3f rotation;
    public Vector3f position;
    public Vector3f scale;

    private boolean normalsProvided = false;

    public Mesh(float[]vertices, int[] indices, Shader shader){
        this.shader = shader;

        // vertexData layout: [vec3 vertex, vec3 normal]
        this.vertexData = new float[vertices.length * 2];
        int i = 0;
        int j = 0;
        while(i < vertexData.length){
            // vertices
            vertexData[i++] = vertices[j++];
            vertexData[i++] = vertices[j++];
            vertexData[i++] = vertices[j++];

            //normals
            vertexData[i++] = 0.0f;
            vertexData[i++] = 0.0f;
            vertexData[i++] = 0.0f;
        }

        this.indices = indices;

        VBO = glGenBuffers();
        EBO = glGenBuffers();
        VAO = glGenVertexArrays();
        rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        position = new Vector3f(0.0f, 0.0f, 0.0f);
        scale = new Vector3f(1.0f, 1.0f, 1.0f);
        this.setDirty();
        normalsProvided = false;
    }

    public Mesh(float[]vertices, int[] indices, float[] normals, Shader shader){
        this.shader = shader;

        // vertexData layout: [vec3 vertex, vec3 normal]
        this.vertexData = new float[vertices.length + normals.length];
        int i = 0;
        int j = 0;
        while(i < vertexData.length){
            // vertices
            vertexData[i++] = vertices[j];
            vertexData[i++] = vertices[j+1];
            vertexData[i++] = vertices[j+2];

            //normals
            vertexData[i++] = normals[j];
            vertexData[i++] = normals[j+1];
            vertexData[i++] = normals[j+2];

            j += 3;
        }

        this.indices = indices;

        VBO = glGenBuffers();
        EBO = glGenBuffers();
        VAO = glGenVertexArrays();
        rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        position = new Vector3f(0.0f, 0.0f, 0.0f);
        scale = new Vector3f(1.0f, 1.0f, 1.0f);
        this.setDirty();
        normalsProvided = true;
    }

    public Mesh(Shader shader){
        this.shader = shader;
        setDirty();
    }

    private void cleanDirty(){
        if(!normalsProvided) calculateVertexNormals();

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);


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

    private void calculateVertexNormals(){
        Vector3f[] vertices = new Vector3f[vertexData.length / 6];
        Vector3f[] normals = new Vector3f[vertices.length];

        for(int i = 0; i < vertexData.length / 6; i++){
            vertices[i] = new Vector3f(
                    vertexData[i * 6],
                    vertexData[i * 6 + 1],
                    vertexData[i * 6 + 2]);
            normals[i] = new Vector3f(1.0f);
        }

        for (Vector3f a : vertices) {
            Vector3f[] adjacentVertices = new Vector3f[3];
            for (int j = 0; j < indices.length; j++) {
                //VoxelGame.LOGGER.info("V: " + vertices[indices[j]].x + " " + vertices[indices[j]].y + " " + vertices[indices[j]].z +
                //      " | " + a.x + " " + a.y + " " + a.z);
                if (vertices[indices[j]].x == a.x && vertices[indices[j]].y == a.y && vertices[indices[j]].z == a.z) {
                    int currentTrig = j - (j % 3);
                    /*
                    for(int k = 0; k < 3; k++){
                        //if(vertices[indices[currentTrig+k]].equals(a)) continue;
                        adjacentVertices[k] = (vertices[indices[currentTrig+k]]);
                    }*/

                    int k = 0;
                    int l = 0;
                    while (true) {
                        if (vertices[indices[currentTrig + k]].equals(a)) {
                            adjacentVertices[l++] = vertices[indices[currentTrig + (k % 3)]];
                            adjacentVertices[l++] = vertices[indices[currentTrig + ((k + 1) % 3)]];
                            adjacentVertices[l] = vertices[indices[currentTrig + ((k + 2) % 3)]];
                            break;
                        }
                        if (k > 64) { // the current vertex is not found so something went wrong
                            VoxelGame.LOGGER.severe(new RuntimeException("current vertex was not found!").getMessage());
                        }
                        k++;
                    }

                    Vector3f normal = new Vector3f(new Vector3f(adjacentVertices[2]).sub(adjacentVertices[0]))
                            .cross(new Vector3f(adjacentVertices[1]).sub(adjacentVertices[0]));

                    normals[indices[currentTrig]].add(normal);
                    normals[indices[currentTrig + 1]].add(normal);
                    normals[indices[currentTrig + 2]].add(normal);
                }
            }
            /*
            Vector3f sum = new Vector3f(0.0f);
            for(int j = 1; j < adjacentVertices.size(); j++){
                Vector3f b = adjacentVertices.get(j - 1);
                Vector3f c = adjacentVertices.get(j);
                sum.add(new Vector3f(b).sub(a)).cross(new Vector3f(c).sub(a));
            }
            VoxelGame.LOGGER.info("Adjacent Vertex Amount: " + adjacentVertices.size());

            if(sum.x == 0 && sum.y == 0 && sum.z == 0){
                normals[i] = sum.normalize();
            } else {
                normals[i] = sum.normalize();
            }

            VoxelGame.LOGGER.info("Normal: x:" + normals[i].x + " y:" + normals[i].y + " z:" + normals[i].z);
            */
        }

        for(int i = 0; i < normals.length; i++){
            normals[i].normalize();
            vertexData[i * 6 + 3] = normals[i].x;
            vertexData[i * 6 + 4] = normals[i].y;
            vertexData[i * 6 + 5] = normals[i].z;
        }
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

    public void setDirectionalLightUniform(String name, DirectionalLight light){

    }

    public void setPointLightUniform(String name, PointLight light){
        shader.setUniform(name + ".color", light.getColor());
        shader.setUniform(name + ".position", light.getPosition());
        shader.setUniform(name + ".intensity", light.getIntensity());
        Attenuation att = light.getAtt();
        shader.setUniform(name + ".att.constant", att.getConstant());
        shader.setUniform(name + ".att.linear", att.getLinear());
        shader.setUniform(name + ".att.exponent", att.getExponent());
    }

    public void setMaterialUniform(String name, Material mat){
        shader.setUniform(name + ".ambient", mat.getAmbient());
        shader.setUniform(name + ".diffuse", mat.getDiffuse());
        shader.setUniform(name + ".specular", mat.getSpecular());
        shader.setUniform(name + ".hasTexture", mat.isHasTexture() ? 1 : 0);
        shader.setUniform(name + ".reflectance", mat.getReflectance());
    }
}
