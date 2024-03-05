package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class UserUpdateDTO {

    @NotNull
    private JsonNullable<String> firstName;
    @NotNull
    private JsonNullable<String> lastName;
    @Email
    private JsonNullable<String> email;
    @NotNull
    private JsonNullable<String> passwordDigest;

}
