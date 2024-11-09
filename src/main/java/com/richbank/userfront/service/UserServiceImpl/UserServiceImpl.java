package com.richbank.userfront.service.UserServiceImpl;

import com.richbank.userfront.dao.RoleDao;
import com.richbank.userfront.dao.UserDao;
import com.richbank.userfront.domain.User;
import com.richbank.userfront.domain.security.Role;
import com.richbank.userfront.domain.security.UserRole;
import com.richbank.userfront.service.AccountService;
import com.richbank.userfront.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AccountService accountService;


    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public User createUser(User user, Set<UserRole> userRoles) {
        User localUser = userDao.findByUsername(user.getUsername());

        if (localUser != null) {
            LOG.info("User with username {} already exists. Nothing will be done.", user.getUsername());
        } else {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);

            for (UserRole ur : userRoles) {
                Role role = ur.getRole();
                if (role != null && role.getName() != null) {
                    Role existingRole = roleDao.findByName(role.getName());

                    if (existingRole == null) {
                        // Save role if it doesn't exist
                        existingRole = roleDao.save(role);
                    }

                    ur.setRole(existingRole); // Ensure we use an existing role
                    ur.setUser(user);
                } else {
                    LOG.error("Role or role name is null. Role: {}", role);
                    throw new IllegalArgumentException("Role or role name cannot be null");
                }
            }

            user.getUserRoles().addAll(userRoles);

            // Create primary and savings accounts
            user.setPrimaryAccount(accountService.createPrimaryAccount());
            user.setSavingsAccount(accountService.createSavingsAccount());

            if (user.getPrimaryAccount() == null || user.getSavingsAccount() == null) {
                LOG.error("Failed to create accounts for user {}. Primary or Savings account is null.", user.getUsername());
                throw new IllegalStateException("Account creation failed. Primary or Savings account is null.");
            }

            localUser = userDao.save(user);
            LOG.info("User with username {} created successfully.", user.getUsername());
        }
        return localUser;
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
        return userDao.save(user);
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
