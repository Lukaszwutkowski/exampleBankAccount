package com.richbank.userfront.controller;

import com.richbank.userfront.dao.RoleDao;
import com.richbank.userfront.domain.PrimaryAccount;
import com.richbank.userfront.domain.SavingsAccount;
import com.richbank.userfront.domain.User;
import com.richbank.userfront.domain.security.Role;
import com.richbank.userfront.domain.security.UserRole;
import com.richbank.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleDao roleDao;

    @RequestMapping("/")
    public String home() {
        return "redirect:/index";
    }

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(Model model) {
        User user = new User();

        model.addAttribute("user", user);

        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signupPost(@ModelAttribute("user") User user, Model model) {

        if (userService.checkUserExists(user.getUsername(), user.getEmail())) {

            if (userService.checkEmailExists(user.getEmail())) {
                model.addAttribute("emailExists", true);
            }

            if (userService.checkUsernameExists(user.getUsername())) {
                model.addAttribute("usernameExists", true);
            }
            return "signup";

        } else {
            Set<UserRole> userRoles = new HashSet<>();

            // Ensure the "ROLE_USER" role exists in the database
            Role userRole = roleDao.findByName("ROLE_USER");
            if (userRole == null) {
                model.addAttribute("roleError", "Required role 'ROLE_USER' does not exist.");
                return "signup"; // Return to signup page with error message
            }

            // Add the role to the user
            userRoles.add(new UserRole(user, userRole));
            userService.createUser(user, userRoles);

            return "redirect:/";
        }
    }


    @RequestMapping("/userFront")
    public String userFront(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        PrimaryAccount primaryAccount = user.getPrimaryAccount();
        SavingsAccount savingsAccount = user.getSavingsAccount();

        model.addAttribute("primaryAccount", primaryAccount);
        model.addAttribute("savingsAccount", savingsAccount);

        return "userFront";
    }
}
