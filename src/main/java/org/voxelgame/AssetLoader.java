package org.voxelgame;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class AssetLoader {
    public String LoadFragmentShaderFile(String modid, String fileName){
        String filePath = "assets/" + modid + "/shader/fragment/" + fileName + ".glsl";

        String result = ReadTextFromFile(filePath);
        if(result == null) {
            VoxelGame.LOGGER.severe("Error reading file " + filePath);
        }
        return result;
    }

    public String LoadVertexShaderFile(String modid, String fileName){
        String filePath = "assets/" + modid + "/shader/vertex/" + fileName + ".glsl";

        String result = ReadTextFromFile(filePath);
        if(result == null) {
            VoxelGame.LOGGER.severe("Error reading file " + filePath);
        }
        return result;
    }

    private String ReadTextFromFile(String filePath){
        try{
            FileReader fileReader = new FileReader("src/main/resources/" + filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String result = "";
            String line;
            while((line = bufferedReader.readLine()) != null)
                result += line + "\n";

            return result;

        } catch(Exception e){
            VoxelGame.LOGGER.severe(e.getMessage());
        }

        return null;
    }
}
