package com.api.football.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO pour la création d'une équipe.
 *
 * @author  Football API Team
 * @version 1.0.0
 */
public class CreateEquipeRequest {

    @NotBlank(message = "Le nom de l'équipe est obligatoire")
    @Size(max = 100, message = "Le nom de l'équipe ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "L'acronyme est obligatoire")
    @Size(max = 10, message = "L'acronyme ne peut pas dépasser 10 caractères")
    private String acronyme;

    @NotNull(message = "Le budget est obligatoire")
    @PositiveOrZero(message = "Le budget doit être positif ou nul")
    private BigDecimal budget;

    @Valid
    private List<CreateJoueurRequest> joueurs;

    /**
     * Constructeur par défaut.
     */
    public CreateEquipeRequest() {
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param nom le nom de l'équipe
     * @param acronyme l'acronyme de l'équipe
     * @param budget le budget de l'équipe
     * @param joueurs la liste des joueurs à créer
     */
    public CreateEquipeRequest(String nom, String acronyme, BigDecimal budget, List<CreateJoueurRequest> joueurs) {
        this.nom = nom;
        this.acronyme = acronyme;
        this.budget = budget;
        this.joueurs = joueurs;
    }

    // Getters et Setters

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

    public List<CreateJoueurRequest> getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(List<CreateJoueurRequest> joueurs) {
        this.joueurs = joueurs;
    }

    @Override
    public String toString() {
        return "CreateEquipeRequest{" +
                "nom='" + nom + '\'' +
                ", acronyme='" + acronyme + '\'' +
                ", budget=" + budget +
                ", nombreJoueurs=" + (joueurs != null ? joueurs.size() : 0) +
                '}';
    }
}

