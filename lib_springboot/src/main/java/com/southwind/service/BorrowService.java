package com.southwind.service;

import com.southwind.entity.Borrow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.southwind.vo.AdminBorrowVO;
import com.southwind.vo.BorrowVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2023-03-07
 */
public interface BorrowService extends IService<Borrow> {
    void add(Integer uid, Integer bid);

    List<BorrowVO> borrowList(Integer uid);

    List<BorrowVO> backList(Integer uid);

    List<AdminBorrowVO> adminBorrowList();
    List<Borrow> findByUid(Integer uid);
    void allowAll();
    void notAllowAll();
}
