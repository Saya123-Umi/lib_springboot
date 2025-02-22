package com.southwind.controller;

import com.southwind.entity.Borrow;
import com.southwind.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/sysadmin")
public class SysadminBorrowController {

    @Autowired
    private BorrowService borrowService;

    @GetMapping("/borrowRecords")
    public String listBorrowRecords(Model model) {
        List<Borrow> borrowList = this.borrowService.list();
        model.addAttribute("borrowList", borrowList);
        return "/sysadmin/borrowRecords";
    }

    // 后续可以添加修改和删除功能
}
