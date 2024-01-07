package com.example.highload.admin.service.impl;

import com.example.highload.admin.exception.data.NotFoundEntityByIdException;
import com.example.highload.admin.model.dto.full.FullDocumentTypeDto;
import com.example.highload.admin.model.entity.DocumentType;
import com.example.highload.admin.service.data.impl.DocumentTypeServiceImpl;
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
public class DocumentTypeServiceImplTest {
    @Mock
    private DocumentTypeRepository documentTypeRepository;
    @Mock
    private DocumentTypeMapper documentTypeMapper;

    @InjectMocks
    private DocumentTypeServiceImpl documentTypeService;

    @Test
    public void getDocumentTypeByIdCorrectTest() {
        long id = 1L;
        String name = "test";
        DocumentType documentType = new DocumentType(id, name);
        when(documentTypeRepository.findById(id)).thenReturn(Optional.of(documentType));
        when(documentTypeMapper.documentTypeToFullDto(documentType)).thenReturn(new FullDocumentTypeDto(id, name));

        FullDocumentTypeDto expected = documentTypeService.getDocumentTypeById(id);
        assertEquals(expected.getId(), id);
        assertEquals(expected.getName(), name);
    }

    @Test
    public void getDocumentTypeByIdShouldThrowException() {
        long id = 1L;
        when(documentTypeRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundEntityByIdException.class,
                () -> documentTypeService.getDocumentTypeById(id));
    }
}
