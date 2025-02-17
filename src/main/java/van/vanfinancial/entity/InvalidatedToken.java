package van.vanfinancial.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class InvalidatedToken {
    private String id;
    private Date expTime;
}
