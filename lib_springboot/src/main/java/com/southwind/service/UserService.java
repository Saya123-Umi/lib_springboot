package com.southwind.service;

import com.southwind.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    boolean register(User user);
    boolean isUsernameExists(String username);
}
