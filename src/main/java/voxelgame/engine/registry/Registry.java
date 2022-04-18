package voxelgame.engine.registry;

import voxelgame.engine.Identifier;
import voxelgame.rendering.Shader;

import java.util.HashMap;

public class Registry<T>{
    private final HashMap<String, T> entries;

    public Registry(){
        entries = new HashMap<>();
    }

    public void register(Identifier identifier, T t){
        entries.put(identifier.toString(), t);
    }

    public T get(Identifier identifier){
        return entries.get(identifier.toString());
    }
}
