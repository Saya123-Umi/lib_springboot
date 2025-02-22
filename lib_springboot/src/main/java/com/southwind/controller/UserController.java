package com.southwind.controller;

import ch.qos.logback.core.BasicStatusManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.southwind.entity.Back;
import com.southwind.entity.Borrow;
import com.southwind.entity.User;
import com.southwind.form.UserRegisterForm;
import com.southwind.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    // BookController.java 顶部添加
    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BookService bookService;
    private BackService backService;

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
    @Transactional
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttrs) {
        // 检查是否存在未完成的借阅记录
        List<Borrow> borrowList = this.borrowService.list(
                new QueryWrapper<Borrow>()
                        .eq("uid", id)
                        .in("status", Arrays.asList(0, 1, 3)) // 0-待审批 1-已通过 3-归还申请中
        );

        if (!borrowList.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "用户存在未归还的借阅记录，无法删除");
            return "redirect:/sysadmin/userList";
        }

        // 删除用户所有关联的归还记录
        QueryWrapper<Back> backQuery = new QueryWrapper<>();
        backQuery.inSql("brid", "SELECT id FROM borrow WHERE uid=" + id);
        this.backService.remove(backQuery);

        // 删除用户所有借阅记录
        QueryWrapper<Borrow> borrowQuery = new QueryWrapper<>();
        borrowQuery.eq("uid", id);
        this.borrowService.remove(borrowQuery);

        // 最后删除用户
        this.userService.removeById(id);
        redirectAttrs.addFlashAttribute("success", "用户删除成功");
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
