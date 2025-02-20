package com.southwind.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String userType = "guest";

        if (session != null) {
            if (session.getAttribute("user") != null) {
                userType = "user";
            } else if (session.getAttribute("admin") != null) {
                userType = "admin";
            } else if (session.getAttribute("sysadmin") != null) {
                userType = "sysadmin";
            }
        }

        model.addAttribute("userType", userType);
        return "error";
    }
}
