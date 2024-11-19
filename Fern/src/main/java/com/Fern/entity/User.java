package com.Fern.entity;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	@Column(length = 10)
	private String mobileNo;

	@Column(nullable = false , unique = true )
	private String email;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private Image image;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date accountCreationDate;

	@Column(nullable = false)
	private String password;

	@Column(length = 15)
	private String gender;

	@Column(nullable = true)
	private String address;

	@Column(nullable = true)
	private LocalDate dateOfBirth;

	private String role;

	private boolean enable;

	private String verificationCode;

	private boolean isAccountNonLocked;

	private int failedAttempt;

	private Date lockTime;

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", mobileNo=" + mobileNo + ", password=" + password + "]";
	}

}
