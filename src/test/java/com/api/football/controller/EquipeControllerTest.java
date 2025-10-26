package com.api.football.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.football.dto.CreateEquipeRequest;
import com.api.football.dto.EquipeDto;
import com.api.football.service.EquipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour le contrôleur EquipeController.
 * 
 * @author Nice Football API Team
 * @version 1.0.0
 */
@WebMvcTest(EquipeController.class)
class EquipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipeService equipeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EquipeDto equipeDto;
    private CreateEquipeRequest createRequest;

    @BeforeEach
    void setUp() {
        equipeDto = new EquipeDto();
        equipeDto.setId(1L);
        equipeDto.setNom("OGC Nice");
        equipeDto.setAcronyme("OGC");
        equipeDto.setBudget(new BigDecimal("50000000.00"));

        createRequest = new CreateEquipeRequest();
        createRequest.setNom("OGC Nice");
        createRequest.setAcronyme("OGC");
        createRequest.setBudget(new BigDecimal("50000000.00"));
    }

    @Test
    void testGetAllEquipes_Success() throws Exception {
        // Given
        List<EquipeDto> equipes = Arrays.asList(equipeDto);
        Page<EquipeDto> pageEquipes = new PageImpl<>(equipes, PageRequest.of(0, 10), 1);
        
        when(equipeService.getAllEquipes(0, 10, "nom", "asc")).thenReturn(pageEquipes);

        // When & Then
        mockMvc.perform(get("/api/equipes")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "nom")
                .param("sortDirection", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nom").value("OGC Nice"))
                .andExpect(jsonPath("$.content[0].acronyme").value("OGC"))
                .andExpect(jsonPath("$.content[0].budget").value(50000000.00))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(equipeService).getAllEquipes(0, 10, "nom", "asc");
    }

    @Test
    void testGetAllEquipes_WithDefaultParameters() throws Exception {
        // Given
        List<EquipeDto> equipes = Arrays.asList(equipeDto);
        Page<EquipeDto> pageEquipes = new PageImpl<>(equipes, PageRequest.of(0, 10), 1);
        
        when(equipeService.getAllEquipes(0, 10, "nom", "asc")).thenReturn(pageEquipes);

        // When & Then
        mockMvc.perform(get("/api/equipes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nom").value("OGC Nice"));

        verify(equipeService).getAllEquipes(0, 10, "nom", "asc");
    }

    @Test
    void testGetAllEquipes_InvalidPage() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/equipes")
                .param("page", "-1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(equipeService, never()).getAllEquipes(anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    void testGetAllEquipes_InvalidSize() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/equipes")
                .param("page", "0")
                .param("size", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(equipeService, never()).getAllEquipes(anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    void testCreateEquipe_Success() throws Exception {
        // Given
        when(equipeService.createEquipe(any(CreateEquipeRequest.class))).thenReturn(equipeDto);

        // When & Then
        mockMvc.perform(post("/api/equipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("OGC Nice"))
                .andExpect(jsonPath("$.acronyme").value("OGC"))
                .andExpect(jsonPath("$.budget").value(50000000.00));

        verify(equipeService).createEquipe(any(CreateEquipeRequest.class));
    }

    @Test
    void testCreateEquipe_ValidationError() throws Exception {
        // Given
        CreateEquipeRequest invalidRequest = new CreateEquipeRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/equipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(equipeService, never()).createEquipe(any(CreateEquipeRequest.class));
    }

    @Test
    void testCreateEquipe_Conflict() throws Exception {
        // Given
        when(equipeService.createEquipe(any(CreateEquipeRequest.class)))
                .thenThrow(new IllegalArgumentException("Une équipe avec l'acronyme 'OGC' existe déjà"));

        // When & Then
        mockMvc.perform(post("/api/equipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Erreur de validation"))
                .andExpect(jsonPath("$.message").value("Une équipe avec l'acronyme 'OGC' existe déjà"));

        verify(equipeService).createEquipe(any(CreateEquipeRequest.class));
    }

    @Test
    void testGetEquipeById_Success() throws Exception {
        // Given
        when(equipeService.getEquipeById(1L)).thenReturn(Optional.of(equipeDto));

        // When & Then
        mockMvc.perform(get("/api/equipes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("OGC Nice"))
                .andExpect(jsonPath("$.acronyme").value("OGC"))
                .andExpect(jsonPath("$.budget").value(50000000.00));

        verify(equipeService).getEquipeById(1L);
    }

    @Test
    void testGetEquipeById_NotFound() throws Exception {
        // Given
        when(equipeService.getEquipeById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/equipes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(equipeService).getEquipeById(1L);
    }

    @Test
    void testGetEquipeByAcronyme_Success() throws Exception {
        // Given
        when(equipeService.getEquipeByAcronyme("OGC")).thenReturn(Optional.of(equipeDto));

        // When & Then
        mockMvc.perform(get("/api/equipes/acronyme/OGC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("OGC Nice"))
                .andExpect(jsonPath("$.acronyme").value("OGC"))
                .andExpect(jsonPath("$.budget").value(50000000.00));

        verify(equipeService).getEquipeByAcronyme("OGC");
    }

    @Test
    void testGetEquipeByAcronyme_NotFound() throws Exception {
        // Given
        when(equipeService.getEquipeByAcronyme("OGC")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/equipes/acronyme/OGC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(equipeService).getEquipeByAcronyme("OGC");
    }
}
