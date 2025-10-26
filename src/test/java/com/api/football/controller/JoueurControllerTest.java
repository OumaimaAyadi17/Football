package com.api.football.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.football.dto.CreateJoueurRequest;
import com.api.football.dto.JoueurDto;
import com.api.football.service.JoueurService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour le contrôleur JoueurController.
 *
 * @author Nice Football API Team
 * @version 1.0.0
 */
@WebMvcTest(JoueurController.class)
class JoueurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JoueurService joueurService;

    @Autowired
    private ObjectMapper objectMapper;

    private JoueurDto joueurDto;
    private CreateJoueurRequest createRequest;

    @BeforeEach
    void setUp() {
        joueurDto = new JoueurDto();
        joueurDto.setId(1L);
        joueurDto.setNom("Kasper Schmeichel");
        joueurDto.setPosition("Gardien");
        joueurDto.setEquipeId(1L);
        joueurDto.setEquipeNom("OGC Nice");

        createRequest = new CreateJoueurRequest();
        createRequest.setNom("Test Player");
        createRequest.setPosition("Milieu");
        createRequest.setEquipeId(1L);
    }

    @Test
    void testGetAllJoueurs_Success() throws Exception {
        // Given
        JoueurDto joueurDto2 = new JoueurDto();
        joueurDto2.setId(2L);
        joueurDto2.setNom("Terem Moffi");
        joueurDto2.setPosition("Attaquant");
        joueurDto2.setEquipeId(1L);

        List<JoueurDto> joueurs = Arrays.asList(joueurDto, joueurDto2);
        Page<JoueurDto> page = new PageImpl<>(joueurs, PageRequest.of(0, 10), 2);

        when(joueurService.getAllJoueurs(0, 10, "nom", "asc", null, null))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/joueurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].nom").value("Kasper Schmeichel"))
                .andExpect(jsonPath("$.content[1].nom").value("Terem Moffi"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testGetAllJoueurs_WithPagination() throws Exception {
        // Given
        List<JoueurDto> joueurs = Arrays.asList(joueurDto);
        Page<JoueurDto> page = new PageImpl<>(joueurs, PageRequest.of(0, 1), 10);

        when(joueurService.getAllJoueurs(0, 1, "nom", "asc", null, null))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/joueurs")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(10));
    }

    @Test
    void testGetAllJoueurs_WithEquipeFilter() throws Exception {
        // Given
        List<JoueurDto> joueurs = Arrays.asList(joueurDto);
        Page<JoueurDto> page = new PageImpl<>(joueurs, PageRequest.of(0, 10), 1);

        when(joueurService.getAllJoueurs(0, 10, "nom", "asc", 1L, null))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/joueurs")
                        .param("equipeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].equipeId").value(1));
    }

    @Test
    void testGetAllJoueurs_WithPositionFilter() throws Exception {
        // Given
        List<JoueurDto> joueurs = Arrays.asList(joueurDto);
        Page<JoueurDto> page = new PageImpl<>(joueurs, PageRequest.of(0, 10), 1);

        when(joueurService.getAllJoueurs(0, 10, "nom", "asc", null, "Gardien"))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/joueurs")
                        .param("position", "Gardien"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].position").value("Gardien"));
    }

    @Test
    void testCreateJoueur_Success() throws Exception {
        // Given
        when(joueurService.createJoueur(any(CreateJoueurRequest.class))).thenReturn(joueurDto);

        // When & Then
        mockMvc.perform(post("/api/joueurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Kasper Schmeichel"))
                .andExpect(jsonPath("$.position").value("Gardien"))
                .andExpect(jsonPath("$.equipeId").value(1));

        verify(joueurService, times(1)).createJoueur(any(CreateJoueurRequest.class));
    }

    @Test
    void testCreateJoueur_ValidationError() throws Exception {
        // Given - Données invalides
        createRequest.setNom(null);
        createRequest.setPosition("");

        // When & Then
        mockMvc.perform(post("/api/joueurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetJoueurById_Success() throws Exception {
        // Given
        when(joueurService.getJoueurById(1L)).thenReturn(Optional.of(joueurDto));

        // When & Then
        mockMvc.perform(get("/api/joueurs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Kasper Schmeichel"))
                .andExpect(jsonPath("$.position").value("Gardien"));

        verify(joueurService, times(1)).getJoueurById(1L);
    }

    @Test
    void testGetJoueurById_NotFound() throws Exception {
        // Given
        when(joueurService.getJoueurById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/joueurs/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteJoueur_Success() throws Exception {
        // Given
        when(joueurService.deleteJoueur(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/joueurs/1"))
                .andExpect(status().isNoContent());

        verify(joueurService, times(1)).deleteJoueur(1L);
    }

    @Test
    void testDeleteJoueur_NotFound() throws Exception {
        // Given
        when(joueurService.deleteJoueur(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/joueurs/999"))
                .andExpect(status().isNotFound());
    }
}

