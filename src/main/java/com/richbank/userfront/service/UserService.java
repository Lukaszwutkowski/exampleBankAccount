package com.richbank.userfront.service;

import com.richbank.userfront.domain.User;

import java.util.List;

public interface UserService {

    public User findByUsername(String username);

    public User findByEmail(String email);

    boolean checkUserExists(String username, String email);

    boolean checkUsernameExists(String username);

    boolean checkEmailExists(String email);

    void save (User user);

   // User createUser(User user, Set<UserRole> userRoles);

    User saveUser (User user);

    List<User> findUserList();

    void enableUser (String username);

    void disableUser (String username);
}
