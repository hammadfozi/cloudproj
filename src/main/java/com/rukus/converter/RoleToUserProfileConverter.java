package com.rukus.converter;

import com.rukus.model.UserProfile;
import com.rukus.service.UserProfileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoleToUserProfileConverter implements Converter<Object, UserProfile> {

    private final UserProfileService userProfileService;

    @Autowired
    public RoleToUserProfileConverter(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @NotNull
    @Override
    public UserProfile convert(@NotNull Object element) {
        return userProfileService.findById(Integer.parseInt((String) element));
    }

}