package com.ecommerce.library.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {
    @Size(min = 3, max = 10, message = "Invalid first name!(3-10 characters)")
    private String firstName;

    @Size(min = 1, max = 10, message = "Invalid last name!(1-10 characters)")
    private String lastName;

    @Size(min = 10, max = 10, message = "Invalid Mobile Number!(10 characters required)")
    private String mobileNumber;

    private String username;

    @Size(min = 5, max = 15, message = "Invalid password!(5-15 characters)")
    private String password;

    private String repeatPassword;
}
