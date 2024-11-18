package com.Fern.service;

import com.Fern.entity.User;


public interface UserService {

	public User saveUser(User user, String url);

	public void removeSessionMessage();

	public void sendEmail(User user, String path);

	public boolean verifyAccount(String verificationCode);

	public void increaseFailedAttempt(User user);

	public void resetAttempt(String email);

	public void lock(User user);

	public boolean unlockAccountTimeExpired(User user);

	boolean updateUserProfile(User user,  String email  );


}
