-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: rscunity_logs
-- ------------------------------------------------------
-- Server version	5.7.21

CREATE DATABASE IF NOT EXISTS `openrsc_logs`;
use `openrsc_logs`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `game_bans`
--

DROP TABLE IF EXISTS `game_bans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_bans` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `account` int(11) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `player` varchar(40) NOT NULL,
  `reason` mediumtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_bans`
--

LOCK TABLES `game_bans` WRITE;
/*!40000 ALTER TABLE `game_bans` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_bans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_chat`
--

DROP TABLE IF EXISTS `game_chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_chat` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `message` varchar(255) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `message` (`message`),
  KEY `ip` (`ip`),
  KEY `user` (`user`)
) ENGINE=MyISAM AUTO_INCREMENT=2714913 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_chat`
--

LOCK TABLES `game_chat` WRITE;
/*!40000 ALTER TABLE `game_chat` DISABLE KEYS */;
INSERT INTO `game_chat` VALUES ('51697882930',1,'127.0.0.1',1527812891,'D ',2714890),('51697882930',1,'127.0.0.1',1527813158,'D ',2714891),('51697882930',1,'127.0.0.1',1527813168,' Online ',2714892),('51697882930',1,'127.0.0.1',1527813205,'Command',2714893),('51697882930',1,'127.0.0.1',1527813272,'D ',2714894),('51697882930',1,'127.0.0.1',1527813426,'D ',2714895),('51697882930',1,'127.0.0.1',1527881240,'D ',2714896),('51697882930',1,'127.0.0.1',1527881249,'S ',2714897),('51697882930',1,'127.0.0.1',1527882094,'D ',2714898),('51697882930',1,'127.0.0.1',1527882152,'-  ',2714899),('51697882930',1,'127.0.0.1',1527882271,'D ',2714900),('51697882930',1,'127.0.0.1',1527882466,'D ',2714901),('51697882930',1,'127.0.0.1',1527882468,'D ',2714902),('51697882930',1,'127.0.0.1',1527882480,'D ',2714903),('51697882930',1,'127.0.0.1',1527882518,'D ',2714904),('51697882930',1,'127.0.0.1',1527882580,'Unlock 1362012 ',2714905),('51697882930',1,'127.0.0.1',1527883189,'D ',2714906),('51697882930',1,'127.0.0.1',1527883431,'D ',2714907),('51697882930',1,'127.0.0.1',1527883435,'D ',2714908),('51697882930',1,'127.0.0.1',1527883437,'A ',2714909),('51697882930',1,'127.0.0.1',1527883503,'D ',2714910),('51697882930',1,'127.0.0.1',1527967204,'D ',2714911),('51697882930',1,'127.0.0.1',1527968434,'D ',2714912);
/*!40000 ALTER TABLE `game_chat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_collect`
--

DROP TABLE IF EXISTS `game_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_collect` (
  `user` bigint(20) NOT NULL,
  `account` int(11) NOT NULL,
  `time` int(11) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `amount` bigint(11) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `user` (`user`,`account`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_collect`
--

