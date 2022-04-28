#include <GameEngine/Vulkan/Queue.hpp>

#include <GLFW/glfw3.h>
#include <optional>
#include <vector>

QueueFamilyIndices findQueueFamilies(VkPhysicalDevice device, VkSurfaceKHR surface){
    QueueFamilyIndices indices;

    uint32_t queueFamilyCount = 0;
    vkGetPhysicalDeviceQueueFamilyProperties(device, &queueFamilyCount, nullptr);

    std::vector<VkQueueFamilyProperties> queueFamilies(queueFamilyCount);
    vkGetPhysicalDeviceQueueFamilyProperties(device, &queueFamilyCount, queueFamilies.data());

    int i = 0;
    for (const auto& queueFamily : queueFamilies){
        if(queueFamily.queueFlags & VK_QUEUE_GRAPHICS_BIT){
            indices.m_graphicsFamily = i;
        }

        if(indices.isComplete()) break;

        i++;
    }

    VkBool32 presentSupport = false;
    vkGetPhysicalDeviceSurfaceSupportKHR(device, i, surface, &presentSupport);

    if(presentSupport) indices.m_presentFamily = i;

    return indices;
}

bool QueueFamilyIndices::isComplete(){
    return m_graphicsFamily.has_value() && m_presentFamily.has_value();
}