package vn.ean.legia.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.ean.legia.service.AuthService;

@Controller
@RequestMapping("/logout")
public class LogOutController {
    
    @Autowired
    private AuthService authService;

    @GetMapping
    public String index(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return "redirect:/";
    }
}
