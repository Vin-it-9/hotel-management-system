package com.Fern.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;

import com.Fern.entity.*;
import com.Fern.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

import com.Fern.repository.UserRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private ImageServiceImpl imageServiceImpl;

	@ModelAttribute
	public void commonUser(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User user = userRepo.findByEmail(email);
			m.addAttribute("user", user);
		}
	}

	@GetMapping("/")
	public String index(Principal principal, HttpSession session, Model model) throws  SQLException {

		if (principal != null && session.getAttribute("userImage") == null) {

			String email = principal.getName();

			Image userImage = imageServiceImpl.findByUserEmail(email);
			if (userImage != null && userImage.getImage() != null) {
				byte[] imageBytes = userImage.getImage().getBytes(1, (int) userImage.getImage().length());
				String base64Image = Base64.getEncoder().encodeToString(imageBytes);
				session.setAttribute("userImage", base64Image);
			}

		}
		return "index";
	}


	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, HttpSession session, HttpServletRequest request) {

		String url = request.getRequestURL().toString();

		url = url.replace(request.getServletPath(), "");

		User savedUser = userService.saveUser(user, url);

		if (savedUser == null) {
			session.setAttribute("msg", "Email already exists. Please use a different email or login.");
			return "redirect:/register";
		}
		session.setAttribute("msg", "Registered successfully! Please check your email to verify your account.");
		return "redirect:/signin";
	}


	@GetMapping("/verify")
	public String verifyAccount(@Param("code") String code, Model m) {

		boolean f = userService.verifyAccount(code);

		if (f) {
			m.addAttribute("msg", "Sucessfully your account is verified");
		} else {
			m.addAttribute("msg", "may be your vefication code is incorrect or already veified ");
		}

		return "message";
	}

	@GetMapping("/editProfile")
	public String showEditProfilePage(Model model, Principal principal) {

		String email = principal.getName();
		User user = userRepo.getUserByEmail(email);

		model.addAttribute("user", user);

		return "editProfile";

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

		return "redirect:/editProfile";
	}

	@GetMapping("/change-password")
	public String showChangePasswordForm() {
		return "change-password";
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
			return "change-password";
		}

		boolean isChanged = userServiceImpl.changePasswordByEmail(userDetails.getUsername(), currentPassword, newPassword);

		if (isChanged) {
			redirectAttributes.addFlashAttribute("message", "Password changed successfully!");
			return "redirect:/user/change-password";
		} else {
			model.addAttribute("error", "Current password is incorrect.");
			return "change-password";
		}
	}


}
