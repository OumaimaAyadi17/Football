package com.api.football.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.football.dto.CreateEquipeRequest;
import com.api.football.model.Equipe;
import com.api.football.model.Joueur;
import com.api.football.repository.EquipeRepository;
import com.api.football.repository.JoueurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration complets pour l'API Nice Football.
 * 
 * Ces tests vérifient le fonctionnement end-to-end de l'API
 * en utilisant une base de données de test.
 * 
 * @author Nice Football API Team
 * @version 1.0.0
 */
@WebMvcTest
@ActiveProfiles("test")
class EquipeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private JoueurRepository joueurRepository;

    @BeforeEach
    void setUp() {
        // Nettoyer la base de données avant chaque test
        joueurRepository.deleteAll();
        equipeRepository.deleteAll();
    }

    @Test
    void testCreateEquipeWithJoueurs_Integration() throws Exception {
        // Given
        CreateEquipeRequest request = new CreateEquipeRequest();
        request.setNom("OGC Nice");
        request.setAcronyme("OGC");
        request.setBudget(new BigDecimal("50000000.00"));
        request.setJoueurs(Arrays.asList(
                new com.api.football.dto.CreateJoueurRequest("Kasper Schmeichel", "Gardien"),
                new com.api.football.dto.CreateJoueurRequest("Terem Moffi", "Attaquant")
        ));

        // When & Then
        mockMvc.perform(post("/api/equipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("OGC Nice"))
                .andExpect(jsonPath("$.acronyme").value("OGC"))
                .andExpect(jsonPath("$.budget").value(50000000.00))
                .andExpect(jsonPath("$.joueurs").isArray())
                .andExpect(jsonPath("$.joueurs.length()").value(2))
                .andExpect(jsonPath("$.joueurs[0].nom").value("Kasper Schmeichel"))
                .andExpect(jsonPath("$.joueurs[0].position").value("Gardien"))
                .andExpect(jsonPath("$.joueurs[1].nom").value("Terem Moffi"))
                .andExpect(jsonPath("$.joueurs[1].position").value("Attaquant"));

        // Vérifier en base de données
        Equipe equipe = equipeRepository.findByAcronyme("OGC").orElse(null);
        assert equipe != null;
        assert equipe.getNom().equals("OGC Nice");
        assert equipe.getJoueurs().size() == 2;
    }

    @Test
    void testCreateEquipeWithoutJoueurs_Integration() throws Exception {
        // Given
        CreateEquipeRequest request = new CreateEquipeRequest();
        request.setNom("AS Monaco");
        request.setAcronyme("ASM");
        request.setBudget(new BigDecimal("120000000.00"));

        // When & Then
        mockMvc.perform(post("/api/equipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("AS Monaco"))
                .andExpect(jsonPath("$.acronyme").value("ASM"))
                .andExpect(jsonPath("$.budget").value(120000000.00))
                .andExpect(jsonPath("$.joueurs").isArray())
                .andExpect(jsonPath("$.joueurs.length()").value(0));

        // Vérifier en base de données
        Equipe equipe = equipeRepository.findByAcronyme("ASM").orElse(null);
        assert equipe != null;
        assert equipe.getNom().equals("AS Monaco");
        assert equipe.getJoueurs().isEmpty();
    }

    @Test
    void testGetAllEquipesWithPagination_Integration() throws Exception {
        // Given - Créer des équipes de test
        Equipe equipe1 = new Equipe("OGC Nice", "OGC", new BigDecimal("50000000.00"));
        Equipe equipe2 = new Equipe("AS Monaco", "ASM", new BigDecimal("120000000.00"));
        Equipe equipe3 = new Equipe("PSG", "PSG", new BigDecimal("200000000.00"));
        
        equipeRepository.save(equipe1);
        equipeRepository.save(equipe2);
        equipeRepository.save(equipe3);

        // When & Then - Test pagination
        mockMvc.perform(get("/api/equipes")
                .param("page", "0")
                .param("size", "2")
                .param("sortBy", "budget")
                .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content[0].acronyme").value("PSG"))
                .andExpect(jsonPath("$.content[1].acronyme").value("ASM"));
    }

    @Test
    void testGetEquipeById_Integration() throws Exception {
        // Given
        Equipe equipe = new Equipe("OGC Nice", "OGC", new BigDecimal("50000000.00"));
        equipe = equipeRepository.save(equipe);
        
        Joueur joueur = new Joueur("Kasper Schmeichel", "Gardien", equipe);
        joueurRepository.save(joueur);

        // When & Then
        mockMvc.perform(get("/api/equipes/" + equipe.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("OGC Nice"))
                .andExpect(jsonPath("$.acronyme").value("OGC"))
                .andExpect(jsonPath("$.budget").value(50000000.00))
                .andExpect(jsonPath("$.joueurs").isArray())
                .andExpect(jsonPath("$.joueurs.length()").value(1))
                .andExpect(jsonPath("$.joueurs[0].nom").value("Kasper Schmeichel"));
    }

    @Test
    void testGetEquipeByAcronyme_Integration() throws Exception {
        // Given
        Equipe equipe = new Equipe("OGC Nice", "OGC", new BigDecimal("50000000.00"));
        equipeRepository.save(equipe);

        // When & Then
        mockMvc.perform(get("/api/equipes/acronyme/OGC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("OGC Nice"))
                .andExpect(jsonPath("$.acronyme").value("OGC"));
    }

    @Test
    void testCreateEquipeWithDuplicateAcronyme_Integration() throws Exception {
        // Given - Créer une équipe existante
        Equipe existingEquipe = new Equipe("OGC Nice", "OGC", new BigDecimal("50000000.00"));
        equipeRepository.save(existingEquipe);

        CreateEquipeRequest request = new CreateEquipeRequest();
        request.setNom("Another Team");
        request.setAcronyme("OGC"); // Même acronyme
        request.setBudget(new BigDecimal("10000000.00"));

        // When & Then
        mockMvc.perform(post("/api/equipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Erreur de validation"))
                .andExpect(jsonPath("$.message").value("Une équipe avec l'acronyme 'OGC' existe déjà"));
    }

    @Test
    void testValidationErrors_Integration() throws Exception {
        // Given - Données invalides
        CreateEquipeRequest request = new CreateEquipeRequest();
        // Champs manquants

        // When & Then
        mockMvc.perform(post("/api/equipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erreur de validation"))
                .andExpect(jsonPath("$.details").isMap());
    }

    @Test
    void testGetEquipeNotFound_Integration() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/equipes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetEquipeByAcronymeNotFound_Integration() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/equipes/acronyme/UNKNOWN"))
                .andExpect(status().isNotFound());
    }
}
