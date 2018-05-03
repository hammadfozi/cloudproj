package com.rukus.service;

import com.rukus.model.UserProfile;
import com.rukus.service.rep.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("userProfileService")
@Transactional
public class UserProfileService {

    private final UserProfileRepository dao;

    @Autowired
    public UserProfileService(UserProfileRepository dao) {
        this.dao = dao;
    }

    public UserProfile findById(int id) {
        Optional<UserProfile> tmp = dao.findById(id);
        return tmp.orElse(null);
    }

    public List<UserProfile> findAll() {
        return dao.findAll();
    }
}
