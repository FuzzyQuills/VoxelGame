package voxelgame.engine.registry;

import voxelgame.engine.Identifier;
import voxelgame.rendering.Shader;

public class ShaderRegister extends Registry<Shader> {
    public void register(Identifier identifier){
        super.register(identifier, new Shader(identifier));
    }
}
