package voxelgame;

import java.io.*;

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
}
