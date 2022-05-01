#include "GameEngine/Vulkan/Device.hpp"

#include <cstdlib>
#include <GLFW/glfw3.h>
#include <vector>
#include <stdexcept>
#include <set>
#include <string>
#include <cstring>

#include "GameEngine/Vulkan/Queue.hpp"
#include "GameEngine/Vulkan/Swapchain.hpp"

#ifdef NDEBUG
const std::vector<const char*> validationLayers = {
    "VK_LAYER_KHRONOS_validation"
};
#endif

const std::vector<const char*> deviceExtensions = {
    VK_KHR_SWAPCHAIN_EXTENSION_NAME
};

VkPhysicalDevice pickPhysicalDevice(VkInstance instance, VkSurfaceKHR surface){
    uint32_t deviceCount = 0;
    vkEnumeratePhysicalDevices(instance, &deviceCount, nullptr);
    if(deviceCount == 0){
        throw std::runtime_error("failed to find GPUs with Vulkan support!");
    }

    std::vector<VkPhysicalDevice> devices(deviceCount);
    vkEnumeratePhysicalDevices(instance, &deviceCount, devices.data());
    for(const auto& device : devices){
        if(isDeviceSuitable(device, surface)){
            return device;
        }
    }

    throw std::runtime_error("failed to find a suitable GPU!");
    return NULL;
}

VkDevice createLogicalDevice(VkPhysicalDevice physicalDevice, VkSurfaceKHR surface, VkQueue* graphicsQueue, VkQueue* presentQueue){

    VkDevice device;

    QueueFamilyIndices indices = findQueueFamilies(physicalDevice, surface);

    VkDeviceQueueCreateInfo queueCreateInfo{};
    queueCreateInfo.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
    queueCreateInfo.queueFamilyIndex = indices.m_graphicsFamily.value();
    queueCreateInfo.queueCount = 1;

    float queuePriority = 1.0f;
    queueCreateInfo.pQueuePriorities = &queuePriority;

    std::vector<VkDeviceQueueCreateInfo> queueCreateInfos;
    std::set<uint32_t> uniqueQueueFamilies = {indices.m_graphicsFamily.value(), indices.m_presentFamily.value()};
    for(uint32_t queueFamily : uniqueQueueFamilies){
        VkDeviceQueueCreateInfo queueCreateInfo{};
        queueCreateInfo.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
        queueCreateInfo.queueFamilyIndex = queueFamily;
        queueCreateInfo.queueCount = 1;
        queueCreateInfo.pQueuePriorities = &queuePriority;
        queueCreateInfos.push_back(queueCreateInfo);
    }

    VkDeviceCreateInfo createInfo{};
    createInfo.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
    createInfo.pQueueCreateInfos = &queueCreateInfo;
    createInfo.queueCreateInfoCount = 1;

    VkPhysicalDeviceFeatures deviceFeatures{};
    createInfo.pEnabledFeatures = &deviceFeatures;

    createInfo.enabledExtensionCount = 0;

#ifdef NDEBUG
    createInfo.enabledLayerCount = static_cast<uint32_t>(validationLayers.size());
    createInfo.ppEnabledLayerNames = validationLayers.data();
#else
    createInfo.enabledLayerCount = 0;
#endif

    createInfo.queueCreateInfoCount = static_cast<uint32_t>(queueCreateInfos.size());
    createInfo.pQueueCreateInfos = queueCreateInfos.data();

    createInfo.enabledExtensionCount = static_cast<uint32_t>(deviceExtensions.size());
    createInfo.ppEnabledExtensionNames = deviceExtensions.data();

    if(vkCreateDevice(physicalDevice, &createInfo, nullptr, &device) != VK_SUCCESS){
        throw std::runtime_error("failed to create logical device!");
    }

    vkGetDeviceQueue(device, indices.m_graphicsFamily.value(), 0, graphicsQueue);
    vkGetDeviceQueue(device, indices.m_presentFamily.value(), 0, presentQueue);

    return device;
}

bool isDeviceSuitable(VkPhysicalDevice device, VkSurfaceKHR surface){
    QueueFamilyIndices indices = findQueueFamilies(device, surface);

    bool extensionSupported = checkDeviceExtensionSupport(device);

    bool swapChainAdequate = false;
    if(extensionSupported){
        SwapchainSupportDetails swapChainSupport = querySwapchainSupport(device, surface);
        swapChainAdequate = !swapChainSupport.m_formats.empty() && !swapChainSupport.m_presentModes.empty();
    }

    return indices.isComplete() && extensionSupported && swapChainAdequate;
}

bool checkDeviceExtensionSupport(VkPhysicalDevice device){
    uint32_t extensionCount;
    vkEnumerateDeviceExtensionProperties(device, nullptr, &extensionCount, nullptr);

    std::vector<VkExtensionProperties> availableExtensions(extensionCount);
    vkEnumerateDeviceExtensionProperties(device, nullptr, &extensionCount, availableExtensions.data());

    std::set<std::string> requiredExtensions(deviceExtensions.begin(), deviceExtensions.end());

    for(const auto& extension : availableExtensions){
        requiredExtensions.erase(extension.extensionName);
    }

    return requiredExtensions.empty();
}

#ifdef NDEBUG
bool checkValidationLayerSupport(){
    uint32_t layerCount;
    vkEnumerateInstanceLayerProperties(&layerCount, nullptr);

    std::vector<VkLayerProperties> availableLayers(layerCount);
    vkEnumerateInstanceLayerProperties(&layerCount, availableLayers.data());

    for(const char* layerName : validationLayers){
        bool layerFound = false;
        for(const auto& layerProperties : availableLayers){
            if(strcmp(layerName, layerProperties.layerName) == 0){
                layerFound = true;
                break;
            }
        }
        if(!layerFound)
            return false;
    }
    return true;
}
#endif