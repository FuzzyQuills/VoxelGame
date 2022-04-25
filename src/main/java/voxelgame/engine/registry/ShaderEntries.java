package voxelgame.engine.registry;

import voxelgame.VoxelGame;
import voxelgame.engine.Identifier;

public class ShaderEntries {
    static{
        Registers.SHADERS.register(new Identifier(VoxelGame.MODID, "raytracing"));
    }
}
