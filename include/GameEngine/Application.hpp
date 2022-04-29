#pragma once
#define GLFW_INCLUDE_VULKAN
#include <GLFW/glfw3.h>
#include <vector>

#include "GameEngine/Vulkan/SwapChain.hpp"
#include "GameEngine/Renderer/Window.hpp"

class Application{

#ifdef NDEBUG
    const bool m_enableValidationLayers = false;
#else
    const bool m_enableValidationLayers = true;
#endif

public:
    VkInstance m_instance;
    VkPhysicalDevice m_physicalDevice = VK_NULL_HANDLE;
    VkDevice m_device;
    VkQueue m_graphicsQueue;
    VkSurfaceKHR m_surface;
    VkQueue m_presentQueue;

    Window m_window;

public:
    

public:
    void run();

private:
    void initWindow();
    void initVulkan();
    void createSurface();
    void pickPhysicalDevice();
    void createLogicalDevice();
    

    void mainLoop();
    void cleanup();

    bool checkValidationLayerSupport();
    bool checkDeviceExtensionSupport(VkPhysicalDevice device);
    bool isDeviceSuitable(VkPhysicalDevice device);
};