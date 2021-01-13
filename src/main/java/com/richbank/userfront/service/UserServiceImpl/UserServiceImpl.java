package com.richbank.userfront.service.UserServiceImpl;

import com.richbank.userfront.Dao.UserDao;
import com.richbank.userfront.domain.User;
import com.richbank.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public boolean checkUserExists(String username, String email) {
        if (checkUsernameExists(username) || checkEmailExists(username)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean checkUsernameExists(String username) {
        if (null != findByUsername(username)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkEmailExists(String email) {
        if (null != findByEmail(email)) {
            return true;
        }
        return false;
    }

    @Override
    public void save(User user) {
        userDao.save(user);

    }

    @Override
    public User saveUser(User user) {
        return null;
    }

    @Override
    public List<User> findUserList() {
        return null;
    }

    @Override
    public void enableUser(String username) {

    }

    @Override
    public void disableUser(String username) {

    }
}
