package hexlet.code.dto.taskStatuses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;


@Getter
@Setter
public class TaskStatusUpdateDTO {

    @Size(min = 1)
    @NotBlank
    private JsonNullable<String> name;

    @Size(min = 1)
    @NotBlank
    private JsonNullable<String> slug;

}
