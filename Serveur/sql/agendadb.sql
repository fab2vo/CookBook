-- Agenda des repas (voir SPEC_AGENDA.md) : à exécuter une fois dans phpMyAdmin (base cookbook)
CREATE TABLE IF NOT EXISTS `agendadb` (
  `pknum`       INT(11) NOT NULL AUTO_INCREMENT,
  `id_entry`    VARCHAR(45) NOT NULL,             -- UUID généré par l'appli
  `family`      VARCHAR(255) NOT NULL,            -- recopié de userdb.family par le serveur
  `id_user`     VARCHAR(45) NOT NULL,             -- auteur de l'entrée
  `scope`       ENUM('FAMILY','PERSO') NOT NULL,
  `date_meal`   DATE NOT NULL,
  `meal`        ENUM('LUNCH','DINNER') NOT NULL,  -- l'ordre de l'ENUM donne l'ordre d'affichage
  `id_recipe`   VARCHAR(45) DEFAULT NULL,         -- NULL = texte libre
  `label`       VARCHAR(255) NOT NULL,            -- texte libre ou copie du titre de la recette
  `date_update` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pknum`),
  UNIQUE KEY `uk_entry` (`id_entry`),
  KEY `ix_family` (`family`, `scope`, `date_meal`),
  KEY `ix_user` (`id_user`, `scope`, `date_meal`),
  KEY `ix_recipe` (`id_recipe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
