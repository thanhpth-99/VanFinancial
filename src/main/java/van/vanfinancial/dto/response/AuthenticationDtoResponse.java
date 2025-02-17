package van.vanfinancial.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationDtoResponse {
    private String accessToken;
    private String refreshToken;
    private String role;
}
