package fit.nlu.dapm.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOTPRequest {
    private String email;
    private String otp;
}