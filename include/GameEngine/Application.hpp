#pragma once
#define GLFW_INCLUDE_VULKAN
#include <GLFW/glfw3.h>
#include <vector>

#include "GameEngine/Vulkan/Swapchain.hpp"
#include "GameEngine/Renderer/Window.hpp"
#include "GameEngine/Vulkan/Device.hpp"

class Application{

public:
    VkInstance m_instance;
    VkPhysicalDevice m_physicalDevice = VK_NULL_HANDLE;
    VkDevice m_device;
    VkQueue m_graphicsQueue;
    VkSurfaceKHR m_surface;
    VkQueue m_presentQueue;

    VkCommandPool m_commandPool;
    VkCommandBuffer m_commandBuffer;

    Window m_window;



public:
    void run();

private:
    void initWindow();
    void initVulkan();
    void createSurface();
    void createCommandPool();
    void createCommandBuffer();

    void mainLoop();
    void cleanup();

};