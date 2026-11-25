CREATE DATABASE  IF NOT EXISTS `silocontrol_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `silocontrol_db`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: silocontrol_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `silos`
--

DROP TABLE IF EXISTS `silos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `silos` (
  `id` varchar(255) NOT NULL,
  `capacity_ton` int NOT NULL,
  `current_ton` double NOT NULL,
  `farm` varchar(255) DEFAULT NULL,
  `grain` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `location_address` varchar(255) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `updated_at` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `silos`
--

LOCK TABLES `silos` WRITE;
/*!40000 ALTER TABLE `silos` DISABLE KEYS */;
INSERT INTO `silos` VALUES ('c4a19618-9555-4294-8b63-8e3d082a734f',100,55,'maiz','grande',NULL,'',NULL,'javi silos 2','23/11/2026 16:36'),('eb8d6816-a874-4dba-8855-08446d039130',100,10,'k','k',NULL,'',NULL,'si','23/11/2026 04:31'),('S1',320,240,'Granja Las Palmas','Maíz',NULL,'',NULL,'Silo 1','10/10/2026 14:32'),('S2',320,144,'Granja Las Palmas','Trigo',NULL,'',NULL,'Silo 2','10/10/2026 14:32'),('S3',320,64,'Granja El Roble','Soja',NULL,'',NULL,'Silo 3','10/10/2026 14:30'),('S4',320,288,'Granja El Roble','Avena',NULL,'',NULL,'Silo 4','10/10/2026 14:28'),('S5',320,200,'Granja El Roble','Cebada',NULL,'',NULL,'Silo 5','10/10/2026 14:25');
/*!40000 ALTER TABLE `silos` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-25 18:00:05
