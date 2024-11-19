package com.Fern.controller;

import com.Fern.entity.User;
import com.Fern.repository.UserRepo;
import com.Fern.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @ModelAttribute
    public void commonUser(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
    }

    @GetMapping("/profile")
    public String profile() {
        return "employee/employee_profile";
    }
    @GetMapping("/")
    public String index() {
        return "employee/employee_index";
    }
    @GetMapping("/change-password")
    public String changePassword() {
        return "employee/change-password";
    }


    @GetMapping("/editProfile")
    public String showEditProfilePage(Model model, Principal principal) {

        String email = principal.getName();
        User user = userRepo.getUserByEmail(email);

        model.addAttribute("user", user);

        return "employee/edit_Profile";

    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute User user, Principal principal, HttpSession session) {

        String email = principal.getName();

        boolean isUpdated = userServiceImpl.updateUserProfile(user, email);

        if (isUpdated) {
            session.setAttribute("msg", "Profile updated successfully.");
        } else {
            session.setAttribute("msg", "Error updating profile.");
        }

        return "redirect:/employee/editProfile";

    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            return "/employee/change-password";
        }

        boolean isChanged = userServiceImpl.changePasswordByEmail(userDetails.getUsername(), currentPassword, newPassword);

        if (isChanged) {
            redirectAttributes.addFlashAttribute("message", "Password changed successfully!");
            return "redirect:/employee/change-password";
        } else {
            model.addAttribute("error", "Current password is incorrect.");
            return "/employee/change-password";
        }
    }


}
