package com.southwind.controller;

import com.southwind.entity.User;
import com.southwind.form.UserRegisterForm;
import com.southwind.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import com.southwind.entity.User;
import com.southwind.form.UserRegisterForm;
import com.southwind.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{url}")
    public String redirect(@PathVariable("url") String url) {
        return "/user/" + url;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @PostMapping("/add")
    public String add(User user) {
        this.userService.save(user);
        return "redirect:/sysadmin/userList";
    }

    @GetMapping("/findById/{id}")
    public String findById(@PathVariable("id") Integer id, Model model) {
        User user = this.userService.getById(id);
        model.addAttribute("user", user);
        return "/sysadmin/updateUser";
    }

    @PostMapping("/update")
    public String update(User user) {
        this.userService.updateById(user);
        return "redirect:/sysadmin/userList";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        this.userService.removeById(id);
        return "redirect:/sysadmin/userList";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userRegisterForm", new UserRegisterForm());
        return "/user/register";
    }

    @PostMapping("/register")
    public String register(UserRegisterForm userRegisterForm, Model model) {
        if (!userRegisterForm.getPassword().equals(userRegisterForm.getConfirmPassword())) {
            model.addAttribute("message", "两次输入的密码不一致！");
            return "/user/register";
        }

        boolean usernameExists = userService.isUsernameExists(userRegisterForm.getUsername());
        if (usernameExists) {
            model.addAttribute("message", "用户名已存在，请选择其他用户名！");
            return "/user/register";
        }

        User user = new User();
        user.setUsername(userRegisterForm.getUsername());
        user.setPassword(userRegisterForm.getPassword());
        boolean success = userService.save(user);
        if (success) {
            return "redirect:/login";
        } else {
            model.addAttribute("message", "注册失败！");
            return "/user/register";
        }
    }
}
