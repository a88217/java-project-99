package hexlet.code.service;

import hexlet.code.dto.taskStatuses.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatuses.TaskStatusDTO;
import hexlet.code.dto.taskStatuses.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    private final TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> index() {
        var taskStatuses = taskStatusRepository.findAll();
        return taskStatuses.stream()
                .map(p -> taskStatusMapper.map(p))
                .toList();
    }

    public TaskStatusDTO show(Long id) {
        var taskStatus =  taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + id + " not found"));
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        var taskStatus = taskStatusMapper.map(taskStatusData);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO update(TaskStatusUpdateDTO taskStatusData, Long id) {
        var taskStatus =  taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + id + " not found"));
        taskStatusMapper.update(taskStatusData, taskStatus);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
