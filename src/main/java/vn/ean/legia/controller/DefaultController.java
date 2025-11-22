package vn.ean.legia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.ean.legia.service.ServiceCategoryService;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("*")
public class DefaultController {

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    @GetMapping
    public String index(HttpServletRequest request, Model model) {
        model.addAttribute("services", serviceCategoryService.list());
        return "index";
    }
}
