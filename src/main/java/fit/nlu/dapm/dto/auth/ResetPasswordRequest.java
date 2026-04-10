package fit.nlu.dapm.dto.auth;

public class ResetPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;


    public String getEmail() {
        return email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getOtp() {
        return otp;
    }
}