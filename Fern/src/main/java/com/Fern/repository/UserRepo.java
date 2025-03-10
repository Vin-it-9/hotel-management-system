package com.Fern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.Fern.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

	public User findByEmail(String emaill);

	public User findByVerificationCode(String code);

	boolean existsByEmail(String email);

	@Query("update User u set u.failedAttempt=?1 where email=?2 ")
	@Modifying
	public void updateFailedAttempt(int attempt, String email);

	User getUserByEmail(String email);

	List<User> findByRole(String role);

	Optional<User> findById(int id);


}
