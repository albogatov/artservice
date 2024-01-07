package com.example.highload.admin.service.impl;

import com.example.highload.admin.exception.data.NotFoundEntityByIdException;
import com.example.highload.admin.model.dto.full.FullBannedReasonDto;
import com.example.highload.admin.model.entity.BannedReason;
import com.example.highload.admin.service.data.impl.BannedReasonServiceImpl;
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
public class BannedReasonServiceImplTest {
    @Mock
    private BannedReasonRepository bannedReasonRepository;
    @Mock
    private BannedReasonMapper bannedReasonMapper;

    @InjectMocks
    private BannedReasonServiceImpl bannedReasonService;

    @Test
    public void getBannedReasonByIdCorrectTest() {
        long id = 1L;
        String name = "test";
        BannedReason bannedReason = new BannedReason(id, name);
        when(bannedReasonRepository.findById(id)).thenReturn(Optional.of(bannedReason));
        when(bannedReasonMapper.bannedReasonToFullDto(bannedReason)).thenReturn(new FullBannedReasonDto(id, name));

        FullBannedReasonDto expected = bannedReasonService.getBannedReasonById(id);
        assertEquals(expected.getId(), id);
        assertEquals(expected.getName(), name);
    }

    @Test
    public void getBannedReasonByIdShouldThrowException() {
        long id = 1L;
        when(bannedReasonRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundEntityByIdException.class,
                () -> bannedReasonService.getBannedReasonById(id));
    }
}
