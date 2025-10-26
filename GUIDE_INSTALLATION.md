# Guide d'installation - Nice Football API

## 🎯 Objectif

Ce guide vous permettra d'installer et de tester l'API Nice Football en quelques minutes.

## 📋 Prérequis

### Logiciels requis

1. **Java 17** ou supérieur
   - Vérifier : `java -version`
   - Télécharger : https://adoptium.net/

2. **Maven 3.1** ou supérieur
   - Vérifier : `mvn -version`
   - Télécharger : https://maven.apache.org/download.cgi

3. **Git** (optionnel)
   - Télécharger : https://git-scm.com/

## 🚀 Installation rapide

### Étape 1 : Vérifier l'environnement

# Vérifier Maven
mvn -version
# Doit afficher : Apache Maven 3.1.x

### Étape 2 : Télécharger le projet
git clone <url-du-repo>

### Étape 3 : Compiler le projet

```bash
mvn clean compile
```

**Résultat attendu :** `BUILD SUCCESS`

### Étape 4 : Lancer les tests

```bash
mvn test
```

**Résultat attendu :** Tous les tests passent (Tests run: X, Failures: 0, Errors: 0)

### Étape 5 : Démarrer l'application

```bash
mvn spring-boot:run
```

**Résultat attendu :**
```
Started FootballApplication in X.XXX seconds (JVM running for X.XXX)
```

## ✅ Vérification de l'installation

### 1. Vérifier que l'API fonctionne

Ouvrez votre navigateur et allez sur :
- **Page d'accueil** : http://localhost:8081
- **Documentation API** : http://localhost:8081/swagger-ui.html

### 3. Vérifier la base de données

1. Allez sur http://localhost:8081/h2-console
2. Connectez-vous avec :
   - **JDBC URL** : `jdbc:h2:mem:testdb`
   - **Username** : `sa`
   - **Password** : `sa`
3. Cliquez sur "Connect"
4. Explorez les tables `EQUIPES` et `JOUEURS`

