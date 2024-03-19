package hexlet.code.service;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskParamsDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final TaskSpecification taskSpecification;

    public List<TaskDTO> index(TaskParamsDTO params) {
        var spec = taskSpecification.build(params);
        return taskMapper.map(taskRepository.findAll(spec));
    }

    public TaskDTO show(Long id) {
        var task =  taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return taskMapper.map(task);
    }

    public TaskDTO create(TaskCreateDTO taskData) {
        var task = taskMapper.map(taskData);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO update(TaskUpdateDTO taskData,Long id) {
        var taskStatus =  taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskMapper.update(taskData, taskStatus);
        taskRepository.save(taskStatus);
        return taskMapper.map(taskStatus);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
