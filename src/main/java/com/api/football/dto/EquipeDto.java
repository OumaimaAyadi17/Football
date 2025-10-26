package com.api.football.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO pour représenter une équipe dans les réponses API.
 *
 * @author API Football API Team
 * @version 1.0.0
 */
public class EquipeDto {

    private Long id;

    @NotBlank(message = "Le nom de l'équipe est obligatoire")
    @Size(max = 100, message = "Le nom de l'équipe ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "L'acronyme est obligatoire")
    @Size(max = 10, message = "L'acronyme ne peut pas dépasser 10 caractères")
    private String acronyme;

    @NotNull(message = "Le budget est obligatoire")
    @PositiveOrZero(message = "Le budget doit être positif ou nul")
    private BigDecimal budget;

    private List<JoueurDto> joueurs;

    /**
     * Constructeur par défaut.
     */
    public EquipeDto() {
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param id l'identifiant de l'équipe
     * @param nom le nom de l'équipe
     * @param acronyme l'acronyme de l'équipe
     * @param budget le budget de l'équipe
     * @param joueurs la liste des joueurs
     */
    public EquipeDto(Long id, String nom, String acronyme, BigDecimal budget, List<JoueurDto> joueurs) {
        this.id = id;
        this.nom = nom;
        this.acronyme = acronyme;
        this.budget = budget;
        this.joueurs = joueurs;
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAcronyme() {
        return acronyme;
    }

    public void setAcronyme(String acronyme) {
        this.acronyme = acronyme;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public List<JoueurDto> getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(List<JoueurDto> joueurs) {
        this.joueurs = joueurs;
    }

    @Override
    public String toString() {
        return "EquipeDto{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", acronyme='" + acronyme + '\'' +
                ", budget=" + budget +
                ", nombreJoueurs=" + (joueurs != null ? joueurs.size() : 0) +
                '}';
    }
}

