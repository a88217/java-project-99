package hexlet.code.controller.api;

import hexlet.code.dto.labels.LabelCreateDTO;
import hexlet.code.dto.labels.LabelDTO;
import hexlet.code.dto.labels.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    @GetMapping(path = "")
    public ResponseEntity<List<LabelDTO>> index() {
        var labels = labelRepository.findAll();
        var result = labels.stream()
                .map(p -> labelMapper.map(p))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))
                .body(result);
    }

    @GetMapping(path = "/{id}")
    public LabelDTO show(@PathVariable long id) {
        var label =  labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        return labelMapper.map(label);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDTO create(@Valid @RequestBody LabelCreateDTO labelData) {
        var label = labelMapper.map(labelData);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    @PutMapping(path = "/{id}")
    public LabelDTO update(@Valid @RequestBody LabelUpdateDTO labelData, @PathVariable Long id) {
        var label =  labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        labelMapper.update(labelData, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        labelRepository.deleteById(id);
    }
}