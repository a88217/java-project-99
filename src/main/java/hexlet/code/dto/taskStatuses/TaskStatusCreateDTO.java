package hexlet.code.dto.taskStatuses;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusCreateDTO {

    @Size(min = 1)
    @NotBlank
    private String name;

    @Column(unique = true)
    @Size(min = 1)
    @NotBlank
    private String slug;

}
