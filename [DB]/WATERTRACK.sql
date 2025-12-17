-- CRIAR DATABASE 'watertrack'
-- E COLOCAR ISTO LA DENTRO
--
--
--
-- SEM SCHEMA
--
--
--
--
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: watertrack
-- ------------------------------------------------------
-- Server version	9.1.0

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
-- Table structure for table `auth_assignment`
--

DROP TABLE IF EXISTS `auth_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_assignment` (
  `item_name` varchar(64) COLLATE utf8mb3_unicode_ci NOT NULL,
  `user_id` varchar(64) COLLATE utf8mb3_unicode_ci NOT NULL,
  `created_at` int DEFAULT NULL,
  PRIMARY KEY (`item_name`,`user_id`),
  KEY `idx-auth_assignment-user_id` (`user_id`),
  CONSTRAINT `auth_assignment_ibfk_1` FOREIGN KEY (`item_name`) REFERENCES `auth_item` (`name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_assignment`
--

LOCK TABLES `auth_assignment` WRITE;
/*!40000 ALTER TABLE `auth_assignment` DISABLE KEYS */;
INSERT INTO `auth_assignment` VALUES ('admin','1',NULL),('resident','10',1763054566),('resident','11',1763485486),('resident','13',1763507717),('resident','14',1763733463),('resident','17',1764341588),('resident','18',1764341604),('technician','12',1763507683);
/*!40000 ALTER TABLE `auth_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_item`
--

DROP TABLE IF EXISTS `auth_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_item` (
  `name` varchar(64) COLLATE utf8mb3_unicode_ci NOT NULL,
  `type` smallint NOT NULL,
  `description` text COLLATE utf8mb3_unicode_ci,
  `rule_name` varchar(64) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `data` blob,
  `created_at` int DEFAULT NULL,
  `updated_at` int DEFAULT NULL,
  PRIMARY KEY (`name`),
  KEY `rule_name` (`rule_name`),
  KEY `idx-auth_item-type` (`type`),
  CONSTRAINT `auth_item_ibfk_1` FOREIGN KEY (`rule_name`) REFERENCES `auth_rule` (`name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_item`
--

LOCK TABLES `auth_item` WRITE;
/*!40000 ALTER TABLE `auth_item` DISABLE KEYS */;
INSERT INTO `auth_item` VALUES ('admin',1,NULL,NULL,NULL,1763054415,1763054415),('createEnterprise',2,'Cria Empresa',NULL,NULL,1763054415,1763054415),('createLogMeter',2,'Cria Log Contador',NULL,NULL,1763054415,1763054415),('createMeter',2,'Cria Contador',NULL,NULL,1763054414,1763054414),('createProblemMeter',2,'Cria Avaria Contador',NULL,NULL,1763054415,1763054415),('createUser',2,'Cria Utilizador',NULL,NULL,1763054415,1763054415),('deleteEnterprise',2,'Apaga Empresa',NULL,NULL,1763054415,1763054415),('deleteLogMeter',2,'Apaga Log Contador',NULL,NULL,1763054415,1763054415),('deleteMeter',2,'Apaga Contador',NULL,NULL,1763054415,1763054415),('deleteProblemMeter',2,'Apaga Avaria Contador',NULL,NULL,1763054415,1763054415),('deleteUser',2,'Apaga Utilizador',NULL,NULL,1763054415,1763054415),('resident',1,NULL,NULL,NULL,1763054415,1763054415),('technician',1,NULL,NULL,NULL,1763054415,1763054415),('updateEnterprise',2,'Edita Empresa',NULL,NULL,1763054415,1763054415),('updateLogMeter',2,'Edita Log Contador',NULL,NULL,1763054415,1763054415),('updateMeter',2,'Edita Contador',NULL,NULL,1763054415,1763054415),('updateProblemMeter',2,'Edita Avaria Contador',NULL,NULL,1763054415,1763054415),('updateUser',2,'Edita Utilizador',NULL,NULL,1763054415,1763054415);
/*!40000 ALTER TABLE `auth_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_item_child`
--

DROP TABLE IF EXISTS `auth_item_child`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_item_child` (
  `parent` varchar(64) COLLATE utf8mb3_unicode_ci NOT NULL,
  `child` varchar(64) COLLATE utf8mb3_unicode_ci NOT NULL,
  PRIMARY KEY (`parent`,`child`),
  KEY `child` (`child`),
  CONSTRAINT `auth_item_child_ibfk_1` FOREIGN KEY (`parent`) REFERENCES `auth_item` (`name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `auth_item_child_ibfk_2` FOREIGN KEY (`child`) REFERENCES `auth_item` (`name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_item_child`
--

LOCK TABLES `auth_item_child` WRITE;
/*!40000 ALTER TABLE `auth_item_child` DISABLE KEYS */;
INSERT INTO `auth_item_child` VALUES ('admin','createEnterprise'),('admin','createLogMeter'),('technician','createLogMeter'),('admin','createMeter'),('technician','createMeter'),('admin','createProblemMeter'),('resident','createProblemMeter'),('admin','createUser'),('admin','deleteEnterprise'),('admin','deleteLogMeter'),('technician','deleteLogMeter'),('admin','deleteMeter'),('admin','deleteProblemMeter'),('resident','deleteProblemMeter'),('technician','deleteProblemMeter'),('admin','deleteUser'),('admin','resident'),('admin','technician'),('admin','updateEnterprise'),('admin','updateLogMeter'),('technician','updateLogMeter'),('admin','updateMeter'),('resident','updateMeter'),('technician','updateMeter'),('admin','updateProblemMeter'),('resident','updateProblemMeter'),('technician','updateProblemMeter'),('admin','updateUser');
/*!40000 ALTER TABLE `auth_item_child` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_rule`
--

DROP TABLE IF EXISTS `auth_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_rule` (
  `name` varchar(64) COLLATE utf8mb3_unicode_ci NOT NULL,
  `data` blob,
  `created_at` int DEFAULT NULL,
  `updated_at` int DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_rule`
--

LOCK TABLES `auth_rule` WRITE;
/*!40000 ALTER TABLE `auth_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enterprise`
--

DROP TABLE IF EXISTS `enterprise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enterprise` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contactNumber` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contactEmail` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `website` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enterprise`
--

LOCK TABLES `enterprise` WRITE;
/*!40000 ALTER TABLE `enterprise` DISABLE KEYS */;
INSERT INTO `enterprise` VALUES (1,'Admins','Rua dos ADMS','917654654','admins@admins.pt','admins.pt');
/*!40000 ALTER TABLE `enterprise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meter`
--

DROP TABLE IF EXISTS `meter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `meter` (
  `id` int NOT NULL AUTO_INCREMENT,
  `address` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `userID` int NOT NULL,
  `meterTypeID` int NOT NULL,
  `enterpriseID` int NOT NULL,
  `class` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `instalationDate` date NOT NULL,
  `shutdownDate` date DEFAULT NULL,
  `maxCapacity` decimal(10,2) NOT NULL,
  `measureUnity` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supportedTemperature` decimal(5,2) NOT NULL,
  `state` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`userID`),
  KEY `idx_meter_type_id` (`meterTypeID`),
  KEY `idx_enterprise_id` (`enterpriseID`),
  CONSTRAINT `fk_meter_enterprise` FOREIGN KEY (`enterpriseID`) REFERENCES `enterprise` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_meter_metertype` FOREIGN KEY (`meterTypeID`) REFERENCES `metertype` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_meter_user` FOREIGN KEY (`userID`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meter`
--

LOCK TABLES `meter` WRITE;
/*!40000 ALTER TABLE `meter` DISABLE KEYS */;
INSERT INTO `meter` VALUES (3,'Rua das Flores 1',1,1,1,'A','2014-03-20',NULL,1000.00,'bar',70.00,1),(4,'Rua teste',2,1,1,'B','2000-01-01',NULL,1000.00,'2',90.00,2);
/*!40000 ALTER TABLE `meter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meterproblem`
--

DROP TABLE IF EXISTS `meterproblem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `meterproblem` (
  `id` int NOT NULL AUTO_INCREMENT,
  `meterID` int NOT NULL,
  `userID` int NOT NULL,
  `problemType` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `desc` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_meter_id` (`meterID`),
  KEY `idx_user_id` (`userID`),
  CONSTRAINT `fk_meterProblem_meter` FOREIGN KEY (`meterID`) REFERENCES `meter` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_meterProblem_user` FOREIGN KEY (`userID`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meterproblem`
--

LOCK TABLES `meterproblem` WRITE;
/*!40000 ALTER TABLE `meterproblem` DISABLE KEYS */;
INSERT INTO `meterproblem` VALUES (1,3,1,'Fuga','Fuga de agua');
/*!40000 ALTER TABLE `meterproblem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meterreading`
--

DROP TABLE IF EXISTS `meterreading`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `meterreading` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userID` int NOT NULL,
  `meterID` int NOT NULL,
  `problemID` int DEFAULT NULL,
  `reading` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `accumulatedConsumption` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date` date NOT NULL,
  `waterPressure` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `desc` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `readingType` tinyint(1) NOT NULL,
  `problemState` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_meter_id` (`meterID`),
  KEY `idx_user_id` (`userID`),
  KEY `idx_problem_id` (`problemID`),
  CONSTRAINT `fk_meterReading_meter` FOREIGN KEY (`meterID`) REFERENCES `meter` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_meterReading_meterProblem` FOREIGN KEY (`problemID`) REFERENCES `meterproblem` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_meterReading_user` FOREIGN KEY (`userID`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meterreading`
--

LOCK TABLES `meterreading` WRITE;
/*!40000 ALTER TABLE `meterreading` DISABLE KEYS */;
INSERT INTO `meterreading` VALUES (1,1,3,1,'1000','1000','2025-09-10','100','leitura',0,0),(2,1,4,1,'100000','23045','0000-00-00','54','leitura',0,0),(3,1,3,1,'2391','2183','0000-00-00','156','leitura',1,0);
/*!40000 ALTER TABLE `meterreading` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metertype`
--

DROP TABLE IF EXISTS `metertype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metertype` (
  `id` int NOT NULL AUTO_INCREMENT,
  `description` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metertype`
--

LOCK TABLES `metertype` WRITE;
/*!40000 ALTER TABLE `metertype` DISABLE KEYS */;
INSERT INTO `metertype` VALUES (1,'mudou'),(2,'aiai');
/*!40000 ALTER TABLE `metertype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `migration`
--

DROP TABLE IF EXISTS `migration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `migration` (
  `version` varchar(180) NOT NULL,
  `apply_time` int DEFAULT NULL,
  PRIMARY KEY (`version`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `migration`
--

LOCK TABLES `migration` WRITE;
/*!40000 ALTER TABLE `migration` DISABLE KEYS */;
INSERT INTO `migration` VALUES ('m000000_000000_base',1761143679),('m130524_201442_init',1761143682),('m190124_110200_add_verification_token_column_to_user_table',1761143682),('m140506_102106_rbac_init',1761146491),('m170907_052038_rbac_add_index_on_auth_assignment_user_id',1761146491),('m180523_151638_rbac_updates_indexes_without_prefix',1761146491),('m200409_110543_rbac_update_mssql_trigger',1761146491),('m251022_150727_init_rbac',1761146501);
/*!40000 ALTER TABLE `migration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `technicianinfo`
--

DROP TABLE IF EXISTS `technicianinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `technicianinfo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userID` int NOT NULL,
  `enterpriseID` int NOT NULL,
  `profissionalCertificateNumber` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`userID`),
  KEY `idx_enterprise_id` (`enterpriseID`),
  CONSTRAINT `fk_technicianinfo_enterprise` FOREIGN KEY (`enterpriseID`) REFERENCES `enterprise` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_technicianinfo_user` FOREIGN KEY (`userID`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `technicianinfo`
--

LOCK TABLES `technicianinfo` WRITE;
/*!40000 ALTER TABLE `technicianinfo` DISABLE KEYS */;
INSERT INTO `technicianinfo` VALUES (1,1,1,'123456789'),(2,8,1,'1234567890'),(3,12,1,'123456789'),(4,10,1,'1234567890');
/*!40000 ALTER TABLE `technicianinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8mb3_unicode_ci NOT NULL,
  `auth_key` varchar(32) COLLATE utf8mb3_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb3_unicode_ci NOT NULL,
  `password_reset_token` varchar(255) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb3_unicode_ci NOT NULL,
  `status` smallint NOT NULL DEFAULT '10',
  `created_at` int NOT NULL,
  `updated_at` int NOT NULL,
  `verification_token` varchar(255) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `password_reset_token` (`password_reset_token`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','ciOgYMRWd5Bj1kFjuDqqKM0tuOLH2a_p','$2y$13$4XRzxuE4BeIZSrolUqGiDe.DVBhd0P0EM.KFa9F7wE3eWYUPXRW76',NULL,'admin@example.com',10,1761144963,1765472149,'O699eNwy857kQqgmD5LxUJIJoVDqfUbT_1761144963'),(2,'morador','YCf36yVba0tnI_awSXzlh_i_Rfp8fl-9','$2y$13$LfA/9PoXM7HpCicYNQB.l.M/lue8daEJBqMVyl0FH2.pekob/ia02',NULL,'morador@example.com',9,1762182667,1762182667,'cRhvNdlomdSrXuOHE-aIQNZJxI5ADFdx_1762182667'),(3,'teste','BE5_yx9XydKnDS3wgJQSFeqVNqjTZLff','$2y$13$wm5nITGZe.IeifNqDLz3SO.OOPSlnzxAMPtKhjQy1DIprZZxThzzO',NULL,'teste@example.com',9,1762265684,1762265684,'9RSv6_cv2NEOrQRtUJOfyS138tprOanY_1762265684'),(4,'teste2','6uDjNt67AUmGtlzNk7JQdyEZTNZ3nLxa','$2y$13$lpeHlyuG./lknG8ovk62RumKojDCzqHqZm0Pz1vhfkfR8uYZH/cOO',NULL,'teste2@example.com',9,1762265911,1762265911,'Bb0b_os6m_4Wk5OIfIh-7JgI2LI8mq0-_1762265911'),(5,'tsete3','KPdKX4YpqhDoU5FOKMFC3yzwfkgRdhHd','$2y$13$xGRMnRtaOR38qS0H45qs5.BZ8ilivip1uEUDDCiq0E.hmlY3kZ7IK',NULL,'teste3@example.com',9,1762265942,1762265942,'ZMZtjv3UxomWlqoZJuh0n_AxLG1qc2VG_1762265942'),(6,'teste4','Wiq3-5607uvXgl_TpX9pqPyCLdjw9aOR','$2y$13$XShrb9KkWHSqa534akr7Y.tGUGnzwzf2veE22qkCGaMMJ9OWw2giS',NULL,'teste4@example.com',9,1762266168,1762266168,'OE3hwOXQ1ZtSYINOIpIcGFS63NG4pf1R_1762266168'),(7,'teste5','u65kL5IWf5wSRruZKwov8BwY0GioD1Zy','$2y$13$OijtH188QvUNWoZxzEG6iuvTHOueuHL0bgMGsPKUuS7tt0v/WgdH6',NULL,'teste5@example.com',9,1762266493,1762266493,'XFSZzp4CDJ4Z--GpppRZBdsKfmPFGtcC_1762266493'),(8,'tsete6','3pXLOJJKsrlkjbOgzAhKD3pnjw4zCs19','$2y$13$yp77.gFJO9Y3sjj3.tawFu9o61LMfYFYYy9mCMqhSzD9kPGlDZUBW',NULL,'teste6@example.com',10,1762266568,1763507336,'wvwUNftL8tETKEpzPbPsGyZ_HhLygKrY_1762266568'),(9,'teste7','PPHgGE6QtPiHfZMDOUdXFtf9q6dS_Vc6','$2y$13$1yhPCGoiS/1TqI1v9BJ2FOynct.L2WuftLDB.yo9sKOGTpJrxa3Mi',NULL,'teste7@example.com',9,1762266602,1762266602,'ivMqWyJwvCA3URjHBR-5BWO8a2WJkAJa_1762266602'),(10,'rbacteste','yv1sqlCai-RsD-drRkVHpVfKPBdMcfum','$2y$13$3wjpcvV5rV6q1osrVoXlTOaV3fe6ef6TOpTll27n6iY7d4G/N1DLa',NULL,'rbacteste@gmail.com',9,1763054566,1765472404,'R4PwR3zOopeqdnUQwQGkxrUBkWvOT9mh_1763054566'),(11,'inactiveuser','JS696qmoZMZqwMJ6wr6MBYxQVezt3EPm','$2y$13$UCTTuEQMZgkFoBUxdCcjGOookNBG6Q2U2I1R8UhGwLblm3e57eKba',NULL,'inactiveuser@example.com',9,1763485486,1763485486,'XBgRPm54u2t4-sLrPgZyV00Ep_fv1qyk_1763485486'),(12,'teste10','nq2k2_z1EMFRFDCP3_IFVgtdnBIkuMzk','$2y$13$dRMlZC0sMMAjcoVSFugv4Oen59GeryJRoYkIewbMKIDOHpddZiO8i',NULL,'teste10@example.com',9,1763507683,1763507683,'9aq8AdBTSsUyJ12wsHW9tsUYGyLocMFn_1763507683'),(13,'teste11','w6_q44R_3IzpkmExWL3j5a6-9ncru35m','$2y$13$0y68sqm.elP54DZSl/eM5uJMI6PjHQiepjNn9SIlAAgNx3MXYkEqG',NULL,'teste11@example.com',9,1763507717,1763507717,'8XGXoPpIMm7A1Em5YgWHep0gXDyKfA3W_1763507717'),(14,'example4','WYK3FOglPmxlTmfVTmJ2bwVj0kv9mN0I','$2y$13$XCaUdm7yzjmWeMPJOXvm2Oj5WP..h4l2RD7SSkH4hswTYOFIqg4HG',NULL,'example4@example.com',10,1763733463,1763733463,'gTiFtWa4nrthMLPbBrv5wVVZM-OTiwxY_1763733463'),(17,'testeApiSignup','f5Mk_Tu0Us6bXx8rEAYop44CTcNQnIvc','$2y$13$529jo9W6O1ioYdMx6z4ULu6dkp4nvvMrLmjbdZoCUQtmXLhxkaciK',NULL,'testeApiSignup@example.pt',9,1764341588,1764341588,'SbTRpV0F4iLbqZfpMcW_I2fbrSBNYi8G_1764341588'),(18,'testeApiSignup2','NngOfjL7EelIolMdMXy0DE15xq-3weUS','$2y$13$zgCxCOCmhddjnKnROSmzWuPIkOkIUep.XcmNRLANki.LMxCb9blpq',NULL,'testeApiSignup2@example.pt',9,1764341604,1764341604,'wXFs3uKICuJnb43JV6i1MDOlLfzlfw8Z_1764341604');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userprofile`
--

DROP TABLE IF EXISTS `userprofile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userprofile` (
  `id` int NOT NULL AUTO_INCREMENT,
  `birthDate` date NOT NULL,
  `address` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `userID` int NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userID` (`userID`),
  KEY `idx_user_id` (`userID`),
  CONSTRAINT `fk_userprofile_user` FOREIGN KEY (`userID`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userprofile`
--

LOCK TABLES `userprofile` WRITE;
/*!40000 ALTER TABLE `userprofile` DISABLE KEYS */;
INSERT INTO `userprofile` VALUES (1,'2000-01-01','Rua dos ADMS',1,NULL),(2,'2000-01-01','N/A',8,'N/A'),(3,'2000-01-01','N/A',9,NULL),(4,'2000-01-01','N/A',10,'N/A'),(5,'2000-01-01','N/A',11,NULL),(6,'2020-01-01','rua teste',12,'teste10'),(7,'2020-01-01','rua teste',13,'teste11'),(8,'2000-01-01','N/A',14,NULL),(9,'2000-01-01','N/A',17,'N/A'),(10,'2000-01-01','N/A',18,'N/A');
/*!40000 ALTER TABLE `userprofile` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-11 17:09:23
