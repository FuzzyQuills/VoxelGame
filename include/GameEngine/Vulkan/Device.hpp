#pragma once

#define GLFW_INCLUDE_VULKAN
#include <GLFW/glfw3.h>

#include <cstdlib>

VkPhysicalDevice pickPhysicalDevice(VkInstance instance, VkSurfaceKHR surface);
VkDevice createLogicalDevice(VkPhysicalDevice physicalDevice, VkSurfaceKHR surface, VkQueue* graphicsQueue, VkQueue* presentQueue);

bool isDeviceSuitable(VkPhysicalDevice device, VkSurfaceKHR surface);
bool checkDeviceExtensionSupport(VkPhysicalDevice device);
//#ifdef NDEBUG
bool checkValidationLayerSupport();
//#endif