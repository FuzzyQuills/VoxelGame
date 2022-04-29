#pragma once

#define GLFW_INCLUDE_VULKAN
#include <GLFW/glfw3.h>
#include <vector>

#include "GameEngine/Vulkan/SwapChain.hpp"
#include "GameEngine/Application.hpp"

class Window{
public:
    GLFWwindow* window;
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

private:
    Application* m_application;

public:
    void initWindow(Application* application);
    void initGraphics();
    void close();

private:
    void createSwapChain();
    void createImageViews();
    void createRenderPass();
    void createGraphicsPipeline();
    void createFramebuffers();

public:
    SwapChainSupportDetails querySwapChainSupport(VkPhysicalDevice device);

    VkSurfaceFormatKHR chooseSwapSurfaceFormat(const std::vector<VkSurfaceFormatKHR>& availableFormats);
    VkPresentModeKHR chooseSwapPresentMode(const std::vector<VkPresentModeKHR>& availablePresentModes);
    VkExtent2D chooseSwapExtent(const VkSurfaceCapabilitiesKHR& capabilities);

    VkShaderModule createShaderModule(const std::vector<char>& code);
};