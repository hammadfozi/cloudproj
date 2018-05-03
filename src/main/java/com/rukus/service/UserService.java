package com.rukus.service;

import com.rukus.Constant;
import com.rukus.model.User;
import com.rukus.service.rep.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("userService")
@Transactional
public class UserService {

    private final UserRepository dao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository dao, PasswordEncoder passwordEncoder) {
        this.dao = dao;
        this.passwordEncoder = passwordEncoder;
    }

    public User findById(final int id) {
        Optional<User> tmp = dao.findById(id);
        return tmp.orElse(null);
    }

    public User findByEmail(String email) {
        return dao.findByEmail(email).orElse(null);
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dao.save(user);
    }

    /*
     * Since the method is running with Transaction, No need to call hibernate update explicitly.
     * Just fetch the entity from db and update it with proper values within transaction.
     * It will be updated in db once transaction ends.
     */
    public void updateUser(@NonNull User user) {
        Optional<User> entity = dao.findById(user.getId());
        if (entity.isPresent()) {
            user.setId(entity.get().getId());
            dao.save(user);
        }
    }

    public void deleteByEmail(String email) {
        dao.deleteByEmail(email);
    }

    public List<User> findAllUsers() {
        return dao.findAll();
    }

    public boolean isUserEmailUnique(Integer id, String email) {
        User user = findByEmail(email);
        return (user == null || ((id != null) && (user.getId() == id)));
    }

    public List<User> findAllCustomers() {
        List<Integer> ids = dao.findByRole(Constant.USER_ROLE.VERIFIED);
        List<User> users = new ArrayList<>();
        for (Integer id : ids) users.add(findById(id));
        return users;
    }

    public List<User> findAllAdmins() {
        List<Integer> ids = dao.findByRole(Constant.USER_ROLE.ADMIN);
        List<User> users = new ArrayList<>();
        for (Integer id : ids) users.add(findById(id));
        return users;
    }
}
