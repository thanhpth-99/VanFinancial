package van.vanfinancial.exception;

import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import van.vanfinancial.common.ApiResponse;
import van.vanfinancial.enums.ErrorCode;

import java.io.IOException;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalExceptionHandling {
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(errorCode.getStatus());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handleException(Exception e) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(ErrorCode.UNCAUGHT_EXCEPTION.getStatus());
        apiResponse.setMessage(ErrorCode.UNCAUGHT_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = BadSqlGrammarException.class)
    ResponseEntity<ApiResponse> handleBadSqlGrammarException(BadSqlGrammarException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                        .status(ErrorCode.BAD_SQL.getStatus())
                        .message(ErrorCode.BAD_SQL.getMessage())
                        .build());
    }

    @ExceptionHandler(value = DateTimeParseException.class)
    ResponseEntity<ApiResponse> handleDateTimeParseException(DateTimeParseException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                        .status(ErrorCode.DATE_EXCEPTION.getStatus())
                        .message(ErrorCode.DATE_EXCEPTION.getMessage())
                        .build());
    }

    @ExceptionHandler(value = JOSEException.class)
    ResponseEntity<ApiResponse> handleJOSEException(JOSEException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                        .status(ErrorCode.JOSE_EXCEPTION.getStatus())
                        .message(ErrorCode.JOSE_EXCEPTION.getMessage())
                        .build());
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<ApiResponse> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.builder()
                        .status(ErrorCode.ACCESS_DENIED.getStatus())
                        .message(ErrorCode.ACCESS_DENIED.getMessage())
                        .build());
    }

    @ExceptionHandler(value = AuthenticationServiceException.class)
    ResponseEntity<ApiResponse> handleAuthenticationServiceException(AuthenticationServiceException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.builder()
                        .status(ErrorCode.INVALID_TOKEN.getStatus())
                        .message(ErrorCode.INVALID_TOKEN.getMessage())
                        .build());
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<ApiResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                        .status(ErrorCode.RESOURCE_NOT_FOUND.getStatus())
                        .message(ErrorCode.RESOURCE_NOT_FOUND.getMessage())
                        .build());
    }

    @ExceptionHandler(value = MessagingException.class)
    ResponseEntity<ApiResponse> handleMessagingException(MessagingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                        .status(ErrorCode.MAIL_EXCEPTION.getStatus())
                        .message(ErrorCode.MAIL_EXCEPTION.getMessage())
                        .build());
    }

    @ExceptionHandler(value = IOException.class)
    ResponseEntity<ApiResponse> handleIOException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                        .status(ErrorCode.FILE_UPLOAD_FAILED.getStatus())
                        .message(ErrorCode.FILE_UPLOAD_FAILED.getMessage())
                        .build());
    }
}
