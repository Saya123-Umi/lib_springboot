package com.southwind.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.southwind.entity.Book;
import com.southwind.entity.Borrow;
import com.southwind.entity.Sort;
import com.southwind.service.BookService;
import com.southwind.service.BorrowService;
import com.southwind.service.SortService;
import com.southwind.vo.BookVO;
import com.southwind.vo.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private SortService sortService;

    @Autowired
    private BorrowService borrowService;

    // 相对路径配置
    private final String tempPath = "static/temp";
    private final String uploadPath = "static/images";

    @GetMapping("/list/{current}")
    public String list(@PathVariable("current") Integer current, Model model) {
        PageVO pageVO = this.bookService.pageList(current);
        model.addAttribute("page", pageVO);
        model.addAttribute("sortList", this.sortService.list());
        return "/user/list";
    }

    @PostMapping("/search")
    public String search(String keyWord, Integer current, Integer sid, Model model) {
        PageVO pageVO;

        // 类别检索
        if (!sid.equals(0)) {
            pageVO = this.bookService.searchBySort(sid, current);
        } else {
            // 关键字检索带分页
            pageVO = this.bookService.searchByKeyWord(keyWord, current); // 使用更新后的方法
        }

        model.addAttribute("page", pageVO);
        model.addAttribute("sortList", this.sortService.list());

        return "/user/list";
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    public List<String> autocomplete(@RequestParam String key) {
        return bookService.findBooksByKeyword(key); // 仅返回书籍名称匹配的结果
    }

    @PostMapping("/add")
    public String add(Book book, @RequestParam("imageFile") MultipartFile imageFile, HttpServletRequest request) throws IOException {
        // 1. 获取临时目录和上传目录的绝对路径
        String tempDir = request.getServletContext().getRealPath(tempPath);
        String uploadDir = request.getServletContext().getRealPath(uploadPath);

        // 2. 文件上传到临时目录
        String tempFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
        Path tempFile = Paths.get(tempDir, tempFileName);

        if (!Files.exists(Paths.get(tempDir))) {
            Files.createDirectories(Paths.get(tempDir));
        }

        imageFile.transferTo(tempFile.toFile());

        // 3. 从临时目录移动到目标目录
        Path targetFile = Paths.get(uploadDir, tempFileName);

        if (!Files.exists(Paths.get(uploadDir))) {
            Files.createDirectories(Paths.get(uploadDir));
        }

        Files.move(tempFile, targetFile, StandardCopyOption.REPLACE_EXISTING);

        // 4. 设置图书的imagePath属性
        book.setImagePath(tempFileName);

        // 5. 保存图书信息到数据库
        this.bookService.save(book);

        return "redirect:/sysadmin/bookList";
    }

    @GetMapping("/findById/{id}")
    public String findById(@PathVariable("id") Integer id, Model model) {
        Book book = this.bookService.getById(id);
        model.addAttribute("book", book);
        model.addAttribute("list", this.sortService.list());

        return "/sysadmin/updateBook";
    }

    @PostMapping("/update")
    public String update(Book book, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, HttpServletRequest request) throws IOException {
        // 1. 获取临时目录和上传目录的绝对路径
        String tempDir = request.getServletContext().getRealPath(tempPath);
        String uploadDir = request.getServletContext().getRealPath(uploadPath);

        // 2. 如果有新的图片上传
        if (imageFile != null && !imageFile.isEmpty()) {
            // 生成唯一文件名
            String tempFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path tempFile = Paths.get(tempDir, tempFileName);

            // 确保临时目录存在
            if (!Files.exists(Paths.get(tempDir))) {
                Files.createDirectories(Paths.get(tempDir));
            }

            // 保存到临时目录
            imageFile.transferTo(tempFile.toFile());

            // 移动到目标目录
            Path targetFile = Paths.get(uploadDir, tempFileName);

            // 确保目标目录存在
            if (!Files.exists(Paths.get(uploadDir))) {
                Files.createDirectories(Paths.get(uploadDir));
            }

            Files.move(tempFile, targetFile, StandardCopyOption.REPLACE_EXISTING);

            // 设置图书的imagePath属性
            book.setImagePath(tempFileName);
        }

        // 3. 更新图书信息到数据库
        this.bookService.updateById(book);

        return "redirect:/sysadmin/bookList";
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttrs) {
        // 检查是否存在未完成的借阅记录
        List<Borrow> borrowList = this.borrowService.list(
                new QueryWrapper<Borrow>()
                        .eq("bid", id)
                        .in("status", Arrays.asList(0, 1, 3)) // 0-待审批 1-已通过 3-归还申请中
        );

        if (!borrowList.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "存在未归还的借阅记录，无法删除书籍");
            return "redirect:/sysadmin/bookList";
        }

        // 删除关联的借阅记录
        this.borrowService.remove(new QueryWrapper<Borrow>().eq("bid", id));
        this.bookService.removeById(id);

        redirectAttrs.addFlashAttribute("success", "书籍删除成功");
        return "redirect:/sysadmin/bookList";
    }

}
