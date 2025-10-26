package com.api.football.service;

import com.api.football.dto.CreateEquipeRequest;
import com.api.football.dto.EquipeDto;
import com.api.football.model.Equipe;
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
 * Tests unitaires pour le service EquipeService.
 * 
 * @author Nice Football API Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class EquipeServiceTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private JoueurRepository joueurRepository;

    @InjectMocks
    private EquipeService equipeService;

    private Equipe equipeTest;
    private CreateEquipeRequest createRequest;

    @BeforeEach
    void setUp() {
        equipeTest = new Equipe("OGC Nice", "OGC", new BigDecimal("50000000.00"));
        equipeTest.setId(1L);

        createRequest = new CreateEquipeRequest();
        createRequest.setNom("OGC Nice");
        createRequest.setAcronyme("OGC");
        createRequest.setBudget(new BigDecimal("50000000.00"));
    }

    @Test
    void testGetAllEquipes_Success() {
        // Given
        List<Equipe> equipes = Arrays.asList(equipeTest);
        Page<Equipe> pageEquipes = new PageImpl<>(equipes);
        
        when(equipeRepository.findAllWithJoueurs(any(Pageable.class))).thenReturn(pageEquipes);

        // When
        Page<EquipeDto> result = equipeService.getAllEquipes(0, 10, "nom", "asc");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("OGC Nice", result.getContent().get(0).getNom());
        assertEquals("OGC", result.getContent().get(0).getAcronyme());
        
        verify(equipeRepository).findAllWithJoueurs(any(Pageable.class));
    }

    @Test
    void testGetAllEquipes_WithInvalidSortField() {
        // Given
        List<Equipe> equipes = Arrays.asList(equipeTest);
        Page<Equipe> pageEquipes = new PageImpl<>(equipes);
        
        when(equipeRepository.findAllWithJoueurs(any(Pageable.class))).thenReturn(pageEquipes);

        // When
        Page<EquipeDto> result = equipeService.getAllEquipes(0, 10, "invalidField", "asc");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        verify(equipeRepository).findAllWithJoueurs(any(Pageable.class));
    }

    @Test
    void testCreateEquipe_Success() {
        // Given
        when(equipeRepository.existsByAcronyme("OGC")).thenReturn(false);
        when(equipeRepository.existsByNom("OGC Nice")).thenReturn(false);
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipeTest);

        // When
        EquipeDto result = equipeService.createEquipe(createRequest);

        // Then
        assertNotNull(result);
        assertEquals("OGC Nice", result.getNom());
        assertEquals("OGC", result.getAcronyme());
        assertEquals(new BigDecimal("50000000.00"), result.getBudget());
        
        verify(equipeRepository).existsByAcronyme("OGC");
        verify(equipeRepository).existsByNom("OGC Nice");
        verify(equipeRepository).save(any(Equipe.class));
    }

    @Test
    void testCreateEquipe_WithExistingAcronyme() {
        // Given
        when(equipeRepository.existsByAcronyme("OGC")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> equipeService.createEquipe(createRequest)
        );
        
        assertEquals("Une équipe avec l'acronyme 'OGC' existe déjà", exception.getMessage());
        
        verify(equipeRepository).existsByAcronyme("OGC");
        verify(equipeRepository, never()).save(any(Equipe.class));
    }

    @Test
    void testCreateEquipe_WithExistingNom() {
        // Given
        when(equipeRepository.existsByAcronyme("OGC")).thenReturn(false);
        when(equipeRepository.existsByNom("OGC Nice")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> equipeService.createEquipe(createRequest)
        );
        
        assertEquals("Une équipe avec le nom 'OGC Nice' existe déjà", exception.getMessage());
        
        verify(equipeRepository).existsByAcronyme("OGC");
        verify(equipeRepository).existsByNom("OGC Nice");
        verify(equipeRepository, never()).save(any(Equipe.class));
    }

    @Test
    void testGetEquipeById_Success() {
        // Given
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipeTest));

        // When
        Optional<EquipeDto> result = equipeService.getEquipeById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("OGC Nice", result.get().getNom());
        assertEquals("OGC", result.get().getAcronyme());
        
        verify(equipeRepository).findById(1L);
    }

    @Test
    void testGetEquipeById_NotFound() {
        // Given
        when(equipeRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<EquipeDto> result = equipeService.getEquipeById(1L);

        // Then
        assertFalse(result.isPresent());
        
        verify(equipeRepository).findById(1L);
    }

    @Test
    void testGetEquipeByAcronyme_Success() {
        // Given
        when(equipeRepository.findByAcronyme("OGC")).thenReturn(Optional.of(equipeTest));

        // When
        Optional<EquipeDto> result = equipeService.getEquipeByAcronyme("OGC");

        // Then
        assertTrue(result.isPresent());
        assertEquals("OGC Nice", result.get().getNom());
        assertEquals("OGC", result.get().getAcronyme());
        
        verify(equipeRepository).findByAcronyme("OGC");
    }

    @Test
    void testGetEquipeByAcronyme_NotFound() {
        // Given
        when(equipeRepository.findByAcronyme("OGC")).thenReturn(Optional.empty());

        // When
        Optional<EquipeDto> result = equipeService.getEquipeByAcronyme("OGC");

        // Then
        assertFalse(result.isPresent());
        
        verify(equipeRepository).findByAcronyme("OGC");
    }
}
