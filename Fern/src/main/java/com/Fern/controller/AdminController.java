package com.Fern.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import com.Fern.dto.AmenityDTO;
import com.Fern.dto.RoomTypeDTO;
import com.Fern.entity.Image;
import com.Fern.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.Fern.entity.User;
import com.Fern.repository.UserRepo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private ImageService imageService;

	@Autowired
	private AmenityService amenityService;

	@Autowired
	private ImageServiceImpl imageServiceImpl;

	@Autowired
	private RoomTypeService roomTypeService;
    @Autowired
    private UserService userService;

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
		return "admin/admin_profile";
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
		return "admin/admin_index";
	}

	@GetMapping("/change-password")
	public String changePassword() {
		return "admin/change-password";
	}


	@GetMapping("/editProfile")
	public String showEditProfilePage(Model model, Principal principal) {

		String email = principal.getName();
		User user = userRepo.getUserByEmail(email);

		model.addAttribute("user", user);

		return "admin/edit_Profile";

	}

	@PostMapping("/updateProfile")
	public String updateProfile(@ModelAttribute User user, Principal principal, HttpSession session) {

		String email = principal.getName();

		if (user.getDateOfBirth() != null) {
			user.setDateOfBirth(LocalDate.parse(user.getDateOfBirth().toString()));
		}

		boolean isUpdated = userServiceImpl.updateUserProfile(user, email);

		if (isUpdated) {
			session.setAttribute("msg", "Profile updated successfully.");
		} else {
			session.setAttribute("msg", "Error updating profile.");
		}

		return "redirect:/admin/editProfile";

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
			return "/admin/change-password";
		}

		boolean isChanged = userServiceImpl.changePasswordByEmail(userDetails.getUsername(), currentPassword, newPassword);

		if (isChanged) {
			redirectAttributes.addFlashAttribute("message", "Password changed successfully!");
			return "redirect:/admin/change-password";
		} else {
			model.addAttribute("error", "Current password is incorrect.");
			return "/admin/change-password";
		}
	}



	@GetMapping("/setImage")
	public String setImageInSession(Principal principal, HttpSession session) throws  SQLException {
		if (principal != null) {

			String email = principal.getName();

			Image userImage = imageServiceImpl.findByUserEmail(email);
			if (userImage != null && userImage.getImage() != null) {
				byte[] imageBytes = userImage.getImage().getBytes(1, (int) userImage.getImage().length());
				String base64Image = Base64.getEncoder().encodeToString(imageBytes);
				session.setAttribute("userImage", base64Image);
			}
		}
		return "redirect:/admin/";
	}


	@GetMapping("/display")
	public ResponseEntity<byte[]> displayImageFromSession(HttpSession session) {

		String base64Image = (String) session.getAttribute("userImage");

		if (base64Image != null) {
			byte[] imageBytes = Base64.getDecoder().decode(base64Image);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/add")
	public ModelAndView addImage(){
		return new ModelAndView("addimage");
	}

	@PostMapping("/add")
	public String addImagePost(@RequestParam("image") MultipartFile file, Principal principal, HttpSession session) throws IOException,  SQLException {
		String email = principal.getName();

		User user = userRepo.getUserByEmail(email);
		byte[] bytes = file.getBytes();

		Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
		Image existingImage = imageServiceImpl.findByUserEmail(email);

		if (existingImage != null) {
			existingImage.setImage(blob);
			imageServiceImpl.update(existingImage);
		} else {
			Image newImage = new Image();
			newImage.setImage(blob);
			newImage.setUser(user);
			imageService.create(newImage);
		}

		byte[] imageBytes = blob.getBytes(1, (int) blob.length());
		String base64Image = Base64.getEncoder().encodeToString(imageBytes);
		session.setAttribute("userImage", base64Image);

		return "redirect:/admin/editProfile";
	}

	@GetMapping("/user/list")
	public String listUsers(Model model) {
		List<User> users = userServiceImpl.getAllUsersByRole("ROLE_USER");
		model.addAttribute("users", users);
		return "admin/list_users";
	}

	@PostMapping("/user/delete/{id}")
	public String deleteUser(@PathVariable("id") int userId, RedirectAttributes redirectAttributes) {
		try {
			userServiceImpl.deleteUserById(userId);
			redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/user/list";
	}

	@GetMapping("/user/edit/{id}")
	public String editUser(@PathVariable("id") int userId, Model model) {
		User user = userServiceImpl.getUserById(userId);
		model.addAttribute("user", user);  // Make sure this is passed correctly
		return "admin/edit_user";  // Thymeleaf template for editing the user
	}

	@PostMapping("/user/update/{id}")
	public String updateUser(@PathVariable("id") int userId,
							 @RequestParam String name,
							 @RequestParam String mobileNo,
							 @RequestParam String gender,
							 @RequestParam String address,
							 @RequestParam LocalDate dateOfBirth,
							 Model model) {

		try {
			userServiceImpl.updateUser(userId, name, mobileNo, gender, address, dateOfBirth );
			model.addAttribute("successMessage", "User updated successfully!");
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Error updating user: " + e.getMessage());
		}
		return "redirect:/admin/user/list";
	}

	@GetMapping("/amenities/list")
	public String getAllAmenities(Model model) {
		List<AmenityDTO> amenities = amenityService.getAllAmenities();
		model.addAttribute("amenities", amenities);
		return "admin/list_amenity";
	}

	@GetMapping("amenities/add")
	public String addAmenity(Model model) {
		model.addAttribute("amenities", new AmenityDTO());
		return "/admin/add_amenity";
	}

	@PostMapping("/amenities/add")
	public String addAmenity(@RequestParam String name, @RequestParam String description, Model model) {
		AmenityDTO amenityDTO = new AmenityDTO();
		amenityDTO.setName(name);
		amenityDTO.setDescription(description);

		try {
			amenityService.addAmenity(amenityDTO);
			model.addAttribute("successMessage", "Amenity added successfully!");
		} catch (Exception ex) {
			model.addAttribute("errorMessage", "Failed to add amenity: " + ex.getMessage());
		}

		return "redirect:/admin/amenities/list";
	}

	@PostMapping("/amenities/delete/{id}")
	public ResponseEntity<String> deleteAmenityById(@PathVariable int id) {
		amenityService.deleteAmenityById(id);
		return new ResponseEntity<>("Amenity deleted successfully.", HttpStatus.OK);
	}

	@GetMapping("/roomtype/add")
	public String addRoomType(Model model) {
		model.addAttribute("roomtype", new RoomTypeDTO());
		return "/admin/add_roomtype";
	}

	@PostMapping("/roomtype/add")
	public String addRoomType(@RequestParam String typeName, @RequestParam String description, @RequestParam String purpose, Model model) {

		RoomTypeDTO roomTypeDTO = new RoomTypeDTO();
		roomTypeDTO.setTypeName(typeName);
		roomTypeDTO.setDescription(description);
		roomTypeDTO.setPurpose(purpose);

		try {
			roomTypeService.addRoomType(roomTypeDTO);
			model.addAttribute("successMessage", "roomtype added successfully!");
		} catch (Exception ex) {
			model.addAttribute("errorMessage", "Failed to add roomtype: " + ex.getMessage());
		}

		return "redirect:/amenities/list";
	}

	@GetMapping("/roomtype/list")
	public String getAllRoomTypes(Model model) {
		List<RoomTypeDTO> amenities = roomTypeService.getAllRoomTypes();
		model.addAttribute("roomtype", amenities);
		return "admin/list_roomtype";
	}

	@PostMapping("/roomtype/delete/{id}")
	public ResponseEntity<String> deleteRoomTypeById(@PathVariable int id) {
		roomTypeService.deleteRoomTypeById(id);
		return new ResponseEntity<>("RoomType deleted successfully.", HttpStatus.OK);
	}




}
