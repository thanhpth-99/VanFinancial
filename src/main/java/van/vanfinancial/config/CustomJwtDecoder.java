package van.vanfinancial.config;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import van.vanfinancial.dto.request.IntrospectDtoRequest;
import van.vanfinancial.service.AuthenticationService;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signer-key}")
    protected String SECRET_KEY;

    private final AuthenticationService authenticationService;

    private NimbusJwtDecoder jwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticationService.introspect(IntrospectDtoRequest.builder()
                    .token(token)
                    .build()
            );
            if (!response.getValid()) throw new JwtException("invalid token");
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(jwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes()
                    , "HS256");
            jwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();
        }
        return jwtDecoder.decode(token);
    }
}
