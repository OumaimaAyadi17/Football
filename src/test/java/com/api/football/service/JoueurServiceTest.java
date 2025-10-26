package com.api.football.service;

import com.api.football.dto.CreateJoueurRequest;
import com.api.football.dto.JoueurDto;
import com.api.football.model.Equipe;
import com.api.football.model.Joueur;
import com.api.football.repository.EquipeRepository;
import com.api.football.repository.JoueurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le service JoueurService.
 * 
 * @author Nice Football API Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class JoueurServiceTest {

    @Mock
    private JoueurRepository joueurRepository;

    @Mock
    private EquipeRepository equipeRepository;

    @InjectMocks
    private JoueurService joueurService;

    private Joueur joueurTest;
    private Equipe equipeTest;
    private CreateJoueurRequest createRequest;

    @BeforeEach
    void setUp() {
        equipeTest = new Equipe("OGC Nice", "OGC", new BigDecimal("50000000.00"));
        equipeTest.setId(1L);

        joueurTest = new Joueur("Kasper Schmeichel", "Gardien", equipeTest);
        joueurTest.setId(1L);

        createRequest = new CreateJoueurRequest();
        createRequest.setNom("Kasper Schmeichel");
        createRequest.setPosition("Gardien");
        createRequest.setEquipeId(1L);
    }

    @Test
    void testGetAllJoueurs_Success() {
        // Given
        List<Joueur> joueurs = Arrays.asList(joueurTest);
        Page<Joueur> pageJoueurs = new PageImpl<>(joueurs);
        
        when(joueurRepository.findAll(any(Pageable.class))).thenReturn(pageJoueurs);

        // When
        Page<JoueurDto> result = joueurService.getAllJoueurs(0, 10, "nom", "asc", null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Kasper Schmeichel", result.getContent().get(0).getNom());
        assertEquals("Gardien", result.getContent().get(0).getPosition());
        
        verify(joueurRepository).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllJoueurs_WithEquipeFilter() {
        // Given
        List<Joueur> joueurs = Arrays.asList(joueurTest);
        Page<Joueur> pageJoueurs = new PageImpl<>(joueurs);
        
        when(joueurRepository.findByEquipeId(1L, any(Pageable.class))).thenReturn(pageJoueurs);

        // When
        Page<JoueurDto> result = joueurService.getAllJoueurs(0, 10, "nom", "asc", 1L, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        verify(joueurRepository).findByEquipeId(1L, any(Pageable.class));
    }

    @Test
    void testCreateJoueur_Success() {
        // Given
        when(joueurRepository.existsByNom("Kasper Schmeichel")).thenReturn(false);
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipeTest));
        when(joueurRepository.save(any(Joueur.class))).thenReturn(joueurTest);

        // When
        JoueurDto result = joueurService.createJoueur(createRequest);

        // Then
        assertNotNull(result);
        assertEquals("Kasper Schmeichel", result.getNom());
        assertEquals("Gardien", result.getPosition());
        assertEquals(1L, result.getEquipeId());
        assertEquals("OGC Nice", result.getEquipeNom());
        
        verify(joueurRepository).existsByNom("Kasper Schmeichel");
        verify(equipeRepository).findById(1L);
        verify(joueurRepository).save(any(Joueur.class));
    }

    @Test
    void testCreateJoueur_WithExistingNom() {
        // Given
        when(joueurRepository.existsByNom("Kasper Schmeichel")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> joueurService.createJoueur(createRequest)
        );
        
        assertEquals("Un joueur avec le nom 'Kasper Schmeichel' existe déjà", exception.getMessage());
        
        verify(joueurRepository).existsByNom("Kasper Schmeichel");
        verify(joueurRepository, never()).save(any(Joueur.class));
    }

    @Test
    void testCreateJoueur_WithNonExistentEquipe() {
        // Given
        when(joueurRepository.existsByNom("Kasper Schmeichel")).thenReturn(false);
        when(equipeRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> joueurService.createJoueur(createRequest)
        );
        
        assertEquals("Équipe avec l'ID 1 non trouvée", exception.getMessage());
        
        verify(joueurRepository).existsByNom("Kasper Schmeichel");
        verify(equipeRepository).findById(1L);
        verify(joueurRepository, never()).save(any(Joueur.class));
    }

    @Test
    void testGetJoueurById_Success() {
        // Given
        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueurTest));

        // When
        Optional<JoueurDto> result = joueurService.getJoueurById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Kasper Schmeichel", result.get().getNom());
        assertEquals("Gardien", result.get().getPosition());
        
        verify(joueurRepository).findById(1L);
    }

    @Test
    void testGetJoueurById_NotFound() {
        // Given
        when(joueurRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<JoueurDto> result = joueurService.getJoueurById(1L);

        // Then
        assertFalse(result.isPresent());
        
        verify(joueurRepository).findById(1L);
    }

    @Test
    void testTransferJoueur_Success() {
        // Given
        Equipe nouvelleEquipe = new Equipe("PSG", "PSG", new BigDecimal("200000000.00"));
        nouvelleEquipe.setId(2L);
        
        when(joueurRepository.findById(1L)).thenReturn(Optional.of(joueurTest));
        when(equipeRepository.findById(2L)).thenReturn(Optional.of(nouvelleEquipe));
        when(joueurRepository.save(any(Joueur.class))).thenReturn(joueurTest);

        // When
        JoueurDto result = joueurService.transferJoueur(1L, 2L);

        // Then
        assertNotNull(result);
        assertEquals("Kasper Schmeichel", result.getNom());
        
        verify(joueurRepository).findById(1L);
        verify(equipeRepository).findById(2L);
        verify(joueurRepository).save(any(Joueur.class));
    }

    @Test
    void testTransferJoueur_JoueurNotFound() {
        // Given
        when(joueurRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> joueurService.transferJoueur(1L, 2L)
        );
        
        assertEquals("Joueur avec l'ID 1 non trouvé", exception.getMessage());
        
        verify(joueurRepository).findById(1L);
        verify(equipeRepository, never()).findById(anyLong());
    }

    @Test
    void testDeleteJoueur_Success() {
        // Given
        when(joueurRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = joueurService.deleteJoueur(1L);

        // Then
        assertTrue(result);
        verify(joueurRepository).existsById(1L);
        verify(joueurRepository).deleteById(1L);
    }

    @Test
    void testDeleteJoueur_NotFound() {
        // Given
        when(joueurRepository.existsById(1L)).thenReturn(false);

        // When
        boolean result = joueurService.deleteJoueur(1L);

        // Then
        assertFalse(result);
        verify(joueurRepository).existsById(1L);
        verify(joueurRepository, never()).deleteById(anyLong());
    }
}
