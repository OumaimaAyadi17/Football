package com.api.football.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour représenter un joueur dans les réponses API.
 *
 * @author Football API Team
 * @version 1.0.0
 */
public class JoueurDto {

    private Long id;

    @NotBlank(message = "Le nom du joueur est obligatoire")
    @Size(max = 100, message = "Le nom du joueur ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "La position est obligatoire")
    @Size(max = 50, message = "La position ne peut pas dépasser 50 caractères")
    private String position;

    private Long equipeId;
    private String equipeNom;

    /**
     * Constructeur par défaut.
     */
    public JoueurDto() {
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param id l'identifiant du joueur
     * @param nom le nom du joueur
     * @param position la position du joueur
     * @param equipeId l'identifiant de l'équipe
     * @param equipeNom le nom de l'équipe
     */
    public JoueurDto(Long id, String nom, String position, Long equipeId, String equipeNom) {
        this.id = id;
        this.nom = nom;
        this.position = position;
        this.equipeId = equipeId;
        this.equipeNom = equipeNom;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(Long equipeId) {
        this.equipeId = equipeId;
    }

    public String getEquipeNom() {
        return equipeNom;
    }

    public void setEquipeNom(String equipeNom) {
        this.equipeNom = equipeNom;
    }

    @Override
    public String toString() {
        return "JoueurDto{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", position='" + position + '\'' +
                ", equipeId=" + equipeId +
                ", equipeNom='" + equipeNom + '\'' +
                '}';
    }
}

