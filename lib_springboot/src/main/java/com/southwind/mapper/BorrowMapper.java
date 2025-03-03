package com.southwind.mapper;

import com.southwind.entity.Borrow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2023-03-07
 */
@Mapper
public interface BorrowMapper extends BaseMapper<Borrow> {
    // ...其他代码...

    @Select("select * from borrow where uid=#{uid}")
    List<Borrow> findByUid(Integer uid);
}