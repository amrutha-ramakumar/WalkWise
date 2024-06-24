package com.ecommerce.library.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomerDto {
    private Long customer_id;
    @NotBlank(message = "enter the name")
    @Size(max=20,message = " name shoud maximum 20 letter")
    private String name;
    @NotBlank(message = "enter the email")
    private String email;
    @NotBlank(message = "Password required")
    @Size(min=6,max=20,message = " Password range(6-20)")
    private String password;
    private String repeatPassword;
    @Min(value = 1000000000, message="Minimum 10 digits required")
    @Max(value = 9999999999L, message="Maximum 10 digits required")
    private Long mobile;
    private String role;
    private boolean activated;
    private boolean blocked;
    private long otp;



}
