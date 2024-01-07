package com.example.highload.admin.service.impl;

import com.example.highload.admin.model.dto.full.*;
import com.example.highload.admin.model.dto.partial.ShortBannedListDto;
import com.example.highload.admin.model.dto.partial.ShortViolationDto;
import com.example.highload.admin.service.business.impl.ViolationOperationServiceImpl;
import com.example.highload.admin.service.data.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViolationOperationServiceImplTest {
    @Mock
    private AccountService accountService;
    @Mock
    private ViolationTypeService violationTypeService;
    @Spy
    private ViolationService violationService;
    @Mock
    private HumanService humanService;
    @Mock
    private BannedReasonService bannedReasonService;
    @Mock
    private BannedListService bannedListService;

    @InjectMocks
    private ViolationOperationServiceImpl violationOperationService;

    @Test
    public void addViolationWhenUserHasZeroViolations() {
        Long accountId = 1L;
        Long typeId = 1L;
        Date date = java.sql.Date.valueOf(LocalDate.now());
        String message = "test";
        ShortViolationDto shortViolationDto = new ShortViolationDto(accountId, typeId, date, message);
        FullAccountDto fullAccountDto = new FullAccountDto();
        fullAccountDto.setViolationCount(0);
        fullAccountDto.setId(accountId);
        when(accountService.getAccountById(accountId)).thenReturn(fullAccountDto);
        when(violationTypeService.getViolationTypeById(typeId)).thenReturn(new FullViolationTypeDto());
        when(accountService.saveAccount(fullAccountDto)).thenReturn(fullAccountDto);
        assertEquals(violationOperationService.addViolation(shortViolationDto), 1);
        verify(bannedListService, times(0)).saveBannedList(any());
    }

    @Test
    public void banTestShouldReturnTrue() {
        Long reasonId = 1L;
        Long humanId = 1L;
        ShortBannedListDto shortBannedListDto = new ShortBannedListDto(reasonId, humanId);
        FullHumanDto fullHumanDto = new FullHumanDto();
        fullHumanDto.setId(humanId);
        FullBannedReasonDto fullBannedReasonDto = new FullBannedReasonDto();
        fullBannedReasonDto.setId(reasonId);
        FullBannedListDto fullBannedListDto = new FullBannedListDto();
        fullBannedListDto.setBannedReason(fullBannedReasonDto);
        fullBannedListDto.setHuman(fullHumanDto);
        when(humanService.getHumanById(humanId)).thenReturn(fullHumanDto);
        when(bannedReasonService.getBannedReasonById(reasonId)).thenReturn(fullBannedReasonDto);
        when(bannedListService.saveBannedList(fullBannedListDto)).thenReturn(true);
        assertTrue(violationOperationService.ban(shortBannedListDto));
    }
}
