package com.appswave.model.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    @NotNull
    @Size(min = 5, max = 50)
    private String fullName;
 
    @NotNull
    @Size(min = 5, max = 50)
    @Email
    private String email;

    @NotNull
    @Size(min = 6, max = 40)
    private String password;

    @NotNull
    private LocalDate birthDate;
}
