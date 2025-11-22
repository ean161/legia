package vn.ean.legia.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.ean.legia.model.User;
import vn.ean.legia.service.AuthService;
import vn.ean.legia.service.UserService;
import vn.ean.legia.util.Response;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private final Response res;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    public AuthController() {
        this.res = new Response();
    }

    @GetMapping
    public String index(@RequestParam(name = "r", required = false) String redirect, @RequestParam(name = "type", required = false) String type, Model model, HttpServletRequest request, HttpSession session) {
        User user = userService.extractUser(request);

        if (type == null || type.isEmpty()) {
            type = "login";
        }

        model.addAttribute("type", type);
        model.addAttribute("typeTitle", type.equals("login") ? "Đăng nhập" : "Đăng ký");

        if (user == null) {
            return "auth";
        }
        
        if (redirect == null || redirect.isEmpty()) {
            redirect = "/";
        }

        session.setAttribute("user", user);
        return "redirect:" + redirect;
    }

    @PostMapping("/me")
    @ResponseBody
    public Object me(HttpServletRequest request) {
        return userService.extractUser(request);
    }

    @PostMapping
    @ResponseBody
    public Map<String, Object> auth(@RequestParam(required = false) String type, @RequestParam(required = false) String username, @RequestParam(required = false) String password, @RequestParam(required = false) String redirect, HttpServletRequest request, HttpServletResponse response) {
        if (redirect == null || redirect.isEmpty()) {
            redirect = "/";
        }

        try {
            if ("login".equals(type)) {
                User isExist = authService.login(username, password);
                if (isExist == null) {
                    return res.create(0, "Tài khoản hoặc mật khẩu không đúng", null);
                }

                authService.giveAccess(username, response);
                return res.create(2, null, redirect);

            } else if ("register".equals(type)) {
                authService.register(username, password);
                authService.giveAccess(username, response);
                return res.create(2, null, redirect);
            }

            return res.create(0, "Yêu cầu không hợp lệ", null);
        } catch (RuntimeException ex) {
            return res.create(0, ex.getMessage(), null);
        }
    }
}
