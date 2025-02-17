package van.vanfinancial.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String userId;
    private String userName;
    private String password;
    private Boolean status;
    private Role role;

    private String email;
    private String roleName;
    private String name;
    private String birthday;
    private String phoneNumber;
    private String avatar;
    private Boolean gender;
    private Float monthlyBudget;
    private String financialGoal;
}
