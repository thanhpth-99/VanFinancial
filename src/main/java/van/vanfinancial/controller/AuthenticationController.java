package van.vanfinancial.controller;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import van.vanfinancial.common.ApiResponse;
import van.vanfinancial.dto.request.AuthenticationDtoRequest;
import van.vanfinancial.dto.request.IntrospectDtoRequest;
import van.vanfinancial.dto.request.LogoutDtoRequest;
import van.vanfinancial.dto.request.RefreshDtoRequest;
import van.vanfinancial.dto.response.AuthenticationDtoResponse;
import van.vanfinancial.dto.response.IntrospectDtoResponse;
import van.vanfinancial.service.AuthenticationService;

import java.text.ParseException;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationDtoResponse> login(@RequestBody AuthenticationDtoRequest request) throws JOSEException {
        return ApiResponse.<AuthenticationDtoResponse>builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .data(authenticationService.authenticate(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutDtoRequest request) throws ParseException, JOSEException {
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .data(authenticationService.logout(request))
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectDtoResponse> logout(@RequestBody IntrospectDtoRequest request) throws ParseException, JOSEException {
        return ApiResponse.<IntrospectDtoResponse>builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .data(authenticationService.introspect(request))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationDtoResponse> refresh(@RequestBody RefreshDtoRequest request) throws ParseException, JOSEException {
        return ApiResponse.<AuthenticationDtoResponse>builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .data(authenticationService.refresh(request))
                .build();
    }
}
