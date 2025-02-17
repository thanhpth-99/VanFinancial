package van.vanfinancial.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import van.vanfinancial.dto.request.AuthenticationDtoRequest;
import van.vanfinancial.dto.request.IntrospectDtoRequest;
import van.vanfinancial.dto.request.LogoutDtoRequest;
import van.vanfinancial.dto.request.RefreshDtoRequest;
import van.vanfinancial.dto.response.AuthenticationDtoResponse;
import van.vanfinancial.dto.response.IntrospectDtoResponse;
import van.vanfinancial.entity.InvalidatedToken;
import van.vanfinancial.entity.User;
import van.vanfinancial.enums.ErrorCode;
import van.vanfinancial.exception.AppException;
import van.vanfinancial.mapper.InvalidatedTokenMapper;
import van.vanfinancial.mapper.UserMapper;
import van.vanfinancial.service.AuthenticationService;
import van.vanfinancial.util.DateUtil;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserMapper userMapper;
    private final InvalidatedTokenMapper tokenMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.signer-key}")
    protected String SECRET_KEY;

    @Override
    public AuthenticationDtoResponse authenticate(AuthenticationDtoRequest request) throws JOSEException {
        var user = userMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        if (user.getStatus().equals(false)) throw new AppException(ErrorCode.ACCOUNT_HAS_BEEN_DISABLE);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        //String jwtId = generateUUID();
        String jwtId = user.getUserId();
        var accessToken = generateAccessToken(user, jwtId);
        var refreshToken = generateRefreshToken(user, jwtId);
        tokenMapper.insertValidatedToken(user.getUserId(), refreshToken);
        String roleName = user.getRole().getRoleName();

        return AuthenticationDtoResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(roleName)
                .build();
    }

    private String generateAccessToken(User user, String UUID) throws JOSEException {
        var roleName = user.getRole().getRoleName();
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserName())
                .jwtID(UUID)
                .issueTime(new Date())
                .expirationTime(Date.from(
                        Instant.now()
                                .plus(1, ChronoUnit.HOURS)
                        )
                )
                .claim("token_type", "access")
                .claim("scope", roleName)
                .build();

        return signToken(jwtClaimsSet, jwsHeader);
    }

    private String generateRefreshToken(User user, String UUID) throws JOSEException {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserName())
                .jwtID(UUID)
                .issueTime(new Date())
                .expirationTime(Date.from(
                        Instant.now()
                                .plus(30, ChronoUnit.DAYS)
                        )
                )
                .claim("token_type", "refresh")
                .build();

        return signToken(jwtClaimsSet, jwsHeader);
    }

    private String signToken(JWTClaimsSet jwtClaimsSet, JWSHeader jwsHeader) throws JOSEException {
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
        return jwsObject.serialize();
    }

    @Override
    public Void logout(LogoutDtoRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), false);
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

//        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//        var expTime = DateUtil.convertDateToTimestamp(expirationTime);
//
//        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
//                .id(jwtId)
//                .expTime(expTime)
//                .build();

        tokenMapper.deleteValidatedToken(jwtId);
        return null;
    }

    @Override
    public IntrospectDtoResponse introspect(IntrospectDtoRequest request) throws JOSEException, ParseException {
        verifyToken(request.getToken(), false);
        return IntrospectDtoResponse.builder()
                .valid(true)
                .build();
    }

    @Override
    public AuthenticationDtoResponse refresh(RefreshDtoRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(request.getRefreshToken(), true);

        var tokenType = signedJWT.getJWTClaimsSet().getClaim("token_type");
        String username = signedJWT.getJWTClaimsSet().getSubject();
        if (!"refresh".equals(tokenType) || username.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        var user = userMapper.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
//        Date expTime = signedJWT.getJWTClaimsSet().getExpirationTime();

//        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
//                .id(jwtId)
//                .expTime(DateUtil.convertDateToTimestamp(expTime))
//                .build();
        tokenMapper.deleteValidatedToken(jwtId);

//        String newJWTId = generateUUID();
        String newAccessToken = generateAccessToken(user, jwtId);
        String newRefreshToken = generateRefreshToken(user, jwtId);
        tokenMapper.insertValidatedToken(jwtId, newRefreshToken);
        return AuthenticationDtoResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private SignedJWT verifyToken(String token, Boolean isRefresh) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

        var tokenType = signedJWT.getJWTClaimsSet().getClaim("token_type");
        if (isRefresh && !"refresh".equals(tokenType)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        } else if (!isRefresh && !"access".equals(tokenType)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean isExpired = expiration.after(new Date());
        boolean isVerified = signedJWT.verify(verifier);

        if (!(isExpired || isVerified)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        if (!tokenMapper.isValidatedTokenExists(jwtId, token) && isRefresh) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        return signedJWT;
    }

//    private String generateUUID() {
//        return UUID.randomUUID().toString();
//    }
}

