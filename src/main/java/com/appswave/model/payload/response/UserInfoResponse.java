package com.appswave.model.payload.response;

import java.time.LocalDate;

public class UserInfoResponse {
	private Long id;
	private String fullName;
	private String email;
	private String role;
	private LocalDate birthDate;

	public UserInfoResponse(Long id, String fullName, String email, String role, LocalDate birthDate) {
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.role = role;
		this.birthDate = birthDate;
	}
}
