package voxelgame.rendering.texture;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import voxelgame.VoxelGame;
import voxelgame.engine.AssetLoader;
import voxelgame.engine.Identifier;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * Textures use 4 channels: R G B A.
 * They are directly sent to the GPU after their creation.
 */
public class Texture {
    private IntBuffer width;
    private IntBuffer height;
    private IntBuffer channelAmount;
    ByteBuffer data;
    int texture;

    public Texture(Identifier identifier, TextureTypes type){
        data = STBImage.stbi_load_from_memory(AssetLoader.LoadFileToByteBuffer(getTypePath(type)), width, height, channelAmount, 4);
        if(data == null){
            VoxelGame.LOGGER.severe("Failed to load texture: " + identifier.toString());
            return;
        }

        texture = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[]{0.0f, 0.0f, 0.0f, 1.0f});

        STBImage.stbi_image_free(data);
    }

    public Texture(Identifier identifier, TextureTypes type, ByteBuffer data, int width, int height){
        texture = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        this.width = BufferUtils.createIntBuffer(1);
        this.height = BufferUtils.createIntBuffer(1);
        this.width.put(0, width);
        this.height.put(0, height);
        glGenerateMipmap(GL_TEXTURE_2D);

        STBImage.stbi_image_free(data);
    }

    // TODO: Actually add correct paths
    public static String getTypePath(TextureTypes type){
        switch(type){
            case GUI_TEXTURE -> {
                return "GUI";
            }
            case ITEM_TEXTURE -> {
                return "ITEM";
            }
            case BLOCK_TEXTURE -> {
                return "BLOCK";
            }
        }

        return "";
    }
}
