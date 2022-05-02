#include "GameEngine/Application.hpp"
#include "GameEngine/Vulkan/Queue.hpp"
#include "GameEngine/Vulkan/Swapchain.hpp"
#include "GameEngine/Assets/Loaders.hpp"

#include "GameEngine/Vulkan/Device.hpp"

#include <GLFW/glfw3.h>
#include <iostream>
#include <stdexcept>
#include <cstdlib>
#include <vector>
#include <cstring>
#include <set>
#include <string>
#include <limits>
#include <algorithm>

#ifdef NDEBUG
VkResult CreateDebugUtilsMessengerEXT(VkInstance instance, const VkDebugUtilsMessengerCreateInfoEXT* pCreateInfo, const VkAllocationCallbacks* pAllocator, VkDebugUtilsMessengerEXT* pDebugMessenger) {
    auto func = (PFN_vkCreateDebugUtilsMessengerEXT) vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT");
    if (func != nullptr) {
        return func(instance, pCreateInfo, pAllocator, pDebugMessenger);
    } else {
        return VK_ERROR_EXTENSION_NOT_PRESENT;
    }
}

void DestroyDebugUtilsMessengerEXT(VkInstance instance, VkDebugUtilsMessengerEXT debugMessenger, const VkAllocationCallbacks* pAllocator) {
    auto func = (PFN_vkDestroyDebugUtilsMessengerEXT) vkGetInstanceProcAddr(instance, "vkDestroyDebugUtilsMessengerEXT");
    if (func != nullptr) {
        func(instance, debugMessenger, pAllocator);
    }
}
#endif

void Application::run(){
    initVulkan();
    mainLoop();
    cleanup();
}

void Application::initVulkan(){

#ifdef NDEBUG
    if(!checkValidationLayerSupport()){
        throw std::runtime_error("validation layers requested, but not available!");
    }
#endif
    m_window.initWindow();

    VkApplicationInfo appInfo{};
    appInfo.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
    appInfo.pApplicationName = "Voxel Game";
    appInfo.applicationVersion = VK_MAKE_VERSION(1, 0, 0);
    appInfo.pEngineName = "No Engine";
    appInfo.engineVersion = VK_MAKE_VERSION(1, 0, 0);
    appInfo.apiVersion = VK_API_VERSION_1_0;

    VkInstanceCreateInfo createInfo{};
    createInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
    createInfo.pApplicationInfo = &appInfo;

    auto extensions = getRequiredExtensions();
    createInfo.enabledExtensionCount = static_cast<uint32_t>(extensions.size());
    createInfo.ppEnabledExtensionNames = extensions.data();

    VkResult result = vkCreateInstance(&createInfo, nullptr, &m_instance);
    if(result != VK_SUCCESS){
        throw std::runtime_error("Failed to create vulkan instance!");
    }

    uint32_t extensionCount = 0;
    vkEnumerateInstanceExtensionProperties(nullptr, &extensionCount, nullptr);
    
    std::vector<VkExtensionProperties> extensionsProps(extensionCount);
    vkEnumerateInstanceExtensionProperties(nullptr, &extensionCount, extensionsProps.data());

    std::cout << "available extensions:\n";
    for(const auto& extension : extensionsProps){
        std::cout << '\t' << extension.extensionName << '\n';
    }

    setupDebugMessenger();
    createSurface();
    this->m_physicalDevice = pickPhysicalDevice(this->m_instance, this->m_surface);
    this->m_device = createLogicalDevice(this->m_physicalDevice, this->m_surface, &this->m_graphicsQueue, &this->m_presentQueue);
    m_window.initGraphics(m_device, m_physicalDevice, m_surface);
    createCommandPool();
    createCommandBuffer();
    m_window.createSyncObjects(m_device);
}

void Application::setupDebugMessenger(){
#ifdef NDEBUG
    VkDebugUtilsMessengerCreateInfoEXT createInfo{};
    createInfo.sType = VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT;
    createInfo.messageSeverity = VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT;
    createInfo.messageType = VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;
    createInfo.pfnUserCallback = debugCallback;
    createInfo.pUserData = nullptr;

    if(CreateDebugUtilsMessengerEXT(m_instance, &createInfo, nullptr, &m_debugMessenger) != VK_SUCCESS){
        throw std::runtime_error("failed to set up debug messenger!");
    }

#else
    return;
#endif
}

void Application::mainLoop(){
    while(!glfwWindowShouldClose(m_window.m_window)){
        glfwPollEvents();
        m_window.drawFrame(m_device, m_commandBuffer, &m_graphicsQueue, m_presentQueue);
    }

    vkDeviceWaitIdle(m_device);
}

void Application::cleanup(){
    vkDestroyCommandPool(m_device, m_commandPool, nullptr);

    vkDestroyPipeline(m_device, m_window.m_graphicsPipeline, nullptr);
    vkDestroyPipelineLayout(m_device, m_window.m_pipelineLayout, nullptr);

    for(auto framebuffer : m_window.m_swapChainFramebuffers){
        vkDestroyFramebuffer(m_device, framebuffer, nullptr);
    }

    vkDestroyRenderPass(m_device, m_window.m_renderPass, nullptr);

    for(auto imageView : m_window.m_swapChainImageViews){
        vkDestroyImageView(m_device, imageView, nullptr);
    }

    m_window.close(m_device);
    
    vkDestroySwapchainKHR(m_device, m_window.m_swapChain, nullptr);
    vkDestroyDevice(m_device, nullptr);
    vkDestroySurfaceKHR(m_instance, m_surface, nullptr);

#ifdef NDEBUG
    DestroyDebugUtilsMessengerEXT(m_instance, m_debugMessenger, nullptr);
#endif

    vkDestroyInstance(m_instance, nullptr);
}

void Application::createSurface(){
    if(glfwCreateWindowSurface(m_instance, m_window.m_window, nullptr, &m_surface) != VK_SUCCESS){
        throw std::runtime_error("failed to create window surface!");
    }
}

void Application::createCommandPool(){
    QueueFamilyIndices queueFamilyIndices = findQueueFamilies(m_physicalDevice, m_surface);
    
    VkCommandPoolCreateInfo poolInfo{};
    poolInfo.sType = VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO;
    poolInfo.flags = VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT;
    poolInfo.queueFamilyIndex = queueFamilyIndices.m_graphicsFamily.value();

    if(vkCreateCommandPool(m_device, &poolInfo, nullptr, &m_commandPool) != VK_SUCCESS){
        throw std::runtime_error("failed to create command pool!");
    }
}

void Application::createCommandBuffer(){
    VkCommandBufferAllocateInfo allocInfo{};
    allocInfo.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
    allocInfo.commandPool = m_commandPool;
    allocInfo.level = VK_COMMAND_BUFFER_LEVEL_PRIMARY;
    allocInfo.commandBufferCount = 1;

    if(vkAllocateCommandBuffers(m_device, &allocInfo, &m_commandBuffer) != VK_SUCCESS){
        throw std::runtime_error("failed to allocate command buffer!");
    }
}

VKAPI_ATTR VkBool32 VKAPI_CALL debugCallback(
    VkDebugUtilsMessageSeverityFlagBitsEXT messageSeverity,
    VkDebugUtilsMessageTypeFlagsEXT messageType,
    const VkDebugUtilsMessengerCallbackDataEXT* pCallbackData,
    void* pUserData){

    if(messageSeverity >= VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT){
        std::cerr << "validation layer: " << pCallbackData->pMessage << std::endl;
    }
    return VK_FALSE;
}