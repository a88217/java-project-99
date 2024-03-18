package hexlet.code.mapper;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "taskLabelIdsToLabels")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "taskLabelIdsToLabels")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "taskLabelIds", source = "labels", qualifiedByName = "labelsToLabelIds")
    public abstract TaskDTO map(Task model);

    public abstract List<TaskDTO> map(List<Task> models);

    @Named("slugToTaskStatus")
    public TaskStatus slugToTaskStatus(String slug) {
        return taskStatusRepository.findBySlug(slug).orElseThrow();
    }

    @Named("taskLabelIdsToLabels")
    public Set<Label> taskLabelIdsToLabels(Set<Long> taskLabelIds) {
        return taskLabelIds == null ? new HashSet<>() : taskLabelIds.stream()
                .map(id -> labelRepository.findById(id).orElseThrow())
                .collect(Collectors.toSet());
    }

    @Named("labelsToLabelIds")
    public Set<Long> labelsToLabelIds(Set<Label> labels) {
        return labels.isEmpty() ? new HashSet<>() : labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}
