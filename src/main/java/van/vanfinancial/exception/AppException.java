package van.vanfinancial.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import van.vanfinancial.enums.ErrorCode;

@Getter
@Setter
@AllArgsConstructor
public class AppException extends RuntimeException {
    private ErrorCode errorCode;
}
