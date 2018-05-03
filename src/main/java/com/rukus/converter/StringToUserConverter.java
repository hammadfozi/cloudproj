package com.rukus.converter;

import com.rukus.model.User;
import com.rukus.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToUserConverter implements Converter<String, User> {

    private final UserService userService;

    @Autowired
    public StringToUserConverter(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets User by Id
     */
    @NotNull
    @Override
    public User convert(@NotNull String element) {
        return userService.findById(Integer.parseInt(element));
    }

}