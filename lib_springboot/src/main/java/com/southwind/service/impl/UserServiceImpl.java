package com.southwind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.southwind.entity.User;
import com.southwind.mapper.UserMapper;
import com.southwind.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existingUser = this.getOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (existingUser != null) {
            return false; // 用户名已存在
        }
        return this.save(user); // 保存用户
    }

    @Override
    public boolean isUsernameExists(String username) {
        User existingUser = this.getOne(new QueryWrapper<User>().eq("username", username));
        return existingUser != null;
    }
}
