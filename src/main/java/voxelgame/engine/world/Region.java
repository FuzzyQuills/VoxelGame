package voxelgame.engine.world;

import org.joml.Vector4f;

import java.util.Random;

public class Region {
    public Vector4f[][][] voxels; // stored as color Values
    public static int DIMENSIONS = 32; // the region is 32x32x32

    public Region(){
        voxels = new Vector4f[DIMENSIONS][DIMENSIONS][DIMENSIONS];

        // generate random pattern
        Random r = new Random();
        for(int x = 0; x < DIMENSIONS; x++)
            for(int y = 0; y < DIMENSIONS; y++)
                for(int z = 0; z < DIMENSIONS; z++){
                    voxels[x][y][z] = new Vector4f(r.nextFloat(), r.nextFloat(), r.nextFloat(), Math.round(r.nextFloat()));
                }
    }

    public Vector4f[][][] getVoxels() {
        return voxels;
    }

    public Vector4f getVoxel(int x, int y, int z){
        return voxels[x][y][z];
    }
}
