package com.southwind.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.southwind.entity.Back;
import com.southwind.entity.Book;
import com.southwind.entity.Borrow;
import com.southwind.entity.User;
import com.southwind.service.BackService;
import com.southwind.service.BookService;
import com.southwind.service.BorrowService;
import com.southwind.service.SortService;
import com.southwind.vo.BackVO;
import com.southwind.vo.BorrowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2023-03-07
 */
@Controller
@RequestMapping("/back")
public class BackController {

    @Autowired
    private BorrowService borrowService;
    @Autowired
    private BackService backService;
    @Autowired
    private BookService bookService;
    @Autowired
    private SortService sortService;

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        List<BorrowVO> backList = this.borrowService.backList(user.getId());
        model.addAttribute("list", backList);
        return "/user/back";
    }

    @GetMapping("/add")
    public String add(Integer id) {
        Back back = new Back();
        back.setBrid(id);
        this.backService.save(back);
        Borrow borrow = this.borrowService.getById(id);
        borrow.setStatus(3); // 假设3表示已归还
        this.borrowService.updateById(borrow);
        return "redirect:/back/list";
    }

    @GetMapping("/adminList")
    public String adminList(Model model) {
        List<BackVO> backVOList = this.backService.backList();
        model.addAttribute("list", backVOList);
        return "/admin/back";
    }

    @GetMapping("/allow")
    @Transactional // 添加事务注解
    public String allow(Integer id) {
        // 获取归还记录
        Back back = this.backService.getById(id);
        if(back.getStatus() != 0) {
            throw new IllegalStateException("该申请已处理过");
        }

        // 更新归还记录状态
        back.setStatus(1);
        this.backService.updateById(back);

        // 获取关联借阅记录
        Borrow borrow = this.borrowService.getById(back.getBrid());

        // 关键修复点↓↓↓
        borrow.setStatus(4); // 设置已归还状态
        this.borrowService.updateById(borrow);

        // 使用原子操作更新库存
        this.bookService.update()
                .setSql("number = number + 1")
                .eq("id", borrow.getBid())
                .update();

        return "redirect:/back/adminList";
    }


    // 新增一键归还全部功能
    @GetMapping("/addAll")
    public String addAll(HttpSession session) {
        User user = (User) session.getAttribute("user");

        // 查找所有未归还的借阅记录
        List<Borrow> borrowList = this.borrowService.list(new QueryWrapper<Borrow>().eq("uid", user.getId()).eq("status", 1));

        for (Borrow borrow : borrowList) {
            Back back = new Back();
            back.setBrid(borrow.getId());
            this.backService.save(back);

            // 更新借阅状态为已归还
            borrow.setStatus(3); // 假设3表示已归还
            this.borrowService.updateById(borrow);

            // 更新书籍数量
            Book book = this.bookService.getById(borrow.getBid());
            book.setNumber(book.getNumber() + 1);
            this.bookService.updateById(book);
        }

        return "redirect:/back/list"; // 返回到借阅列表页面
    }
}
