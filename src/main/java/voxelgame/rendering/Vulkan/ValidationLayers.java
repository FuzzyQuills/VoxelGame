package voxelgame.rendering.Vulkan;

import org.lwjgl.BufferUtils;
import org.lwjgl.vulkan.VkLayerProperties;

import java.nio.IntBuffer;

import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceLayerProperties;

public class ValidationLayers {
    public static String[] validationLayers = new String[]{
            "VK_LAYER_KHRONOS_validation"
    };

    public static boolean enableValidationLayers = false;

    public static boolean checkValidationLayerSupport(){
        IntBuffer layerCount = BufferUtils.createIntBuffer(1);
        vkEnumerateInstanceLayerProperties(layerCount, null);

        VkLayerProperties.Buffer availableLayers = new VkLayerProperties.Buffer(BufferUtils.createByteBuffer(layerCount.get(0) * VkLayerProperties.SIZEOF));
        vkEnumerateInstanceLayerProperties(layerCount, availableLayers);

        for(String layerName : validationLayers){
            boolean layerFound = false;

            for(VkLayerProperties layerProperties: (VkLayerProperties[]) availableLayers.stream().toArray()){
                if(layerName.equals(layerProperties.layerNameString())){
                    layerFound = true;
                    break;
                }
            }

            if(!layerFound)
                return false;
        }

        return true;
    }
}
