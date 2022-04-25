package voxelgame.engine.world;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL12.glTexImage3D;

public class World {
    HashMap<Vector3i, Region> regions;
    public int renderDistance = 2;
    public int voxelTexture;

    public World(){
        regions = new HashMap<>();
        for(int x = 0; x < renderDistance; x++)
            for(int y = 0; y < renderDistance; y++)
                for(int z = 0; z < renderDistance; z++){
                    regions.put(new Vector3i(x, y, z), new Region());
                }
        voxelTexture = glGenTextures();
        setVoxelTexture();
    }

    public void setVoxelTexture(){
        int dimension = renderDistance * Region.DIMENSIONS;

        float[] data = new float[dimension * dimension * dimension * 4];

        for(int x = 0; x < dimension; x++){
            for(int y = 0; y < dimension; y++) {
                for (int z = 0; z < dimension; z++) {
                    int dataIndex = (z * dimension * dimension + y * dimension + x) * 4;
                    Vector4f voxel = getVoxel(x, y, z);
                    data[dataIndex]     = voxel.x;
                    data[dataIndex + 1] = voxel.y;
                    data[dataIndex + 2] = voxel.z;
                    data[dataIndex + 3] = voxel.w;
                }
            }
        }
        // set data
        glBindTexture(GL_TEXTURE_3D, voxelTexture);
        glTexImage3D(GL_TEXTURE_3D, 0, GL_RGBA, dimension, dimension, dimension, 0, GL_RGBA, GL_FLOAT, data);
    }

    public Vector4f getVoxel(int x, int y, int z){
        Vector3i regionPos = new Vector3i(x / Region.DIMENSIONS, y / Region.DIMENSIONS, z / Region.DIMENSIONS);
        Region reg = regions.get(regionPos);
        if(reg != null)
            return regions.get(regionPos).getVoxel(x % Region.DIMENSIONS, y % Region.DIMENSIONS, z % Region.DIMENSIONS);
        return new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void BindVoxelTexture(){
        glBindTexture(GL_TEXTURE_3D, voxelTexture);
    }
}