LOCK TABLES `game_collect` WRITE;
/*!40000 ALTER TABLE `game_collect` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_connect`
--

DROP TABLE IF EXISTS `game_connect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_connect` (
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `ip` (`ip`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_connect`
--

LOCK TABLES `game_connect` WRITE;
/*!40000 ALTER TABLE `game_connect` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_connect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_death`
--

DROP TABLE IF EXISTS `game_death`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_death` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `time` int(10) NOT NULL,
  `x` int(4) NOT NULL,
  `y` int(4) NOT NULL,
  `id` int(6) NOT NULL AUTO_INCREMENT,
  `item1_id` int(4) DEFAULT NULL,
  `item1_amount` int(4) DEFAULT NULL,
  `item2_id` int(4) DEFAULT NULL,
  `item2_amount` int(4) DEFAULT NULL,
  `item3_id` int(4) DEFAULT NULL,
  `item3_amount` int(4) DEFAULT NULL,
  `item4_id` int(4) DEFAULT NULL,
  `item4_amount` int(4) DEFAULT NULL,
  `item5_id` int(4) DEFAULT NULL,
  `item5_amount` int(4) DEFAULT NULL,
  `item6_id` int(4) DEFAULT NULL,
  `item6_amount` int(4) DEFAULT NULL,
  `item7_id` int(4) DEFAULT NULL,
  `item7_amount` int(4) DEFAULT NULL,
  `item8_id` int(4) DEFAULT NULL,
  `item8_amount` int(4) DEFAULT NULL,
  `item9_id` int(4) DEFAULT NULL,
  `item9_amount` int(4) DEFAULT NULL,
  `item10_id` int(4) DEFAULT NULL,
  `item10_amount` int(4) DEFAULT NULL,
  `item11_id` int(4) DEFAULT NULL,
  `item11_amount` int(4) DEFAULT NULL,
  `item12_id` int(4) DEFAULT NULL,
  `item12_amount` int(4) DEFAULT NULL,
  `item13_id` int(4) DEFAULT NULL,
  `item13_amount` int(4) DEFAULT NULL,
  `item14_id` int(4) DEFAULT NULL,
  `item14_amount` int(4) DEFAULT NULL,
  `item15_id` int(4) DEFAULT NULL,
  `item15_amount` int(4) DEFAULT NULL,
  `item16_id` int(4) DEFAULT NULL,
  `item16_amount` int(4) DEFAULT NULL,
  `item17_id` int(4) DEFAULT NULL,
  `item17_amount` int(4) DEFAULT NULL,
  `item18_id` int(4) DEFAULT NULL,
  `item18_amount` int(4) DEFAULT NULL,
  `item19_id` int(4) DEFAULT NULL,
  `item19_amount` int(4) DEFAULT NULL,
  `item20_id` int(4) DEFAULT NULL,
  `item20_amount` int(4) DEFAULT NULL,
  `item21_id` int(4) DEFAULT NULL,
  `item21_amount` int(4) DEFAULT NULL,
  `item22_id` int(4) DEFAULT NULL,
  `item22_amount` int(4) DEFAULT NULL,
  `item23_id` int(4) DEFAULT NULL,
  `item23_amount` int(4) DEFAULT NULL,
  `item24_id` int(4) DEFAULT NULL,
  `item24_amount` int(4) DEFAULT NULL,
  `item25_id` int(4) DEFAULT NULL,
  `item25_amount` int(4) DEFAULT NULL,
  `item26_id` int(4) DEFAULT NULL,
  `item26_amount` int(4) DEFAULT NULL,
  `item27_id` int(4) DEFAULT NULL,
  `item27_amount` int(4) DEFAULT NULL,
  `item28_id` int(4) DEFAULT NULL,
  `item28_amount` int(4) DEFAULT NULL,
  `item29_id` int(4) DEFAULT NULL,
  `item29_amount` int(4) DEFAULT NULL,
  `item30_id` int(4) DEFAULT NULL,
  `item30_amount` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `ip` (`ip`)
) ENGINE=MyISAM AUTO_INCREMENT=1007129 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_death`
--

LOCK TABLES `game_death` WRITE;
/*!40000 ALTER TABLE `game_death` DISABLE KEYS */;
INSERT INTO `game_death` VALUES ('51697882930',1,'127.0.0.1',1527883931,224,446,1007128,10,9999,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `game_death` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_drop`
--

DROP TABLE IF EXISTS `game_drop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_drop` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `x` int(4) NOT NULL,
  `y` int(4) NOT NULL,
  `item` int(4) NOT NULL,
  `amount` int(10) NOT NULL,
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `ip` (`ip`),
  KEY `item` (`item`),
  KEY `amount` (`amount`)
) ENGINE=MyISAM AUTO_INCREMENT=496115 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_drop`
--

LOCK TABLES `game_drop` WRITE;
/*!40000 ALTER TABLE `game_drop` DISABLE KEYS */;
INSERT INTO `game_drop` VALUES ('51697882930',1,'127.0.0.1',221,436,357,0,1527883365,496013),('51697882930',1,'127.0.0.1',221,436,357,0,1527883366,496014),('51697882930',1,'127.0.0.1',221,436,357,0,1527883367,496015),('51697882930',1,'127.0.0.1',221,436,357,0,1527883367,496016),('51697882930',1,'127.0.0.1',221,436,357,0,1527883368,496017),('51697882930',1,'127.0.0.1',221,436,357,0,1527883368,496018),('51697882930',1,'127.0.0.1',221,436,357,0,1527883369,496019),('51697882930',1,'127.0.0.1',221,436,357,0,1527883369,496020),('51697882930',1,'127.0.0.1',221,436,357,0,1527883370,496021),('51697882930',1,'127.0.0.1',221,436,357,0,1527883370,496022),('51697882930',1,'127.0.0.1',221,436,357,0,1527883371,496023),('51697882930',1,'127.0.0.1',221,436,357,0,1527883371,496024),('51697882930',1,'127.0.0.1',221,436,357,0,1527883372,496025),('51697882930',1,'127.0.0.1',221,436,357,0,1527883372,496026),('51697882930',1,'127.0.0.1',221,436,357,0,1527883373,496027),('51697882930',1,'127.0.0.1',221,436,357,0,1527883373,496028),('51697882930',1,'127.0.0.1',221,436,357,0,1527883374,496029),('51697882930',1,'127.0.0.1',221,436,357,0,1527883374,496030),('51697882930',1,'127.0.0.1',221,436,357,0,1527883375,496031),('51697882930',1,'127.0.0.1',226,447,41,6,1527968083,496032),('51697882930',1,'127.0.0.1',226,448,41,6,1527968094,496033),('51697882930',1,'127.0.0.1',226,448,81,1,1527968106,496034),('51697882930',1,'127.0.0.1',226,445,41,6,1527968116,496035),('51697882930',1,'127.0.0.1',226,446,10,3,1527968123,496036),('51697882930',1,'127.0.0.1',226,446,41,6,1527968137,496037),('51697882930',1,'127.0.0.1',225,445,10,3,1527968141,496038),('51697882930',1,'127.0.0.1',225,445,10,3,1527968271,496039),('51697882930',1,'127.0.0.1',224,442,357,0,1527968849,496040),('51697882930',1,'127.0.0.1',224,442,357,0,1527968850,496041),('51697882930',1,'127.0.0.1',224,442,357,0,1527968850,496042),('51697882930',1,'127.0.0.1',224,442,357,0,1527968851,496043),('51697882930',1,'127.0.0.1',224,442,357,0,1527968851,496044),('51697882930',1,'127.0.0.1',224,442,357,0,1527968852,496045),('51697882930',1,'127.0.0.1',224,442,357,0,1527968852,496046),('51697882930',1,'127.0.0.1',224,442,357,0,1527968853,496047),('51697882930',1,'127.0.0.1',224,442,11,51,1527968860,496048),('51697882930',1,'127.0.0.1',224,442,357,0,1527968870,496049),('51697882930',1,'127.0.0.1',224,442,357,0,1527968871,496050),('51697882930',1,'127.0.0.1',224,442,357,0,1527968871,496051),('51697882930',1,'127.0.0.1',224,442,357,0,1527968872,496052),('51697882930',1,'127.0.0.1',224,442,357,0,1527968872,496053),('51697882930',1,'127.0.0.1',224,442,357,0,1527968873,496054),('51697882930',1,'127.0.0.1',224,442,357,0,1527968873,496055),('51697882930',1,'127.0.0.1',224,442,357,0,1527968874,496056),('51697882930',1,'127.0.0.1',224,442,357,0,1527968874,496057),('51697882930',1,'127.0.0.1',224,442,357,0,1527968875,496058),('51697882930',1,'127.0.0.1',224,442,357,0,1527968875,496059),('51697882930',1,'127.0.0.1',224,442,357,0,1527968876,496060),('51697882930',1,'127.0.0.1',224,442,357,0,1527968876,496061),('51697882930',1,'127.0.0.1',224,442,357,0,1527968877,496062),('51697882930',1,'127.0.0.1',224,442,357,0,1527968877,496063),('51697882930',1,'127.0.0.1',224,442,357,0,1527968878,496064),('51697882930',1,'127.0.0.1',224,442,357,0,1527968878,496065),('51697882930',1,'127.0.0.1',224,442,357,0,1527968879,496066),('51697882930',1,'127.0.0.1',224,442,357,0,1527968880,496067),('51697882930',1,'127.0.0.1',224,442,357,0,1527968880,496068),('51697882930',1,'127.0.0.1',217,453,20,0,1527969570,496069),('51697882930',1,'127.0.0.1',217,453,20,0,1527969571,496070),('51697882930',1,'127.0.0.1',217,453,20,0,1527969571,496071),('51697882930',1,'127.0.0.1',217,453,20,0,1527969572,496072),('51697882930',1,'127.0.0.1',217,453,20,0,1527969572,496073),('51697882930',1,'127.0.0.1',217,453,20,0,1527969573,496074),('51697882930',1,'127.0.0.1',217,453,20,0,1527969573,496075),('51697882930',1,'127.0.0.1',217,453,20,0,1527969574,496076),('51697882930',1,'127.0.0.1',217,453,20,0,1527969574,496077),('51697882930',1,'127.0.0.1',217,453,20,0,1527969575,496078),('51697882930',1,'127.0.0.1',217,453,20,0,1527969576,496079),('51697882930',1,'127.0.0.1',217,453,20,0,1527969576,496080),('51697882930',1,'127.0.0.1',217,453,20,0,1527969577,496081),('51697882930',1,'127.0.0.1',217,453,28,1,1527969589,496082),('51697882930',1,'127.0.0.1',217,453,380,1,1527969594,496083),('51697882930',1,'127.0.0.1',217,453,104,1,1527969606,496084),('51697882930',1,'127.0.0.1',217,453,450,1,1527969631,496085),('51697882930',1,'127.0.0.1',217,453,451,1,1527969633,496086),('51697882930',1,'127.0.0.1',217,453,444,1,1527969635,496087),('51697882930',1,'127.0.0.1',217,453,449,1,1527969637,496088),('51697882930',1,'127.0.0.1',217,453,448,1,1527969639,496089),('51697882930',1,'127.0.0.1',217,453,448,0,1527969641,496090),('51697882930',1,'127.0.0.1',217,453,448,0,1527969642,496091),('51697882930',1,'127.0.0.1',217,453,445,1,1527969644,496092),('51697882930',1,'127.0.0.1',217,453,11,43,1527969648,496093),('51697882930',1,'127.0.0.1',217,453,188,1,1527969650,496094),('51697882930',1,'127.0.0.1',217,453,35,16,1527969653,496095),('51697882930',1,'127.0.0.1',212,451,81,1,1527969694,496096),('51697882930',1,'127.0.0.1',216,451,576,1,1527969774,496097),('51697882930',1,'127.0.0.1',215,452,1093,1,1527969921,496098),('51697882930',1,'127.0.0.1',215,451,1095,1,1527969936,496099),('51697882930',1,'127.0.0.1',215,451,1096,1,1527969941,496100),('51697882930',1,'127.0.0.1',216,450,575,1,1527970001,496101),('51697882930',1,'127.0.0.1',216,452,755,1,1527970132,496102),('51697882930',1,'127.0.0.1',217,448,833,1,1527970179,496103),('51697882930',1,'127.0.0.1',217,448,620,1,1527970192,496104),('51697882930',1,'127.0.0.1',215,452,582,1,1527970316,496105),('51697882930',1,'127.0.0.1',215,452,581,1,1527970324,496106),('51697882930',1,'127.0.0.1',215,452,584,1,1527970382,496107),('51697882930',1,'127.0.0.1',215,452,1119,1,1527970386,496108),('51697882930',1,'127.0.0.1',215,452,582,1,1527970393,496109),('51697882930',1,'127.0.0.1',217,452,465,1,1527970526,496110),('51697882930',1,'127.0.0.1',217,452,496,1,1527970541,496111),('51697882930',1,'127.0.0.1',217,452,495,1,1527970553,496112),('51697882930',1,'127.0.0.1',217,452,493,1,1527970564,496113),('51697882930',1,'127.0.0.1',211,445,861,1,1527970635,496114);
/*!40000 ALTER TABLE `game_drop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_duel`
--

DROP TABLE IF EXISTS `game_duel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_duel` (
  `user1` varchar(40) NOT NULL,
  `account1` int(11) DEFAULT NULL,
  `user1_ip` varchar(15) NOT NULL,
  `user2` varchar(40) NOT NULL,
  `account2` int(11) DEFAULT NULL,
  `user2_ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `no_retreating` tinyint(1) NOT NULL,
  `no_prayer` tinyint(1) NOT NULL,
  `no_magic` tinyint(1) NOT NULL,
  `no_weapons` tinyint(1) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user1_item1` int(4) DEFAULT NULL,
  `user1_amount1` int(10) DEFAULT NULL,
  `user1_item2` int(4) DEFAULT NULL,
  `user1_amount2` int(10) DEFAULT NULL,
  `user1_item3` int(4) DEFAULT NULL,
  `user1_amount3` int(10) DEFAULT NULL,
  `user1_item4` int(4) DEFAULT NULL,
  `user1_amount4` int(10) DEFAULT NULL,
  `user1_item5` int(4) DEFAULT NULL,
  `user1_amount5` int(10) DEFAULT NULL,
  `user1_item6` int(4) DEFAULT NULL,
  `user1_amount6` int(10) DEFAULT NULL,
  `user1_item7` int(4) DEFAULT NULL,
  `user1_amount7` int(10) DEFAULT NULL,
  `user1_item8` int(4) DEFAULT NULL,
  `user1_amount8` int(10) DEFAULT NULL,
  `user2_item1` int(4) DEFAULT NULL,
  `user2_amount1` int(10) DEFAULT NULL,
  `user2_item2` int(4) DEFAULT NULL,
  `user2_amount2` int(10) DEFAULT NULL,
  `user2_item3` int(4) DEFAULT NULL,
  `user2_amount3` int(10) DEFAULT NULL,
  `user2_item4` int(4) DEFAULT NULL,
  `user2_amount4` int(10) DEFAULT NULL,
  `user2_item5` int(4) DEFAULT NULL,
  `user2_amount5` int(10) DEFAULT NULL,
  `user2_item6` int(4) DEFAULT NULL,
  `user2_amount6` int(10) DEFAULT NULL,
  `user2_item7` int(4) DEFAULT NULL,
  `user2_amount7` int(10) DEFAULT NULL,
  `user2_item8` int(4) DEFAULT NULL,
  `user2_amount8` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user1` (`user1`),
  KEY `user1_ip` (`user1_ip`),
  KEY `user2` (`user2`),
  KEY `user2_ip` (`user2_ip`)
) ENGINE=MyISAM AUTO_INCREMENT=551932 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_duel`
--

LOCK TABLES `game_duel` WRITE;
/*!40000 ALTER TABLE `game_duel` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_duel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_error`
--

DROP TABLE IF EXISTS `game_error`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_error` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `error` varchar(255) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `ip` (`ip`)
) ENGINE=MyISAM AUTO_INCREMENT=33540 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_error`
--

LOCK TABLES `game_error` WRITE;
/*!40000 ALTER TABLE `game_error` DISABLE KEYS */;
INSERT INTO `game_error` VALUES ('51697882930',1,'127.0.0.1',1527884045,'Kill-Stealing fucked up - killer was not in damage table',33536),('51697882930',1,'127.0.0.1',1527884050,'Kill-Stealing fucked up - killer was not in damage table',33537),('51697882930',1,'127.0.0.1',1527884057,'Kill-Stealing fucked up - killer was not in damage table',33538),('51697882930',1,'127.0.0.1',1527884064,'Kill-Stealing fucked up - killer was not in damage table',33539);
/*!40000 ALTER TABLE `game_error` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_event`
--

DROP TABLE IF EXISTS `game_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_event` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user` varchar(40) NOT NULL,
  `account` int(11) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `message` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_event`
--

LOCK TABLES `game_event` WRITE;
/*!40000 ALTER TABLE `game_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_exploit`
--

DROP TABLE IF EXISTS `game_exploit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_exploit` (
  `user` bigint(20) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `exploit` varchar(500) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `user` (`user`)
) ENGINE=MyISAM AUTO_INCREMENT=13756616 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_exploit`
--

LOCK TABLES `game_exploit` WRITE;
/*!40000 ALTER TABLE `game_exploit` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_exploit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_generic`
--

DROP TABLE IF EXISTS `game_generic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_generic` (
  `message` varchar(255) NOT NULL,
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game_command`
--

DROP TABLE IF EXISTS `game_command`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_command` (
 `id` int NOT NULL AUTO_INCREMENT,
 `account` int NOT NULL,
 `user` varchar(45) NOT NULL,
 `username` varchar(12) NOT NULL,
 `group_id` int NOT NULL,
 `ip` varchar(30) NOT NULL,
 `command` varchar(255) NOT NULL,
 `time` int NOT NULL,
 PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game_global`
--

DROP TABLE IF EXISTS `game_global`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_global` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `message` varchar(255) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `ip` (`ip`),
  KEY `message` (`message`)
) ENGINE=MyISAM AUTO_INCREMENT=478867 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_global`
--

LOCK TABLES `game_global` WRITE;
/*!40000 ALTER TABLE `game_global` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_global` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_login`
--

DROP TABLE IF EXISTS `game_login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_login` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `uid` int(8) DEFAULT NULL,
  `time` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `id` int(6) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `ip` (`ip`)
) ENGINE=MyISAM AUTO_INCREMENT=4182465 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_login`
--

LOCK TABLES `game_login` WRITE;
/*!40000 ALTER TABLE `game_login` DISABLE KEYS */;
INSERT INTO `game_login` VALUES ('51697882930',1,NULL,1527812828,'127.0.0.1',4182434),('51697882930',1,NULL,1527812965,'127.0.0.1',4182435),('51697882930',1,NULL,1527813155,'127.0.0.1',4182436),('51697882930',1,NULL,1527813271,'127.0.0.1',4182437),('51697882930',1,NULL,1527813424,'127.0.0.1',4182438),('51697882930',1,NULL,1527881233,'127.0.0.1',4182439),('51697882930',1,NULL,1527882082,'127.0.0.1',4182440),('51697882930',1,NULL,1527882464,'127.0.0.1',4182441),('51697882930',1,NULL,1527882672,'127.0.0.1',4182442),('51697882930',1,NULL,1527883443,'127.0.0.1',4182443),('51697882930',1,NULL,1527883662,'127.0.0.1',4182444),('51697882930',1,NULL,1527883856,'127.0.0.1',4182445),('51697882930',1,NULL,1527884607,'127.0.0.1',4182446),('51697882930',1,NULL,1527885146,'127.0.0.1',4182447),('51697882930',1,NULL,1527885465,'127.0.0.1',4182448),('51697882930',1,NULL,1527886586,'127.0.0.1',4182449),('51697882930',1,NULL,1527887109,'127.0.0.1',4182450),('51697882930',1,NULL,1527967196,'127.0.0.1',4182451),('51697882930',1,NULL,1527967506,'127.0.0.1',4182452),('51697882930',1,NULL,1527967798,'127.0.0.1',4182453),('51697882930',1,NULL,1527967891,'127.0.0.1',4182454),('51697882930',1,NULL,1527968059,'127.0.0.1',4182455),('51697882930',1,NULL,1527968265,'127.0.0.1',4182456),('51697882930',1,NULL,1527968403,'127.0.0.1',4182457),('51697882930',1,NULL,1527968431,'127.0.0.1',4182458),('51697882930',1,NULL,1527968632,'127.0.0.1',4182459),('51697882930',1,NULL,1527968693,'127.0.0.1',4182460),('51697882930',1,NULL,1527969267,'127.0.0.1',4182461),('51697882930',1,NULL,1527969466,'127.0.0.1',4182462),('51697882930',1,NULL,1527971988,'127.0.0.1',4182463),('51697882930',1,NULL,1528107597,'127.0.0.1',4182464);
/*!40000 ALTER TABLE `game_login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_pickup`
--

DROP TABLE IF EXISTS `game_pickup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_pickup` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `x` int(4) NOT NULL,
  `y` int(4) NOT NULL,
  `item` int(4) NOT NULL,
  `amount` int(10) NOT NULL,
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `ip` (`ip`),
  KEY `item` (`item`),
  KEY `amount` (`amount`)
) ENGINE=MyISAM AUTO_INCREMENT=3375410 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_pickup`
--

LOCK TABLES `game_pickup` WRITE;
/*!40000 ALTER TABLE `game_pickup` DISABLE KEYS */;
INSERT INTO `game_pickup` VALUES ('51697882930',1,'127.0.0.1',212,444,41,6,1527967927,3375365),('51697882930',1,'127.0.0.1',225,446,41,6,1527968088,3375366),('51697882930',1,'127.0.0.1',226,448,41,6,1527968110,3375367),('51697882930',1,'127.0.0.1',226,446,10,3,1527968131,3375368),('51697882930',1,'127.0.0.1',226,446,41,6,1527968132,3375369),('51697882930',1,'127.0.0.1',225,445,10,3,1527968157,3375370),('51697882930',1,'127.0.0.1',225,445,10,3,1527968273,3375371),('51697882930',1,'127.0.0.1',216,445,20,1,1527969475,3375372),('51697882930',1,'127.0.0.1',215,444,104,1,1527969479,3375373),('51697882930',1,'127.0.0.1',215,444,20,1,1527969480,3375374),('51697882930',1,'127.0.0.1',215,444,35,16,1527969481,3375375),('51697882930',1,'127.0.0.1',216,445,11,1,1527969483,3375376),('51697882930',1,'127.0.0.1',216,445,20,1,1527969486,3375377),('51697882930',1,'127.0.0.1',215,444,11,1,1527969487,3375378),('51697882930',1,'127.0.0.1',216,444,439,1,1527969490,3375379),('51697882930',1,'127.0.0.1',216,444,165,1,1527969499,3375380),('51697882930',1,'127.0.0.1',216,444,20,1,1527969500,3375381),('51697882930',1,'127.0.0.1',213,445,380,1,1527969502,3375382),('51697882930',1,'127.0.0.1',213,445,20,1,1527969503,3375383),('51697882930',1,'127.0.0.1',215,443,20,1,1527969505,3375384),('51697882930',1,'127.0.0.1',215,443,441,1,1527969506,3375385),('51697882930',1,'127.0.0.1',216,442,20,1,1527969508,3375386),('51697882930',1,'127.0.0.1',216,442,10,5,1527969510,3375387),('51697882930',1,'127.0.0.1',215,444,20,1,1527969512,3375388),('51697882930',1,'127.0.0.1',215,444,438,1,1527969513,3375389),('51697882930',1,'127.0.0.1',213,444,435,1,1527969517,3375390),('51697882930',1,'127.0.0.1',213,444,20,1,1527969518,3375391),('51697882930',1,'127.0.0.1',212,443,20,1,1527969521,3375392),('51697882930',1,'127.0.0.1',212,443,438,1,1527969523,3375393),('51697882930',1,'127.0.0.1',215,443,10,5,1527969526,3375394),('51697882930',1,'127.0.0.1',216,442,440,1,1527969530,3375395),('51697882930',1,'127.0.0.1',215,443,20,1,1527969530,3375396),('51697882930',1,'127.0.0.1',216,442,20,1,1527969532,3375397),('51697882930',1,'127.0.0.1',216,441,20,1,1527969534,3375398),('51697882930',1,'127.0.0.1',216,441,28,1,1527969536,3375399),('51697882930',1,'127.0.0.1',216,441,438,1,1527969542,3375400),('51697882930',1,'127.0.0.1',216,441,20,1,1527969545,3375401),('51697882930',1,'127.0.0.1',217,453,16,1,1527969567,3375402),('51697882930',1,'127.0.0.1',215,451,1095,1,1527969945,3375403),('51697882930',1,'127.0.0.1',215,451,1096,1,1527969946,3375404),('51697882930',1,'127.0.0.1',215,452,1093,1,1527969947,3375405),('51697882930',1,'127.0.0.1',216,451,576,1,1527969949,3375406),('51697882930',1,'127.0.0.1',216,450,575,1,1527970004,3375407),('51697882930',1,'127.0.0.1',215,452,582,1,1527970388,3375408),('51697882930',1,'127.0.0.1',215,452,1119,1,1527970391,3375409);
/*!40000 ALTER TABLE `game_pickup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_pm`
--

DROP TABLE IF EXISTS `game_pm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_pm` (
  `sender` varchar(40) NOT NULL,
  `sender_account` int(11) DEFAULT NULL,
  `sender_ip` varchar(15) NOT NULL,
  `reciever` varchar(40) NOT NULL,
  `reciever_account` int(11) DEFAULT NULL,
  `reciever_ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `message` varchar(255) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `sender` (`sender`),
  KEY `sender_ip` (`sender_ip`),
  KEY `reciever` (`reciever`),
  KEY `reciever_ip` (`reciever_ip`),
  KEY `message` (`message`)
) ENGINE=MyISAM AUTO_INCREMENT=11725351 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_pm`
--

LOCK TABLES `game_pm` WRITE;
/*!40000 ALTER TABLE `game_pm` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_pm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_redeem`
--

DROP TABLE IF EXISTS `game_redeem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_redeem` (
  `user` bigint(20) NOT NULL,
  `account` int(11) NOT NULL,
  `time` int(11) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `user` (`user`,`account`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_redeem`
--

LOCK TABLES `game_redeem` WRITE;
/*!40000 ALTER TABLE `game_redeem` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_redeem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_report`
--

DROP TABLE IF EXISTS `game_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_report` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user` bigint(20) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `reported` bigint(20) NOT NULL,
  `reported_account` int(11) DEFAULT NULL,
  `reported_ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `rule` smallint(2) NOT NULL,
  `resolved_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `rule` (`rule`),
  KEY `reported` (`reported`),
  KEY `user` (`user`),
  KEY `reported_ip` (`reported_ip`),
  KEY `ip` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_report`
--

LOCK TABLES `game_report` WRITE;
/*!40000 ALTER TABLE `game_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_report_actions`
--

DROP TABLE IF EXISTS `game_report_actions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_report_actions` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `report_id` int(10) NOT NULL,
  `account` int(10) NOT NULL,
  `time` int(10) NOT NULL,
  `action` tinyint(3) NOT NULL,
  `duration` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_report_actions`
--

LOCK TABLES `game_report_actions` WRITE;
/*!40000 ALTER TABLE `game_report_actions` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_report_actions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_report_comments`
--

DROP TABLE IF EXISTS `game_report_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_report_comments` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `report_id` int(10) NOT NULL,
  `account` int(10) NOT NULL,
  `date` int(10) NOT NULL,
  `message` varchar(500) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_report_comments`
--

LOCK TABLES `game_report_comments` WRITE;
/*!40000 ALTER TABLE `game_report_comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_report_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_script`
--

DROP TABLE IF EXISTS `game_script`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_script` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user` bigint(20) NOT NULL,
  `account` int(11) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(11) NOT NULL,
  `script` varchar(15) NOT NULL,
  `target` bigint(20) NOT NULL,
  `status` tinyint(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_script`
--

LOCK TABLES `game_script` WRITE;
/*!40000 ALTER TABLE `game_script` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_script` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_shop`
--

DROP TABLE IF EXISTS `game_shop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_shop` (
  `user` bigint(30) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `account` int(10) NOT NULL,
  `item_id` int(11) NOT NULL,
  `item_amount` int(11) NOT NULL,
  `action` int(11) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `user` (`user`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_shop`
--

LOCK TABLES `game_shop` WRITE;
/*!40000 ALTER TABLE `game_shop` DISABLE KEYS */;
INSERT INTO `game_shop` VALUES (51697882930,'127.0.0.1',1527968801,1,11,1,1,1),(51697882930,'127.0.0.1',1527968803,1,11,50,1,2),(51697882930,'127.0.0.1',1527968806,1,188,1,1,3),(51697882930,'127.0.0.1',1527968888,1,11,50,1,4),(51697882930,'127.0.0.1',1527968900,1,188,1,1,5);
/*!40000 ALTER TABLE `game_shop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_trade`
--

DROP TABLE IF EXISTS `game_trade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_trade` (
  `user1` varchar(40) NOT NULL,
  `account1` int(11) DEFAULT NULL,
  `user1_ip` varchar(15) NOT NULL,
  `user2` varchar(40) NOT NULL,
  `account2` int(11) DEFAULT NULL,
  `user2_ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user1_item1` int(4) DEFAULT NULL,
  `user1_amount1` int(10) DEFAULT NULL,
  `user1_item2` int(4) DEFAULT NULL,
  `user1_amount2` int(10) DEFAULT NULL,
  `user1_item3` int(4) DEFAULT NULL,
  `user1_amount3` int(10) DEFAULT NULL,
  `user1_item4` int(4) DEFAULT NULL,
  `user1_amount4` int(10) DEFAULT NULL,
  `user1_item5` int(4) DEFAULT NULL,
  `user1_amount5` int(10) DEFAULT NULL,
  `user1_item6` int(4) DEFAULT NULL,
  `user1_amount6` int(10) DEFAULT NULL,
  `user1_item7` int(4) DEFAULT NULL,
  `user1_amount7` int(10) DEFAULT NULL,
  `user1_item8` int(4) DEFAULT NULL,
  `user1_amount8` int(10) DEFAULT NULL,
  `user1_item9` int(4) DEFAULT NULL,
  `user1_amount9` int(10) DEFAULT NULL,
  `user1_item10` int(4) DEFAULT NULL,
  `user1_amount10` int(10) DEFAULT NULL,
  `user1_item11` int(4) DEFAULT NULL,
  `user1_amount11` int(10) DEFAULT NULL,
  `user1_item12` int(4) DEFAULT NULL,
  `user1_amount12` int(10) DEFAULT NULL,
  `user2_item1` int(4) DEFAULT NULL,
  `user2_amount1` int(10) DEFAULT NULL,
  `user2_item2` int(4) DEFAULT NULL,
  `user2_amount2` int(10) DEFAULT NULL,
  `user2_item3` int(4) DEFAULT NULL,
  `user2_amount3` int(10) DEFAULT NULL,
  `user2_item4` int(4) DEFAULT NULL,
  `user2_amount4` int(10) DEFAULT NULL,
  `user2_item5` int(4) DEFAULT NULL,
  `user2_amount5` int(10) DEFAULT NULL,
  `user2_item6` int(4) DEFAULT NULL,
  `user2_amount6` int(10) DEFAULT NULL,
  `user2_item7` int(4) DEFAULT NULL,
  `user2_amount7` int(10) DEFAULT NULL,
  `user2_item8` int(4) DEFAULT NULL,
  `user2_amount8` int(10) DEFAULT NULL,
  `user2_item9` int(4) DEFAULT NULL,
  `user2_amount9` int(10) DEFAULT NULL,
  `user2_item10` int(4) DEFAULT NULL,
  `user2_amount10` int(10) DEFAULT NULL,
  `user2_item11` int(4) DEFAULT NULL,
  `user2_amount11` int(10) DEFAULT NULL,
  `user2_item12` int(4) DEFAULT NULL,
  `user2_amount12` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user1` (`user1`),
  KEY `user1_ip` (`user1_ip`),
  KEY `user2` (`user2`),
  KEY `user2_ip` (`user2_ip`)
) ENGINE=MyISAM AUTO_INCREMENT=2333814 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_trade`
--

LOCK TABLES `game_trade` WRITE;
/*!40000 ALTER TABLE `game_trade` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_trade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loader_error`
--

DROP TABLE IF EXISTS `loader_error`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `loader_error` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `message` text NOT NULL,
  `os` varchar(255) NOT NULL,
  `os_version` varchar(255) NOT NULL,
  `java_vendor` varchar(255) NOT NULL,
  `java_version` varchar(255) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loader_error`
--

LOCK TABLES `loader_error` WRITE;
/*!40000 ALTER TABLE `loader_error` DISABLE KEYS */;
/*!40000 ALTER TABLE `loader_error` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_bank_wipe_recovery`
--

DROP TABLE IF EXISTS `web_bank_wipe_recovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_bank_wipe_recovery` (
  `owner` int(6) NOT NULL,
  `id` smallint(4) NOT NULL,
  `amount` int(10) NOT NULL DEFAULT '1',
  `slot` smallint(3) NOT NULL,
  KEY `owner` (`owner`),
  KEY `id` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_bank_wipe_recovery`
--

LOCK TABLES `web_bank_wipe_recovery` WRITE;
/*!40000 ALTER TABLE `web_bank_wipe_recovery` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_bank_wipe_recovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_create`
--

DROP TABLE IF EXISTS `web_create`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_create` (
  `id` int(6) NOT NULL AUTO_INCREMENT,
  `user` varchar(25) NOT NULL,
  `owner` int(6) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `owner` (`owner`),
  KEY `ip` (`ip`)
) ENGINE=MyISAM AUTO_INCREMENT=89367 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_create`
--

LOCK TABLES `web_create` WRITE;
/*!40000 ALTER TABLE `web_create` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_create` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_delete`
--

DROP TABLE IF EXISTS `web_delete`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_delete` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `user` varchar(25) NOT NULL,
  `owner` int(6) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=11249 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_delete`
--

LOCK TABLES `web_delete` WRITE;
/*!40000 ALTER TABLE `web_delete` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_delete` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_highscores`
--

DROP TABLE IF EXISTS `web_highscores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_highscores` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(25) CHARACTER SET latin1 NOT NULL,
  `owner` int(6) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) CHARACTER SET latin1 NOT NULL DEFAULT '0.0.0.0',
  `hs_pref` varchar(255) CHARACTER SET latin1 NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `owner` (`owner`),
  KEY `ip` (`ip`),
  KEY `hs_pref` (`hs_pref`)
) ENGINE=MyISAM AUTO_INCREMENT=43586 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_highscores`
--

LOCK TABLES `web_highscores` WRITE;
/*!40000 ALTER TABLE `web_highscores` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_highscores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_inv_wipe_recovery`
--

DROP TABLE IF EXISTS `web_inv_wipe_recovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_inv_wipe_recovery` (
  `user` varchar(40) NOT NULL,
  `id` smallint(4) NOT NULL,
  `amount` int(10) NOT NULL DEFAULT '1',
  `wielded` tinyint(1) NOT NULL DEFAULT '0',
  `slot` tinyint(2) NOT NULL,
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_inv_wipe_recovery`
--

LOCK TABLES `web_inv_wipe_recovery` WRITE;
/*!40000 ALTER TABLE `web_inv_wipe_recovery` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_inv_wipe_recovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_legacy_transfer`
--

DROP TABLE IF EXISTS `web_legacy_transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_legacy_transfer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `transfer_date` varchar(20) NOT NULL,
  `transfer_ip` varchar(20) NOT NULL,
  `transfer_session_id` varchar(255) NOT NULL,
  `transferStartTime` varchar(20) NOT NULL,
  `transferAuthMethod` varchar(50) NOT NULL,
  `transferGameVersion` varchar(50) NOT NULL,
  `transferCharacterId` varchar(100) DEFAULT NULL,
  `transferForumId` varchar(100) DEFAULT NULL,
  `newOwnerId` varchar(100) NOT NULL,
  `characterToTransferId` varchar(100) NOT NULL,
  `newUsername` varchar(255) NOT NULL,
  `newEncodedUsername` varchar(255) NOT NULL,
  `transferKey` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_legacy_transfer`
--

LOCK TABLES `web_legacy_transfer` WRITE;
/*!40000 ALTER TABLE `web_legacy_transfer` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_legacy_transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_login`
--

DROP TABLE IF EXISTS `web_login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_login` (
  `ip` varchar(15) NOT NULL,
  `count` int(5) NOT NULL DEFAULT '0',
  KEY `ip` (`ip`),
  KEY `count` (`count`),
  KEY `ip_2` (`ip`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_login`
--

LOCK TABLES `web_login` WRITE;
/*!40000 ALTER TABLE `web_login` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_logins`
--

DROP TABLE IF EXISTS `web_logins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_logins` (
  `account` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_logins`
--

LOCK TABLES `web_logins` WRITE;
/*!40000 ALTER TABLE `web_logins` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_logins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_password`
--

DROP TABLE IF EXISTS `web_password`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_password` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(25) NOT NULL,
  `owner` int(6) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `password` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `owner` (`owner`),
  KEY `ip` (`ip`),
  KEY `password` (`password`)
) ENGINE=MyISAM AUTO_INCREMENT=14842 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_password`
--

LOCK TABLES `web_password` WRITE;
/*!40000 ALTER TABLE `web_password` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_password` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_recovery`
--

DROP TABLE IF EXISTS `web_recovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_recovery` (
  `account` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `date` int(10) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `account` (`account`,`ip`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_recovery`
--

LOCK TABLES `web_recovery` WRITE;
/*!40000 ALTER TABLE `web_recovery` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_recovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_reduce`
--

DROP TABLE IF EXISTS `web_reduce`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_reduce` (
  `user` bigint(23) NOT NULL,
  `time` int(10) NOT NULL,
  `xp_before` int(10) NOT NULL,
  `xp_after` int(10) NOT NULL,
  `stat` varchar(20) NOT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `user` (`user`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_reduce`
--

LOCK TABLES `web_reduce` WRITE;
/*!40000 ALTER TABLE `web_reduce` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_reduce` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_rename`
--

DROP TABLE IF EXISTS `web_rename`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_rename` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `owner` int(10) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `old_hash` varchar(255) NOT NULL,
  `new_hash` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id` (`id`),
  KEY `ip` (`ip`)
) ENGINE=MyISAM AUTO_INCREMENT=2152 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_rename`
--

LOCK TABLES `web_rename` WRITE;
/*!40000 ALTER TABLE `web_rename` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_rename` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_rename_old`
--

DROP TABLE IF EXISTS `web_rename_old`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_rename_old` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL,
  `old` varchar(12) NOT NULL,
  `old_hash` bigint(20) NOT NULL,
  `new` varchar(12) NOT NULL,
  `new_hash` bigint(20) NOT NULL,
  `time` int(10) NOT NULL,
  `account` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `id` (`id`),
  KEY `user_id_2` (`user_id`),
  KEY `old` (`old`),
  KEY `old_hash` (`old_hash`),
  KEY `new` (`new`),
  KEY `new_hash` (`new_hash`),
  KEY `account` (`account`),
  KEY `ip` (`ip`)
) ENGINE=MyISAM AUTO_INCREMENT=1443 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_rename_old`
--

LOCK TABLES `web_rename_old` WRITE;
/*!40000 ALTER TABLE `web_rename_old` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_rename_old` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_staff_actions`
--

DROP TABLE IF EXISTS `web_staff_actions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_staff_actions` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `owner` int(10) NOT NULL,
  `user` varchar(40) NOT NULL,
  `staff_id` int(10) NOT NULL,
  `ip` varchar(15) CHARACTER SET latin1 NOT NULL DEFAULT '0.0.0.0',
  `action_type` varchar(255) CHARACTER SET latin1 NOT NULL,
  `action_date` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=9438 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_staff_actions`
--

LOCK TABLES `web_staff_actions` WRITE;
/*!40000 ALTER TABLE `web_staff_actions` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_staff_actions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_statistics`
--

DROP TABLE IF EXISTS `web_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_statistics` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `time` int(10) NOT NULL,
  `total_accounts` int(6) NOT NULL,
  `accounts_today` int(4) NOT NULL,
  `total_characters` int(6) NOT NULL,
  `characters_today` int(4) NOT NULL,
  `online` int(3) NOT NULL,
  `online_unique` int(4) NOT NULL,
  `online_today` int(4) NOT NULL,
  `online_today_unique` int(4) NOT NULL,
  `total_topics` int(6) NOT NULL,
  `topics_today` int(4) NOT NULL,
  `total_posts` int(6) NOT NULL,
  `posts_today` int(4) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `time` (`time`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_statistics`
--

LOCK TABLES `web_statistics` WRITE;
/*!40000 ALTER TABLE `web_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_transfer`
--

DROP TABLE IF EXISTS `web_transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_transfer` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `user` varchar(25) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `from` int(6) NOT NULL,
  `to` int(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1274 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_transfer`
--

LOCK TABLES `web_transfer` WRITE;
/*!40000 ALTER TABLE `web_transfer` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_v1transfer`
--

DROP TABLE IF EXISTS `web_v1transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_v1transfer` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `user` varchar(25) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `from` int(6) NOT NULL,
  `to` int(6) NOT NULL,
  `transfer_log` text NOT NULL,
  `new_username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=8271 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_v1transfer`
--

LOCK TABLES `web_v1transfer` WRITE;
/*!40000 ALTER TABLE `web_v1transfer` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_v1transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_visit`
--

DROP TABLE IF EXISTS `web_visit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_visit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `time` int(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ip` (`ip`),
  KEY `ip_2` (`ip`)
) ENGINE=MyISAM AUTO_INCREMENT=156415 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_visit`
--

LOCK TABLES `web_visit` WRITE;
/*!40000 ALTER TABLE `web_visit` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_visit` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-06 21:01:00
