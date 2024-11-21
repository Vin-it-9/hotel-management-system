package com.Fern.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.Fern.entity.User;
import com.Fern.repository.UserRepo;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

	public UserServiceImpl(UserRepo userRepository) {
		this.userRepo = userRepository;
	}

	@Override
	public User saveUser(User user, String url) {

		if (userRepo.existsByEmail(user.getEmail())) {
			return null;
		}

		String password = passwordEncoder.encode(user.getPassword());
		user.setPassword(password);
		user.setEnable(false);
		user.setVerificationCode(UUID.randomUUID().toString());
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		user.setLockTime(null);
		user.setRole("ROLE_USER");

		User newUser = userRepo.save(user);

		if (newUser != null) {
			sendEmail(newUser, url);
		}
		return newUser;
	}


	public boolean changePasswordByEmail(String email, String currentPassword, String newPassword) {

		User user = userRepo.findByEmail(email);

		if (user != null && passwordEncoder.matches(currentPassword, user.getPassword())) {
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepo.save(user);
			return true;
		}
		return false;

	}

	@Override
	public boolean updateUserProfile(User updatedUser, String email) {

		User existingUser = userRepo.findByEmail(email);

		if (existingUser != null) {

			existingUser.setName(updatedUser.getName());
			existingUser.setMobileNo(updatedUser.getMobileNo());
			existingUser.setGender(updatedUser.getGender());
			existingUser.setAddress(updatedUser.getAddress());
			existingUser.setDateOfBirth(updatedUser.getDateOfBirth());

			userRepo.save(existingUser);
			return true;
		}
		return false;
	}

	public User getUserByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public void sendEmail(User user, String url) {

		String from = "springboot2559@gmail.com";
		String to = user.getEmail();
		String subject = "Account Verfication";

		String content =
				"<div style=\"font-family: Arial, sans-serif; color: #333; line-height: 1.6; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
						"<h2 style=\"text-align: center; color: #4CAF50; font-size: 24px; margin-bottom: 30px;\">Account Verification</h2>" +

						"<p style=\"font-size: 16px;\">Dear [[name]],</p>" +
						"<p style=\"font-size: 16px;\">Thank you for registering! Please click the button below to verify your account:</p>" +

						"<div style=\"text-align: center; margin: 30px 0;\">" +
						"<a href=\"[[URL]]\" target=\"_self\" style=\"background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; font-size: 16px; border-radius: 5px; display: inline-block;\">VERIFY</a>" +
						"</div>" +

						"<p style=\"font-size: 16px;\">If you did not register for an account, you can safely ignore this email.</p>" +
						"<p style=\"font-size: 16px;\">Thank you,<br>Nexus Team</p>" +

						"<hr style=\"border: 0; border-top: 1px solid #ddd; margin: 30px 0;\">" +
						"<p style=\"font-size: 12px; color: #999; text-align: center;\">If you have any questions, please contact us at springboot2559@gmail.com.</p>" +
						"</div>";


		try {

			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom(from, "Nexus");
			helper.setTo(to);
			helper.setSubject(subject);

			content = content.replace("[[name]]", user.getName());
			String siteUrl = url + "/verify?code=" + user.getVerificationCode();

			System.out.println(siteUrl);

			content = content.replace("[[URL]]", siteUrl);

			helper.setText(content, true);

			mailSender.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean verifyAccount(String verificationCode) {
		User user = userRepo.findByVerificationCode(verificationCode);
		if (user == null) {
			return false;
		} else {
			user.setEnable(true);
			user.setVerificationCode(null);
			userRepo.save(user);
			return true;
		}

	}

	@Override
	public void removeSessionMessage() {
		HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest()
				.getSession();
		session.removeAttribute("msg");
	}

	@Override
	public void increaseFailedAttempt(User user) {
		int attempt = user.getFailedAttempt() + 1;
		userRepo.updateFailedAttempt(attempt, user.getEmail());
	}

	private static final long lock_duration_time = 30 * 1000;

	public static final long ATTEMPT_TIME = 3;

	@Override
	public void resetAttempt(String email) {
		userRepo.updateFailedAttempt(0, email);
	}

	@Override
	public void lock(User user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepo.save(user);
	}

	@Override
	public boolean unlockAccountTimeExpired(User user) {
		long lockTimeInMills = user.getLockTime().getTime();
		long currentTimeMillis = System.currentTimeMillis();
		if (lockTimeInMills + lock_duration_time < currentTimeMillis) {
			user.setAccountNonLocked(true);
			user.setLockTime(null);
			user.setFailedAttempt(0);
			userRepo.save(user);
			return true;
		}
		return false;
	}

	@Override
	public List<User> getAllUsersByRole(String role) {
		return userRepo.findByRole(role);
	}

	@Override
	public void deleteUserById(int userId) {
		User user = userRepo.findById((long) userId)
				.orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
		userRepo.delete(user);
	}

	@Override
	public User getUserById(int userId) {
		return userRepo.findById(userId).orElse(null);
	}

	@Override
	public void updateUser(int userId, String name, String mobileNo, String gender, String address, LocalDate dateOfBirth) {

		User user = getUserById(userId);
		user.setName(name);
		user.setMobileNo(mobileNo);
		user.setGender(gender);
		user.setAddress(address);
		user.setDateOfBirth(dateOfBirth);

		userRepo.save(user);

	}

}
