package hexlet.code.controller.api;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskParamsDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping(path = "")
    public ResponseEntity<List<TaskDTO>> index(TaskParamsDTO params) {
        var result = taskService.index(params);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))
                .body(result);
    }

    @GetMapping(path = "/{id}")
    public TaskDTO show(@PathVariable long id) {
        return taskService.show(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TaskCreateDTO taskData) {
        return taskService.create(taskData);
    }

    @PutMapping(path = "/{id}")
    public TaskDTO update(@Valid @RequestBody TaskUpdateDTO taskData, @PathVariable Long id) {
        return taskService.update(taskData, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
