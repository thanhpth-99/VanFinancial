package van.vanfinancial.service;

import com.nimbusds.jose.JOSEException;
import van.vanfinancial.dto.request.AuthenticationDtoRequest;
import van.vanfinancial.dto.request.IntrospectDtoRequest;
import van.vanfinancial.dto.request.LogoutDtoRequest;
import van.vanfinancial.dto.request.RefreshDtoRequest;
import van.vanfinancial.dto.response.AuthenticationDtoResponse;
import van.vanfinancial.dto.response.IntrospectDtoResponse;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationDtoResponse authenticate(AuthenticationDtoRequest request) throws JOSEException;
    Void logout(LogoutDtoRequest request) throws ParseException, JOSEException;
    IntrospectDtoResponse introspect(IntrospectDtoRequest request) throws JOSEException, ParseException;
    AuthenticationDtoResponse refresh(RefreshDtoRequest request) throws ParseException, JOSEException;
}
