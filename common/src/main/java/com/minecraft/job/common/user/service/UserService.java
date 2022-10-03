package com.minecraft.job.common.user.service;

import com.minecraft.job.common.user.domain.User;

public interface UserService {

    User create(
            String email, String password,
            String nickname, String interest, Long age
    );
}