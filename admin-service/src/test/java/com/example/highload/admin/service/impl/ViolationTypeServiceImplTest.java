package com.example.highload.admin.service.impl;

import com.example.highload.admin.exception.data.NotFoundEntityByIdException;
import com.example.highload.admin.model.dto.full.FullViolationTypeDto;
import com.example.highload.admin.model.entity.ViolationType;
import com.example.highload.admin.service.data.impl.ViolationTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ViolationTypeServiceImplTest {

    @Mock
    private ViolationTypeRepository violationTypeRepository;

    @Mock
    private ViolationTypeMapper violationTypeMapper;

    @InjectMocks
    private ViolationTypeServiceImpl violationTypeService;

    @Test
    public void getViolationTypeByIdCorrectTest() {
        long id = 1L;
        String name = "test";
        ViolationType violationType = new ViolationType(id, name);
        when(violationTypeRepository.findById(id)).thenReturn(Optional.of(violationType));
        when(violationTypeMapper.violationTypeToFullDto(violationType)).thenReturn(new FullViolationTypeDto(id, name));

        FullViolationTypeDto expected = violationTypeService.getViolationTypeById(id);
        assertEquals(expected.getId(), id);
        assertEquals(expected.getName(), name);
    }

    @Test
    public void getViolationTypeByIdShouldThrowException() {
        long id = 1L;
        when(violationTypeRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundEntityByIdException.class,
                () -> violationTypeService.getViolationTypeById(id));
    }
}
