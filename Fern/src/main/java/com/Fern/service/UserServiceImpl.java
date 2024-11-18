package com.Fern.service;

import java.util.Date;
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


	@Override
	public User saveUser(User user, String url) {

		if (userRepo.existsByEmail(user.getEmail())) {
			return null;
		}

		String password = passwordEncoder.encode(user.getPassword());

		user.setPassword(password);
		user.setRole("ROLE_USER");

		user.setEnable(false);
		user.setVerificationCode(UUID.randomUUID().toString());

		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		user.setLockTime(null);

		User newuser = userRepo.save(user);

		if (newuser != null) {
			sendEmail(newuser, url);
		}
		return newuser;
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


}
