package hexlet.code.controller.api;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @GetMapping(path = "")
    public ResponseEntity<List<TaskDTO>> index() {
        var tasks = taskRepository.findAll();
        var result = tasks.stream()
                .map(p -> taskMapper.map(p))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))
                .body(result);
    }

    @GetMapping(path = "/{id}")
    public TaskDTO show(@PathVariable long id) {
        var task =  taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return taskMapper.map(task);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TaskCreateDTO taskData) {
        var task = taskMapper.map(taskData);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    @PutMapping(path = "/{id}")
    public TaskDTO update(@Valid @RequestBody TaskUpdateDTO taskData, @PathVariable Long id) {
        var taskStatus =  taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskMapper.update(taskData, taskStatus);
        taskRepository.save(taskStatus);
        return taskMapper.map(taskStatus);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}
