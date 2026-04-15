package com.example.bloodbank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to handle SPA routing fallback for the embedded Angular application.
 * Any path not mapped to an API and not containing a dot (like a file extension)
 * will be forwarded back to index.html.
 */
@Controller
public class FrontendRoutingController {

    @RequestMapping({
        "/dashboard", "/login", "/register", 
        "/patients", "/patients/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
