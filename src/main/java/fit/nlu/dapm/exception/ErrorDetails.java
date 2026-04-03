package fit.nlu.dapm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ErrorDetails {
    private LocalDateTime timestamp;
    private String status;
    private String message;
    private List<String> errors;

    public ErrorDetails(LocalDateTime timestamp, String status, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }

    public ErrorDetails(LocalDateTime timestamp, String status, String message, List<String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}
