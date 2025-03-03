package com.southwind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.southwind.entity.Back;
import com.southwind.entity.Book;
import com.southwind.entity.Borrow;
import com.southwind.entity.User;
import com.southwind.mapper.BackMapper;
import com.southwind.mapper.BookMapper;
import com.southwind.mapper.BorrowMapper;
import com.southwind.mapper.UserMapper;
import com.southwind.service.BackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.southwind.vo.BackVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author admin
 * @since 2023-03-07
 */
@Service
public class BackServiceImpl extends ServiceImpl<BackMapper, Back> implements BackService {

    @Autowired
    private BackMapper backMapper;

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;

    @Override
    public List<BackVO> backList() {
        QueryWrapper<Back> backQueryWrapper = new QueryWrapper<>();
        backQueryWrapper.eq("status", 0); // 假设0表示未归还状态

        List<Back> backList = this.backMapper.selectList(backQueryWrapper);

        List<BackVO> backVOList = new ArrayList<>();

        for (Back back : backList) {
            BackVO backVO = new BackVO();

            Borrow borrow = this.borrowMapper.selectById(back.getBrid());
            User user = this.userMapper.selectById(borrow.getUid());

            backVO.setUserName(user.getUsername());

            Book book = this.bookMapper.selectById(borrow.getBid());

            BeanUtils.copyProperties(book, backVO);
            backVO.setBookName(book.getName());

            BeanUtils.copyProperties(back, backVO);

            backVOList.add(backVO);
        }

        return backVOList; // 返回归还记录列表
    }
    
    @Override
    public void allowAll() {
        // 查询所有待归还的记录（假设 status=0 表示待归还）
        QueryWrapper<Back> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0);
        List<Back> backList = backMapper.selectList(queryWrapper);

        // 遍历待归还记录，更新状态
        for (Back back : backList) {
            // 更新 Back 表状态（假设 status=1 表示已归还）
            back.setStatus(1);
            backMapper.updateById(back);

            // 更新 Borrow 表状态（假设 status=4 表示已归还）
            Borrow borrow = borrowMapper.selectById(back.getBrid());
            if (borrow != null) {
                borrow.setStatus(4);
                borrowMapper.updateById(borrow);
            }

            // 更新 Book 表库存
            Book book = bookMapper.selectById(borrow.getBid());
            if (book != null) {
                book.setNumber(book.getNumber() + 1); // 库存加 1
                bookMapper.updateById(book);
            }
        }
    }
}
