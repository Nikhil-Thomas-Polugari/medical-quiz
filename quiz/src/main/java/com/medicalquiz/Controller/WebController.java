package com.medicalquiz.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
    
    @GetMapping("/qr")
    public String qrCode() {
        return "forward:/qr_code.html";
    }
}