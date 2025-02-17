package van.vanfinancial.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationDtoRequest {
    private String username;
    private String password;
}
