package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.taskStatuses.TaskStatusDTO;
import hexlet.code.dto.taskStatuses.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private TaskStatus testTaskStatus;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        taskStatusRepository.save(testTaskStatus);
        var testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/task_statuses")
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/task_statuses/" + testTaskStatus.getId())
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                a -> a.node("id").isEqualTo(testTaskStatus.getId()),
                a -> a.node("name").isEqualTo(testTaskStatus.getName()),
                a -> a.node("slug").isEqualTo(testTaskStatus.getSlug()),
                a -> a.node("createdAt").isEqualTo(testTaskStatus.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        );
    }

    @Test
    public void testCreate() throws Exception {
        var newTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        var data = new TaskStatusDTO();
        data.setName(newTaskStatus.getName());
        data.setSlug(newTaskStatus.getSlug());
        var request = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var taskStatus = taskStatusRepository.findBySlug(newTaskStatus.getSlug()).get();
        assertNotNull(taskStatus);
        assertThat(taskStatus.getName()).isEqualTo(newTaskStatus.getName());
        assertThat(taskStatus.getSlug()).isEqualTo(newTaskStatus.getSlug());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new TaskStatusUpdateDTO();
        data.setName(JsonNullable.of("newName"));
        data.setSlug(JsonNullable.of("newSlug"));
        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var taskStatus = taskStatusRepository.findById(testTaskStatus.getId()).get();
        assertThat(taskStatus.getSlug()).isEqualTo("newSlug");
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/task_statuses/" + testTaskStatus.getId())
                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(taskStatusRepository.existsById(testTaskStatus.getId())).isFalse();
    }

}
