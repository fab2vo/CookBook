-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost:3306
-- Généré le : dim. 31 mars 2024 à 17:21
-- Version du serveur : 10.5.20-MariaDB
-- Version de PHP : 7.3.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `id18724387_cookbook`
--

-- --------------------------------------------------------

--
-- Structure de la table `userdb`
--

CREATE TABLE `userdb` (
  `pknum` int(11) NOT NULL,
  `id_user` char(45) NOT NULL,
  `name` varchar(255) NOT NULL,
  `family` varchar(255) NOT NULL,
  `last_sync` datetime NOT NULL,
  `pass` varchar(45) NOT NULL DEFAULT 'cookbook'
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Déchargement des données de la table `userdb`
--

INSERT INTO `userdb` (`pknum`, `id_user`, `name`, `family`, `last_sync`, `pass`) VALUES
(42, '0ddab906-93e9-4d45-8c7d-4407e4f5f27d', 'Carole', 'Sayler', '2021-10-28 23:53:45', 'pilote1'),
(45, '1d19a9b2-5c36-4e74-8b3f-d0aa8b3e398c', 'Marie', 'Agius2022', '2021-12-20 18:13:42', 'Sawadee'),
(12, '252b2d53-af35-4e63-8ebd-f172d5436435', 'Yves', 'Hoet de Liege', '2021-09-11 10:25:54', 'cookbook'),
(47, '43ea25c3-8b16-41db-90ee-924b2790c1eb', 'Maman', 'Bazot Tribe', '2022-05-14 16:22:06', 'Ciboulette06!'),
(43, '89ead477-9a29-4017-80a9-afb94af4c2fe', 'Fab', 'MasterCook', '2021-11-20 12:30:49', 'Book'),
(50, '8d6c5ba7-acea-4f69-92b3-a2233ec07874', '15666666666', '15666666666', '2023-08-19 17:44:12', '80996240'),
(41, '8e1846bc-ac74-4f87-802f-bbbed5f11600', 'maud', 'sabiani', '2021-10-28 14:28:39', '21Spyke!'),
(44, '95297437-546b-45c5-943c-4806eecc0c2f', 'Vero', 'MasterCook', '2021-12-12 17:31:19', 'Book'),
(46, '9a8637b8-ef01-4a1e-834d-61b9d447f2cd', 'All', 'Co2-Sustainability', '2021-12-20 18:15:49', 'OneTech'),
(17, 'ae03f73a-cacc-4818-af54-216d70b281f8', 'Walter', 'Hoet de Liège', '2021-10-22 18:16:43', 'cookbook'),
(1, 'b9ca15d6-5b36-4561-8339-f5496e286a39', 'Antoinette', 'De Cursac de Neuilly', '2021-10-25 17:58:40', 'cookbook'),
(3, 'c81d4e2e-bcf2-11e6-869b-7df92533d2db', 'Fabrice', 'Devaux_Lion de ML', '2021-05-27 10:27:13', 'Lion'),
(4, 'c81d4e2e-bcf2-11e6-869b-8df92533d2db', 'Antoine', 'De Cursac de Neuilly', '2021-05-18 08:00:36', 'cookbook'),
(5, 'c81d4e2e-bcf2-11e7-869b-7df92533d2db', 'Lucile', 'Devaux_Lion de ML', '2021-05-27 10:16:37', 'Lion'),
(6, 'c81d4e2e-bcf3-11e6-869b-7df92533d2db', 'Véronique', 'Devaux_Lion de ML', '2021-05-28 07:00:00', 'Lion'),
(9, 'fce5a6c7-2dc3-4f4d-989f-69b308b6966a', 'Leandre', 'Devaux_Lion de ML', '2021-09-01 22:13:07', 'Lion');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `userdb`
--
ALTER TABLE `userdb`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `pknum` (`pknum`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `userdb`
--
ALTER TABLE `userdb`
  MODIFY `pknum` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
