package com.southwind.service;

import com.southwind.entity.Back;
import com.baomidou.mybatisplus.extension.service.IService;
import com.southwind.vo.BackVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2023-03-07
 */
public interface BackService extends IService<Back> {
    List<BackVO> backList(); // 获取所有归还记录
    void allowAll();
}
