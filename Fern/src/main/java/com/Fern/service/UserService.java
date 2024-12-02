package com.Fern.service;

import com.Fern.entity.User;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.List;


public interface UserService {

	public User saveUser(User user, String url);

//	public User saveUser(User user, String url);

	public void removeSessionMessage();

	public void sendEmail(User user, String path);

	public boolean verifyAccount(String verificationCode);

	public void increaseFailedAttempt(User user);

	public void resetAttempt(String email);

	public void lock(User user);

	public boolean unlockAccountTimeExpired(User user);

	boolean updateUserProfile(User user,  String email  );

	List<User> getAllUsersByRole(String role);

	void deleteUserById(int userId);

	User getUserById(int userId);

	void updateUser(int userId, String name, String mobileNo, String gender, String address, LocalDate dateOfBirth);

	User findByEmail(String email);

}
