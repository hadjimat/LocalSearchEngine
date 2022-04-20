package main.services;

import main.model.Field;
import main.model.FieldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FieldService {

    private final FieldRepository fieldRepository;

    public FieldService(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    @Transactional
    public Optional<Field> getById(int id) {
        return fieldRepository.findById(id);
    }
}
