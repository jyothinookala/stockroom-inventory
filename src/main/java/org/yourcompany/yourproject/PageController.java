package org.yourcompany.yourproject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String home() { return "login"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String register() { return "register"; }

    @GetMapping("/forgot-password")
    public String forgotPassword() { return "forgot-password"; }

    @GetMapping("/inventory")
    public String inventory() { return "inventory"; }
}