package voxelgame.engine;

import org.lwjgl.BufferUtils;
import voxelgame.Utils;
import voxelgame.VoxelGame;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AssetLoader {
    private static AssetLoader assetLoader;

    public static void init(){
        assetLoader = new AssetLoader();
    }
    private String LocalLoadFragmentShaderFile(String modid, String fileName){
        String filePath = "assets/" + modid + "/shader/fragment/" + fileName + ".glsl";

        String result = ReadTextFromFile(filePath);
        if(result == null) {
            VoxelGame.LOGGER.severe("Error reading file " + filePath);
        }
        return result;
    }

    private String LocalLoadVertexShaderFile(String modid, String fileName){
        String filePath = "assets/" + modid + "/shader/vertex/" + fileName + ".glsl";

        String result = ReadTextFromFile(filePath);
        if(result == null) {
            VoxelGame.LOGGER.severe("Error reading file " + filePath);
        }
        return result;
    }

    private String LocalReadTextFromFile(String filePath){
        try{
            FileReader fileReader = new FileReader("src/main/resources/" + filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder result = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null)
                result.append(line).append("\n");

            return result.toString();

        } catch(Exception e){
            VoxelGame.LOGGER.severe(e.getMessage());
        }

        return null;
    }

    public ByteBuffer LocalLoadFileToByteBuffer(String filePath) throws IOException{
        ByteBuffer buffer;

        Path path = Paths.get(filePath);
        if(Files.isReadable(path)){
            try(SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                while (true) {
                    if (fc.read(buffer) == -1) break;
                }
            }
        } else {
            try(
                InputStream source = AssetLoader.class.getResourceAsStream(filePath)) {
                assert source != null;
                try(ReadableByteChannel rbc = Channels.newChannel(source)){

                    buffer = BufferUtils.createByteBuffer(1024);

                    while(true) {
                        int bytes = rbc.read(buffer);
                        if (bytes == -1)
                            break;
                        if (buffer.remaining() == 0) {
                            buffer = Utils.resizeBuffer(buffer, buffer.capacity() * 2);
                        }
                    }
                }
            }
        }

        buffer.flip();
        return buffer;
    }

    public static String LoadFragmentShaderFile(String modid, String fileName) {
        return assetLoader.LocalLoadFragmentShaderFile(modid, fileName);
    }

    public static String LoadVertexShaderFile(String modid, String fileName){
        return assetLoader.LocalLoadVertexShaderFile(modid, fileName);
    }

    public static String ReadTextFromFile(String filePath){
        return assetLoader.LocalReadTextFromFile(filePath);
    }

    public static ByteBuffer LoadFileToByteBuffer(String filePath) {
        try {
            return assetLoader.LocalLoadFileToByteBuffer(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
