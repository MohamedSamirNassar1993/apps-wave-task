package com.appswave.model.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDtoResponse {

	private Long id;
	private String fullName;
	private String email;
	private String password;
	private LocalDate birthDate;
}
