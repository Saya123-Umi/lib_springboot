package com.southwind.service;

import com.southwind.entity.Book;
import com.baomidou.mybatisplus.extension.service.IService;
import com.southwind.vo.PageVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2023-03-07
 */
public interface BookService extends IService<Book> {
    PageVO pageList(Integer currentPage);
    PageVO searchByKeyWord(String keyWord, Integer currentPage);
    PageVO searchBySort(Integer sid, Integer currentPage);
    List<String> findBooksByKeyword(String key); // 新增方法声明
}
