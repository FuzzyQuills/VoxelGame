package voxelgame.engine;

public class Identifier {
    String modid;
    String name;

    public Identifier(String modid, String name) {
        this.modid = modid;
        this.name = name;
    }


    public String getModid() {
        return modid;
    }

    public void setModid(String modid) {
        this.modid = modid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return modid + ":" + name;
    }
}
