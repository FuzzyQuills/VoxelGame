#pragma once

#define GLFW_INCLUDE_VULKAN
#include <GLFW/glfw3.h>
#include <vector>

#include "GameEngine/Vulkan/Swapchain.hpp"

class Window{
public:
    GLFWwindow* m_window;
    uint32_t m_width = 1920, m_height = 1080;

    VkSwapchainKHR m_swapChain;
    std::vector<VkImage> m_swapChainImages;
    VkFormat m_swapChainImageFormat;
    VkExtent2D m_swapChainExtent;
    std::vector<VkImageView> m_swapChainImageViews;

    VkRenderPass m_renderPass;
    VkPipelineLayout m_pipelineLayout;
    VkPipeline m_graphicsPipeline;

    std::vector<VkFramebuffer> m_swapChainFramebuffers;

    VkSemaphore m_imageAvailableSemaphore;
    VkSemaphore m_renderFinishedSemaphore;
    VkFence m_inFlightFence;

public:
    void initWindow();
    void initGraphics(VkDevice device, VkPhysicalDevice physicalDevice, VkSurfaceKHR surface);
    void close(VkDevice device);

    void drawFrame(VkDevice device, VkCommandBuffer commandBuffer, VkQueue graphicsQueue, VkQueue presentQueue);
    void recordCommandBuffer(VkCommandBuffer commandBuffer, uint32_t imageIndex);

private:
    void createSwapChain(VkDevice device, VkPhysicalDevice physicalDevice, VkSurfaceKHR surface);
    void createImageViews(VkDevice device);
    void createRenderPass(VkDevice device);
    void createGraphicsPipeline(VkDevice device);
    void createFramebuffers(VkDevice device);
public:
    void createSyncObjects(VkDevice device);

public:
    SwapchainSupportDetails querySwapchainSupport(VkPhysicalDevice device, VkSurfaceKHR surface);

    VkSurfaceFormatKHR chooseSwapSurfaceFormat(const std::vector<VkSurfaceFormatKHR>& availableFormats);
    VkPresentModeKHR chooseSwapPresentMode(const std::vector<VkPresentModeKHR>& availablePresentModes);
    VkExtent2D chooseSwapExtent(const VkSurfaceCapabilitiesKHR& capabilities);

    VkShaderModule createShaderModule(const std::vector<char>& code, VkDevice device);
};