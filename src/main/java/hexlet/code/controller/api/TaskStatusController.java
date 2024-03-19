package hexlet.code.controller.api;

import hexlet.code.dto.taskStatuses.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatuses.TaskStatusDTO;
import hexlet.code.dto.taskStatuses.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
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
@RequestMapping("/api/task_statuses")
@AllArgsConstructor
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    @GetMapping(path = "")
    public ResponseEntity<List<TaskStatusDTO>> index() {
        var result = taskStatusService.index();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))
                .body(result);
    }

    @GetMapping(path = "/{id}")
    public TaskStatusDTO show(@PathVariable long id) {
        return taskStatusService.show(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO taskStatusData) {
        return taskStatusService.create(taskStatusData);
    }

    @PutMapping(path = "/{id}")
    public TaskStatusDTO update(@Valid @RequestBody TaskStatusUpdateDTO taskStatusData, @PathVariable Long id) {
        return taskStatusService.update(taskStatusData, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusService.delete(id);
    }
}
