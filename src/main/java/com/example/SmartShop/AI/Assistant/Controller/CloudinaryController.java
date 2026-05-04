package com.example.SmartShop.AI.Assistant.Controller;

import com.example.SmartShop.AI.Assistant.Service.CloudinaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/cloudinary/account")
    public Object getAccountInfo() throws Exception {
        return cloudinaryService.getAccountInfo();
    }
    @GetMapping("/cloudinary/resources")
    public Object getResources() throws Exception {
        return cloudinaryService.getResources();
    }
}