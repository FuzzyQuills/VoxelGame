package voxelgame.rendering.hud;

import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryUtil;
import voxelgame.VoxelGame;
import voxelgame.engine.AssetLoader;
import voxelgame.rendering.Window;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_STENCIL_STROKES;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.system.MemoryUtil.NULL;

public class HUD {
    long vg;
    NVGColor color;
    DoubleBuffer posx, posy;
    ByteBuffer fontBuffer;
    int counter;
    public static final String FONT_NAME = "fff-forward";

    public void init(Window window){
        vg = nvgCreate(NVG_STENCIL_STROKES);
        if(vg == NULL){
            VoxelGame.LOGGER.severe("Could not initialize nanovg");
            return;
        }

        fontBuffer = AssetLoader.LoadFileToByteBuffer("/assets/voxelgame/fonts/fff-forward.regular.ttf");
        int font = nvgCreateFontMem(vg, FONT_NAME, fontBuffer, 0);
        if(font == -1){
            VoxelGame.LOGGER.severe("Could not add font!");
            return;
        }

        posx = MemoryUtil.memAllocDouble(1);
        posy = MemoryUtil.memAllocDouble(1);

        counter = 0;

        color = NVGColor.create();
    }

    public void beginFrame(int windowWidth, int windowHeight){
        nvgBeginFrame(vg, windowWidth, windowHeight, 1);
    }

    public void endFrame(){
        nvgEndFrame(vg);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void drawString(String str, int x, int y){
        nvgText(this.vg, x, y, str);
        nvgFillColor(vg, nvgRGBA((byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, color));
        //nvgFill(this.vg);
    }
}
