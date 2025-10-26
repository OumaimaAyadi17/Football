-- Données initiales pour l'API Nice Football
-- Ces données sont chargées automatiquement au démarrage de l'application

-- Insertion de l'équipe de Nice
INSERT INTO equipes (nom, acronyme, budget) VALUES 
('Olympique Gymnaste Club Nice Côte d''Azur', 'OGC', 50000000.00);

-- Insertion des joueurs de Nice
INSERT INTO joueurs (nom, position, equipe_id) VALUES 
('Kasper Schmeichel', 'Gardien', 1),
('Marcin Bulka', 'Gardien', 1),
('Jordan Lotomba', 'Défenseur', 1),
('Jean-Clair Todibo', 'Défenseur', 1),
('Dante', 'Défenseur', 1),
('Melvin Bard', 'Défenseur', 1),
('Youcef Atal', 'Défenseur', 1),
('Pablo Rosario', 'Milieu', 1),
('Khephren Thuram', 'Milieu', 1),
('Hicham Boudaoui', 'Milieu', 1),
('Morgan Sanson', 'Milieu', 1),
('Terem Moffi', 'Attaquant', 1),
('Gaëtan Laborde', 'Attaquant', 1),
('Evann Guessand', 'Attaquant', 1),
('Alexis Claude-Maurice', 'Attaquant', 1);

-- Insertion d'autres équipes de Ligue 1 pour les tests
INSERT INTO equipes (nom, acronyme, budget) VALUES 
('Paris Saint-Germain', 'PSG', 200000000.00),
('Olympique de Marseille', 'OM', 80000000.00),
('AS Monaco', 'ASM', 120000000.00),
('Olympique Lyonnais', 'OL', 60000000.00);

-- Insertion de quelques joueurs pour les autres équipes
INSERT INTO joueurs (nom, position, equipe_id) VALUES 
('Gianluigi Donnarumma', 'Gardien', 2),
('Kylian Mbappé', 'Attaquant', 2),
('Lionel Messi', 'Attaquant', 2),
('Neymar Jr', 'Attaquant', 2),
('Pau Lopez', 'Gardien', 3),
('Alexis Sánchez', 'Attaquant', 3),
('Alexandre Lacazette', 'Attaquant', 3),
('Alexander Nübel', 'Gardien', 4),
('Wissam Ben Yedder', 'Attaquant', 4),
('Anthony Lopes', 'Gardien', 5),
('Alexandre Lacazette', 'Attaquant', 5);
