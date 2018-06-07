-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: rscunity
-- ------------------------------------------------------
-- Server version	5.7.21

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
-- Table structure for table `bans`
--

DROP TABLE IF EXISTS `bans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bans` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(200) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `email` varchar(80) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `expire` int(10) unsigned DEFAULT NULL,
  `ban_creator` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `bans_username_idx` (`username`(25))
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bans`
--

LOCK TABLES `bans` WRITE;
/*!40000 ALTER TABLE `bans` DISABLE KEYS */;
/*!40000 ALTER TABLE `bans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `cat_name` varchar(80) NOT NULL DEFAULT 'New Category',
  `disp_position` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `censoring`
--

DROP TABLE IF EXISTS `censoring`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `censoring` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `search_for` varchar(60) NOT NULL DEFAULT '',
  `replace_with` varchar(60) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `censoring`
--

LOCK TABLES `censoring` WRITE;
/*!40000 ALTER TABLE `censoring` DISABLE KEYS */;
/*!40000 ALTER TABLE `censoring` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config` (
  `conf_name` varchar(255) NOT NULL DEFAULT '',
  `conf_value` text,
  PRIMARY KEY (`conf_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_perms`
--

DROP TABLE IF EXISTS `forum_perms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forum_perms` (
  `group_id` int(10) NOT NULL DEFAULT '0',
  `forum_id` int(10) NOT NULL DEFAULT '0',
  `read_forum` tinyint(1) NOT NULL DEFAULT '1',
  `post_replies` tinyint(1) NOT NULL DEFAULT '1',
  `post_topics` tinyint(1) NOT NULL DEFAULT '1',
  `post_polls` tinyint(1) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`group_id`,`forum_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_perms`
--

LOCK TABLES `forum_perms` WRITE;
/*!40000 ALTER TABLE `forum_perms` DISABLE KEYS */;
/*!40000 ALTER TABLE `forum_perms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_subscriptions`
--

DROP TABLE IF EXISTS `forum_subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forum_subscriptions` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `forum_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`forum_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_subscriptions`
--

LOCK TABLES `forum_subscriptions` WRITE;
/*!40000 ALTER TABLE `forum_subscriptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `forum_subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forums`
--

DROP TABLE IF EXISTS `forums`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forums` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `forum_name` varchar(80) NOT NULL DEFAULT 'New forum',
  `forum_desc` text,
  `redirect_url` varchar(100) DEFAULT NULL,
  `moderators` text,
  `num_topics` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `num_posts` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `last_post` int(10) unsigned DEFAULT NULL,
  `last_post_id` int(10) unsigned DEFAULT NULL,
  `last_poster` varchar(200) DEFAULT NULL,
  `sort_by` tinyint(1) NOT NULL DEFAULT '0',
  `disp_position` int(10) NOT NULL DEFAULT '0',
  `cat_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forums`
--

LOCK TABLES `forums` WRITE;
/*!40000 ALTER TABLE `forums` DISABLE KEYS */;
/*!40000 ALTER TABLE `forums` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `g_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `g_title` varchar(50) NOT NULL DEFAULT '',
  `g_user_title` varchar(50) DEFAULT NULL,
  `g_promote_min_posts` int(10) unsigned NOT NULL DEFAULT '0',
  `g_promote_next_group` int(10) unsigned NOT NULL DEFAULT '0',
  `g_moderator` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_edit_users` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_rename_users` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_change_passwords` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_ban_users` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_promote_users` tinyint(1) NOT NULL DEFAULT '0',
  `g_read_board` tinyint(1) NOT NULL DEFAULT '1',
  `g_view_users` tinyint(1) NOT NULL DEFAULT '1',
  `g_post_replies` tinyint(1) NOT NULL DEFAULT '1',
  `g_post_topics` tinyint(1) NOT NULL DEFAULT '1',
  `g_edit_posts` tinyint(1) NOT NULL DEFAULT '1',
  `g_delete_posts` tinyint(1) NOT NULL DEFAULT '1',
  `g_delete_topics` tinyint(1) NOT NULL DEFAULT '1',
  `g_post_links` tinyint(1) NOT NULL DEFAULT '1',
  `g_set_title` tinyint(1) NOT NULL DEFAULT '1',
  `g_search` tinyint(1) NOT NULL DEFAULT '1',
  `g_search_users` tinyint(1) NOT NULL DEFAULT '1',
  `g_send_email` tinyint(1) NOT NULL DEFAULT '1',
  `g_post_flood` smallint(6) NOT NULL DEFAULT '30',
  `g_search_flood` smallint(6) NOT NULL DEFAULT '30',
  `g_email_flood` smallint(6) NOT NULL DEFAULT '60',
  `g_report_flood` smallint(6) NOT NULL DEFAULT '60',
  `g_post_polls` smallint(5) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`g_id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `highscores`
--

DROP TABLE IF EXISTS `highscores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `highscores` (
  `user` bigint(20) NOT NULL,
  `username` varchar(12) NOT NULL,
  `owner` int(10) NOT NULL,
  `highscores` tinyint(1) DEFAULT NULL,
  `total_xp` bigint(20) NOT NULL,
  `total_xp_rank` int(10) NOT NULL,
  `combat` tinyint(3) DEFAULT NULL,
  `combat_rank` int(10) DEFAULT NULL,
  `skill_total` smallint(4) DEFAULT NULL,
  `skill_total_rank` int(10) DEFAULT NULL,
  `attack` int(9) DEFAULT NULL,
  `attack_rank` int(10) DEFAULT NULL,
  `defense` int(9) DEFAULT NULL,
  `defense_rank` int(10) DEFAULT NULL,
  `strength` int(9) DEFAULT NULL,
  `strength_rank` int(10) DEFAULT NULL,
  `hits` int(9) DEFAULT NULL,
  `hits_rank` int(10) DEFAULT NULL,
  `ranged` int(9) DEFAULT NULL,
  `ranged_rank` int(10) DEFAULT NULL,
  `prayer` int(9) DEFAULT NULL,
  `prayer_rank` int(10) DEFAULT NULL,
  `magic` int(9) DEFAULT NULL,
  `magic_rank` int(10) DEFAULT NULL,
  `cooking` int(9) DEFAULT NULL,
  `cooking_rank` int(10) DEFAULT NULL,
  `woodcut` int(9) DEFAULT NULL,
  `woodcut_rank` int(10) DEFAULT NULL,
  `fletching` int(9) DEFAULT NULL,
  `fletching_rank` int(10) DEFAULT NULL,
  `fishing` int(9) DEFAULT NULL,
  `fishing_rank` int(10) DEFAULT NULL,
  `firemaking` int(9) DEFAULT NULL,
  `firemaking_rank` int(10) DEFAULT NULL,
  `crafting` int(9) DEFAULT NULL,
  `crafting_rank` int(10) DEFAULT NULL,
  `smithing` int(9) DEFAULT NULL,
  `smithing_rank` int(10) DEFAULT NULL,
  `mining` int(9) DEFAULT NULL,
  `mining_rank` int(10) DEFAULT NULL,
  `herblaw` int(9) DEFAULT NULL,
  `herblaw_rank` int(10) DEFAULT NULL,
  `agility` int(9) DEFAULT NULL,
  `agility_rank` int(10) DEFAULT NULL,
  `thieving` int(9) DEFAULT NULL,
  `thieving_rank` int(10) DEFAULT NULL,
  `runecrafting` int(9) DEFAULT NULL,
  `runecrafting_rank` int(10) DEFAULT NULL,
  `kills` int(9) DEFAULT NULL,
  `kills_rank` int(10) DEFAULT NULL,
  `deaths` int(9) DEFAULT NULL,
  `deaths_rank` int(10) DEFAULT NULL,
  KEY `user` (`user`,`owner`),
  KEY `total_xp` (`total_xp`),
  KEY `combat` (`combat`),
  KEY `skill_total` (`skill_total`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `highscores`
--

LOCK TABLES `highscores` WRITE;
/*!40000 ALTER TABLE `highscores` DISABLE KEYS */;
/*!40000 ALTER TABLE `highscores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messages` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `shared_id` int(10) NOT NULL DEFAULT '0',
  `last_shared_id` int(10) NOT NULL DEFAULT '0',
  `last_post` int(10) DEFAULT '0',
  `last_post_id` int(10) DEFAULT '0',
  `last_poster` varchar(255) NOT NULL DEFAULT '0',
  `owner` int(10) NOT NULL DEFAULT '0',
  `subject` varchar(120) NOT NULL DEFAULT '',
  `message` text,
  `sender` varchar(200) NOT NULL DEFAULT '',
  `receiver` varchar(200) DEFAULT NULL,
  `sender_id` int(10) NOT NULL DEFAULT '0',
  `receiver_id` varchar(255) DEFAULT '0',
  `posted` int(10) NOT NULL DEFAULT '0',
  `sender_ip` varchar(120) NOT NULL DEFAULT '0.0.0.0',
  `hide_smilies` tinyint(1) NOT NULL DEFAULT '0',
  `show_message` tinyint(1) NOT NULL DEFAULT '0',
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `showed` tinyint(1) NOT NULL DEFAULT '0',
  `popup` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `owner` (`owner`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `online`
--

DROP TABLE IF EXISTS `online`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `online` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '1',
  `ident` varchar(200) NOT NULL DEFAULT '',
  `logged` int(10) unsigned NOT NULL DEFAULT '0',
  `idle` tinyint(1) NOT NULL DEFAULT '0',
  `last_post` int(10) unsigned DEFAULT NULL,
  `last_search` int(10) unsigned DEFAULT NULL,
  UNIQUE KEY `online_user_id_ident_idx` (`user_id`,`ident`(25)),
  KEY `online_ident_idx` (`ident`(25)),
  KEY `online_logged_idx` (`logged`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `online`
--

LOCK TABLES `online` WRITE;
/*!40000 ALTER TABLE `online` DISABLE KEYS */;
/*!40000 ALTER TABLE `online` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `polls`
--

DROP TABLE IF EXISTS `polls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `polls` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pollid` int(10) unsigned NOT NULL DEFAULT '0',
  `options` text NOT NULL,
  `voters` text,
  `ptype` tinyint(4) NOT NULL DEFAULT '0',
  `votes` text,
  `created` int(10) unsigned NOT NULL DEFAULT '0',
  `edited` int(10) unsigned DEFAULT NULL,
  `edited_by` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `polls_pollid_idx` (`pollid`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `polls`
--

LOCK TABLES `polls` WRITE;
/*!40000 ALTER TABLE `polls` DISABLE KEYS */;
/*!40000 ALTER TABLE `polls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `posts` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `poster` varchar(200) NOT NULL DEFAULT '',
  `poster_id` int(10) unsigned NOT NULL DEFAULT '1',
  `poster_ip` varchar(39) DEFAULT NULL,
  `poster_email` varchar(80) DEFAULT NULL,
  `message` mediumtext,
  `hide_smilies` tinyint(1) NOT NULL DEFAULT '0',
  `posted` int(10) unsigned NOT NULL DEFAULT '0',
  `edited` int(10) unsigned DEFAULT NULL,
  `edited_by` varchar(200) DEFAULT NULL,
  `topic_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `posts_topic_id_idx` (`topic_id`),
  KEY `posts_multi_idx` (`poster_id`,`topic_id`)
) ENGINE=MyISAM AUTO_INCREMENT=743 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recovery_questions`
--

DROP TABLE IF EXISTS `recovery_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recovery_questions` (
  `account` int(10) NOT NULL,
  `question1` varchar(70) NOT NULL,
  `question2` varchar(70) NOT NULL,
  `question3` varchar(70) NOT NULL,
  `answer1` varchar(70) NOT NULL,
  `answer2` varchar(70) NOT NULL,
  `answer3` varchar(70) NOT NULL,
  `time` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `set` tinyint(1) NOT NULL DEFAULT '0',
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recovery_questions`
--

LOCK TABLES `recovery_questions` WRITE;
/*!40000 ALTER TABLE `recovery_questions` DISABLE KEYS */;
/*!40000 ALTER TABLE `recovery_questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reports` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `post_id` int(10) unsigned NOT NULL DEFAULT '0',
  `topic_id` int(10) unsigned NOT NULL DEFAULT '0',
  `forum_id` int(10) unsigned NOT NULL DEFAULT '0',
  `reported_by` int(10) unsigned NOT NULL DEFAULT '0',
  `created` int(10) unsigned NOT NULL DEFAULT '0',
  `message` text,
  `zapped` int(10) unsigned DEFAULT NULL,
  `zapped_by` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `reports_zapped_idx` (`zapped`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rscd_auctions`
--

DROP TABLE IF EXISTS `rscd_auctions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rscd_auctions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player` bigint(18) NOT NULL,
  `owner` int(5) NOT NULL,
  `state` tinyint(1) NOT NULL,
  `item` int(5) NOT NULL,
  `amount` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  `expiration` int(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_auctions`
--

LOCK TABLES `rscd_auctions` WRITE;
/*!40000 ALTER TABLE `rscd_auctions` DISABLE KEYS */;
/*!40000 ALTER TABLE `rscd_auctions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rscd_bank`
--

DROP TABLE IF EXISTS `rscd_bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rscd_bank` (
  `owner` int(6) NOT NULL,
  `id` smallint(4) NOT NULL,
  `amount` int(10) NOT NULL DEFAULT '1',
  `slot` smallint(3) NOT NULL,
  KEY `owner` (`owner`),
  KEY `id` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_bank`
--

LOCK TABLES `rscd_bank` WRITE;
/*!40000 ALTER TABLE `rscd_bank` DISABLE KEYS */;
INSERT INTO `rscd_bank` VALUES (1,703,1,38),(1,702,1,37),(1,1216,1,36),(1,1278,1,35),(1,1263,1,34),(1,1288,1,33),(1,1119,1,32),(1,581,1,31),(1,580,1,30),(1,579,1,29),(1,578,1,28),(1,577,1,27),(1,576,1,26),(1,828,1,25),(1,832,1,24),(1,831,1,23),(1,981,1,22),(1,766,1,21),(1,656,1,20),(1,647,9999,19),(1,573,1,18),(1,525,1,17),(1,387,1,16),(1,1041,99,15),(1,1035,1,14),(1,1034,1,13),(1,1033,1,12),(1,1032,1,11),(1,1093,1,10),(1,1095,1,9),(1,1096,1,8),(1,422,1,7),(1,795,1,6),(1,1289,1,5),(1,575,2,4),(1,609,1,3),(1,16,1,2),(1,81,1,1),(1,10,100939654,0);
/*!40000 ALTER TABLE `rscd_bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rscd_curstats`
--

DROP TABLE IF EXISTS `rscd_curstats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rscd_curstats` (
  `user` varchar(40) NOT NULL,
  `cur_attack` smallint(3) NOT NULL DEFAULT '1',
  `cur_defense` smallint(3) NOT NULL DEFAULT '1',
  `cur_strength` smallint(3) NOT NULL DEFAULT '1',
  `cur_hits` tinyint(3) NOT NULL DEFAULT '10',
  `cur_ranged` tinyint(3) NOT NULL DEFAULT '1',
  `cur_prayer` tinyint(3) NOT NULL DEFAULT '1',
  `cur_magic` tinyint(3) NOT NULL DEFAULT '1',
  `cur_cooking` tinyint(3) NOT NULL DEFAULT '1',
  `cur_woodcut` tinyint(3) NOT NULL DEFAULT '1',
  `cur_fletching` tinyint(3) NOT NULL DEFAULT '1',
  `cur_fishing` tinyint(3) NOT NULL DEFAULT '1',
  `cur_firemaking` tinyint(3) NOT NULL DEFAULT '1',
  `cur_crafting` tinyint(3) NOT NULL DEFAULT '1',
  `cur_smithing` tinyint(3) NOT NULL DEFAULT '1',
  `cur_mining` tinyint(3) NOT NULL DEFAULT '1',
  `cur_herblaw` tinyint(3) NOT NULL DEFAULT '1',
  `cur_agility` tinyint(3) NOT NULL DEFAULT '1',
  `cur_thieving` tinyint(3) NOT NULL DEFAULT '1',
  `cur_runecrafting` tinyint(3) NOT NULL DEFAULT '1',
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user` (`user`)
) ENGINE=MyISAM AUTO_INCREMENT=1338 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_curstats`
--

LOCK TABLES `rscd_curstats` WRITE;
/*!40000 ALTER TABLE `rscd_curstats` DISABLE KEYS */;
INSERT INTO `rscd_curstats` VALUES ('51697882930',114,114,115,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,1);
/*!40000 ALTER TABLE `rscd_curstats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rscd_experience`
--

DROP TABLE IF EXISTS `rscd_experience`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rscd_experience` (
  `user` varchar(40) NOT NULL,
  `exp_attack` int(9) NOT NULL DEFAULT '0',
  `exp_defense` int(9) NOT NULL DEFAULT '0',
  `exp_strength` int(9) NOT NULL DEFAULT '0',
  `exp_hits` int(9) NOT NULL DEFAULT '1154',
  `exp_ranged` int(9) NOT NULL DEFAULT '0',
  `exp_prayer` int(9) NOT NULL DEFAULT '0',
  `exp_magic` int(9) NOT NULL DEFAULT '0',
  `exp_cooking` int(9) NOT NULL DEFAULT '0',
  `exp_woodcut` int(9) NOT NULL DEFAULT '0',
  `exp_fletching` int(9) NOT NULL DEFAULT '0',
  `exp_fishing` int(9) NOT NULL DEFAULT '0',
  `exp_firemaking` int(9) NOT NULL DEFAULT '0',
  `exp_crafting` int(9) NOT NULL DEFAULT '0',
  `exp_smithing` int(9) NOT NULL DEFAULT '0',
  `exp_mining` int(9) NOT NULL DEFAULT '0',
  `exp_herblaw` int(9) NOT NULL DEFAULT '0',
  `exp_agility` int(9) NOT NULL DEFAULT '0',
  `exp_thieving` int(9) NOT NULL DEFAULT '0',
  `exp_runecrafting` int(9) NOT NULL DEFAULT '0',
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `attack_rank` int(10) DEFAULT NULL,
  `defense_rank` int(10) DEFAULT NULL,
  `strength_rank` int(10) DEFAULT NULL,
  `hits_rank` int(10) DEFAULT NULL,
  `ranged_rank` int(10) DEFAULT NULL,
  `magic_rank` int(10) DEFAULT NULL,
  `prayer_rank` int(10) DEFAULT NULL,
  `cooking_rank` int(10) DEFAULT NULL,
  `woodcut_rank` int(10) DEFAULT NULL,
  `fletching_rank` int(10) DEFAULT NULL,
  `fishing_rank` int(10) DEFAULT NULL,
  `firemaking_rank` int(10) DEFAULT NULL,
  `crafting_rank` int(10) DEFAULT NULL,
  `smithing_rank` int(10) DEFAULT NULL,
  `mining_rank` int(10) DEFAULT NULL,
  `herblaw_rank` int(10) DEFAULT NULL,
  `agility_rank` int(10) DEFAULT NULL,
  `thieving_rank` int(10) DEFAULT NULL,
  `runecrafting_rank` int(10) DEFAULT NULL,
  `total_xp_rank` int(10) DEFAULT NULL,
  `total_xp` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `exp_attack` (`exp_attack`),
  KEY `exp_defense` (`exp_defense`),
  KEY `exp_strength` (`exp_strength`),
  KEY `exp_hits` (`exp_hits`),
  KEY `exp_ranged` (`exp_ranged`),
  KEY `exp_prayer` (`exp_prayer`),
  KEY `exp_magic` (`exp_magic`),
  KEY `exp_cooking` (`exp_cooking`),
  KEY `exp_woodcut` (`exp_woodcut`),
  KEY `exp_fletching` (`exp_fletching`),
  KEY `exp_fishing` (`exp_fishing`),
  KEY `exp_firemaking` (`exp_firemaking`),
  KEY `exp_crafting` (`exp_crafting`),
  KEY `exp_smithing` (`exp_smithing`),
  KEY `exp_mining` (`exp_mining`),
  KEY `exp_herblaw` (`exp_herblaw`),
  KEY `exp_agility` (`exp_agility`),
  KEY `exp_thieving` (`exp_thieving`),
  KEY `exp_runecrafting` (`exp_runecrafting`),
  KEY `exp_total` (`total_xp`),
  KEY `user` (`user`)
) ENGINE=MyISAM AUTO_INCREMENT=1338 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_experience`
--

LOCK TABLES `rscd_experience` WRITE;
/*!40000 ALTER TABLE `rscd_experience` DISABLE KEYS */;
INSERT INTO `rscd_experience` VALUES ('51697882930',13044835,13044835,13044835,13044835,13038319,13034557,13034431,13034431,13034431,13034431,13034431,13034431,13034431,13034431,13034431,13034797,13034431,13034431,13034431,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `rscd_experience` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rscd_friends`
--

DROP TABLE IF EXISTS `rscd_friends`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rscd_friends` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user` varchar(40) NOT NULL,
  `friend` varchar(40) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `friend` (`friend`)
) ENGINE=MyISAM AUTO_INCREMENT=2359406 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_friends`
--

LOCK TABLES `rscd_friends` WRITE;
/*!40000 ALTER TABLE `rscd_friends` DISABLE KEYS */;
/*!40000 ALTER TABLE `rscd_friends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rscd_ignores`
--

DROP TABLE IF EXISTS `rscd_ignores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rscd_ignores` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user` varchar(40) NOT NULL,
  `ignore` varchar(40) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `ignore` (`ignore`)
) ENGINE=MyISAM AUTO_INCREMENT=6943 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_ignores`
--

LOCK TABLES `rscd_ignores` WRITE;
/*!40000 ALTER TABLE `rscd_ignores` DISABLE KEYS */;
/*!40000 ALTER TABLE `rscd_ignores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rscd_invitems`
--

DROP TABLE IF EXISTS `rscd_invitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rscd_invitems` (
  `user` varchar(40) NOT NULL,
  `id` smallint(4) NOT NULL,
  `amount` int(10) NOT NULL DEFAULT '1',
  `wielded` tinyint(1) NOT NULL DEFAULT '0',
  `slot` tinyint(2) NOT NULL,
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_invitems`
--

LOCK TABLES `rscd_invitems` WRITE;
/*!40000 ALTER TABLE `rscd_invitems` DISABLE KEYS */;
INSERT INTO `rscd_invitems` VALUES ('51697882930',401,1,1,5),('51697882930',402,1,1,4),('51697882930',594,1,1,3),('51697882930',1213,1,1,2),('51697882930',971,1,1,1),('51697882930',597,1,1,0);
/*!40000 ALTER TABLE `rscd_invitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rscd_players`
--

DROP TABLE IF EXISTS `rscd_players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rscd_players` (
  `user` varchar(40) NOT NULL,
  `username` varchar(12) NOT NULL,
  `group_id` tinyint(1) NOT NULL DEFAULT '4',
  `owner` int(5) NOT NULL,
  `combat` tinyint(3) NOT NULL DEFAULT '3',
  `combat_rank` int(10) DEFAULT NULL,
  `skill_total` smallint(4) NOT NULL DEFAULT '27',
  `skill_total_rank` int(10) DEFAULT NULL,
  `x` smallint(4) NOT NULL DEFAULT '225',
  `y` smallint(4) NOT NULL DEFAULT '447',
  `fatigue` tinyint(3) NOT NULL DEFAULT '0',
  `combatstyle` tinyint(1) NOT NULL DEFAULT '0',
  `block_chat` tinyint(1) NOT NULL DEFAULT '0',
  `block_private` tinyint(1) NOT NULL DEFAULT '0',
  `block_trade` tinyint(1) NOT NULL DEFAULT '0',
  `block_duel` tinyint(1) NOT NULL DEFAULT '0',
  `block_global` tinyint(1) NOT NULL DEFAULT '1',
  `cameraauto` tinyint(1) NOT NULL DEFAULT '1',
  `onemouse` tinyint(1) NOT NULL DEFAULT '0',
  `soundoff` tinyint(1) NOT NULL DEFAULT '0',
  `showroof` tinyint(1) NOT NULL DEFAULT '0',
  `autoscreenshot` tinyint(1) NOT NULL DEFAULT '0',
  `combatwindow` tinyint(1) NOT NULL DEFAULT '1',
  `haircolour` tinyint(1) NOT NULL DEFAULT '2',
  `topcolour` tinyint(2) NOT NULL DEFAULT '8',
  `trousercolour` tinyint(2) NOT NULL DEFAULT '14',
  `skincolour` tinyint(1) NOT NULL DEFAULT '0',
  `headsprite` tinyint(1) NOT NULL DEFAULT '1',
  `bodysprite` tinyint(1) NOT NULL DEFAULT '2',
  `male` tinyint(1) NOT NULL DEFAULT '1',
  `skulled` int(10) NOT NULL DEFAULT '0',
  `pass` char(32) DEFAULT NULL,
  `password` char(128) DEFAULT NULL,
  `password_salt` char(30) DEFAULT NULL,
  `password_x` varchar(255) NOT NULL,
  `creation_date` int(10) NOT NULL DEFAULT '0',
  `creation_ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `login_date` int(10) NOT NULL DEFAULT '0',
  `logout_date` bigint(10) NOT NULL,
  `death_time` bigint(10) NOT NULL,
  `login_ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `deaths` smallint(5) NOT NULL DEFAULT '0',
  `deaths_rank` int(10) DEFAULT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `online` tinyint(1) NOT NULL DEFAULT '0',
  `kills` smallint(5) NOT NULL DEFAULT '0',
  `kills_rank` int(10) DEFAULT NULL,
  `highscores` tinyint(1) NOT NULL DEFAULT '0',
  `killnotify` tinyint(1) NOT NULL DEFAULT '0',
  `store_employee` tinyint(1) NOT NULL DEFAULT '0',
  `draynor_hopper` tinyint(1) NOT NULL DEFAULT '0',
  `guild_hopper` tinyint(1) NOT NULL DEFAULT '0',
  `banana_job` tinyint(1) NOT NULL DEFAULT '0',
  `bananas_in_crate` tinyint(1) NOT NULL DEFAULT '0',
  `rum_in_karamja_crate` tinyint(1) NOT NULL DEFAULT '0',
  `rum_in_sarim_crate` tinyint(1) NOT NULL DEFAULT '0',
  `has_traiborn_key` tinyint(1) NOT NULL DEFAULT '0',
  `collecting_bones` tinyint(1) NOT NULL DEFAULT '0',
  `bones` tinyint(2) NOT NULL DEFAULT '0',
  `balls_of_wool` tinyint(2) NOT NULL DEFAULT '0',
  `killed_skeleton` tinyint(1) NOT NULL DEFAULT '0',
  `recieved_key_payment` tinyint(1) NOT NULL DEFAULT '0',
  `delete_date` int(10) NOT NULL DEFAULT '0',
  `lever_A_down` tinyint(1) NOT NULL DEFAULT '0',
  `lever_B_down` tinyint(1) NOT NULL DEFAULT '0',
  `lever_C_down` tinyint(1) NOT NULL DEFAULT '0',
  `lever_D_down` tinyint(1) NOT NULL DEFAULT '0',
  `lever_E_down` tinyint(1) NOT NULL DEFAULT '0',
  `lever_F_down` tinyint(1) NOT NULL DEFAULT '0',
  `leela_has_key` tinyint(1) NOT NULL DEFAULT '0',
  `tutstage` int(1) NOT NULL DEFAULT '0',
  `on_crandor` tinyint(1) NOT NULL DEFAULT '0',
  `lady_patches` int(1) NOT NULL DEFAULT '0',
  `has_map_piece` int(1) NOT NULL DEFAULT '0',
  `cannon_x` int(4) NOT NULL DEFAULT '-1',
  `cannon_y` int(4) NOT NULL DEFAULT '-1',
  `cannon_stage` int(1) NOT NULL DEFAULT '-1',
  `railing1` tinyint(1) NOT NULL DEFAULT '0',
  `railing2` tinyint(1) NOT NULL DEFAULT '0',
  `railing3` tinyint(1) NOT NULL DEFAULT '0',
  `railing4` tinyint(1) NOT NULL DEFAULT '0',
  `railing5` tinyint(1) NOT NULL DEFAULT '0',
  `railing6` tinyint(1) NOT NULL DEFAULT '0',
  `barrel` tinyint(1) NOT NULL DEFAULT '0',
  `axle` tinyint(1) NOT NULL DEFAULT '0',
  `shaft` tinyint(1) NOT NULL DEFAULT '0',
  `pipe` tinyint(1) NOT NULL DEFAULT '0',
  `poison` tinyint(2) NOT NULL DEFAULT '0',
  `avatar_items` varchar(255) DEFAULT NULL,
  `avatar` blob,
  `quests` longblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user` (`user`),
  UNIQUE KEY `password` (`password`),
  UNIQUE KEY `password_salt` (`password_salt`),
  KEY `pass` (`pass`),
  KEY `group_id` (`group_id`),
  KEY `owner` (`owner`),
  KEY `online` (`online`),
  KEY `combat` (`combat`),
  KEY `skill_total` (`skill_total`),
  KEY `login_ip` (`login_ip`),
  KEY `combat_rank` (`combat_rank`),
  KEY `skill_total_rank` (`skill_total_rank`),
  KEY `kills_rank` (`kills_rank`),
  KEY `deaths_rank` (`deaths_rank`),
  KEY `login_date` (`login_date`),
  KEY `delete_date` (`delete_date`),
  KEY `creation_date` (`creation_date`),
  KEY `avatar_items` (`avatar_items`)
) ENGINE=MyISAM AUTO_INCREMENT=1338 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 9216 kB';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_players`
--

LOCK TABLES `rscd_players` WRITE;
/*!40000 ALTER TABLE `rscd_players` DISABLE KEYS */;
INSERT INTO `rscd_players` VALUES ('51697882930','testing',1,1,123,NULL,1881,NULL,215,452,40,0,0,0,0,0,1,1,0,1,1,0,2,3,14,7,1,1,2,1,0,NULL,NULL,NULL,'0',0,'0.0.0.0',1528107597,0,0,'127.0.0.1',0,NULL,1,0,0,NULL,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,0,0,0,0,0,0,0,0,0,0,0,NULL,NULL,'¬\í\0sr\0java.util.TreeMapÁö>-%j\æ\0L\0\ncomparatort\0Ljava/util/Comparator;xppw\0\0\0\0x');
/*!40000 ALTER TABLE `rscd_players` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rscd_quests`
--

DROP TABLE IF EXISTS `rscd_quests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rscd_quests` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user` varchar(30) NOT NULL,
  `quest_id` tinyint(3) NOT NULL,
  `quest_stage` tinyint(3) NOT NULL,
  `finished` tinyint(1) NOT NULL DEFAULT '0',
  `quest_points` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `rscd_quests_user` (`user`)
) ENGINE=MyISAM AUTO_INCREMENT=341 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_quests`
--

LOCK TABLES `rscd_quests` WRITE;
/*!40000 ALTER TABLE `rscd_quests` DISABLE KEYS */;
/*!40000 ALTER TABLE `rscd_quests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `search_cache`
--

DROP TABLE IF EXISTS `search_cache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_cache` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `ident` varchar(200) NOT NULL DEFAULT '',
  `search_data` mediumtext,
  PRIMARY KEY (`id`),
  KEY `search_cache_ident_idx` (`ident`(8))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `search_cache`
--

LOCK TABLES `search_cache` WRITE;
/*!40000 ALTER TABLE `search_cache` DISABLE KEYS */;
/*!40000 ALTER TABLE `search_cache` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `search_matches`
--

DROP TABLE IF EXISTS `search_matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_matches` (
  `post_id` int(10) unsigned NOT NULL DEFAULT '0',
  `word_id` int(10) unsigned NOT NULL DEFAULT '0',
  `subject_match` tinyint(1) NOT NULL DEFAULT '0',
  KEY `search_matches_word_id_idx` (`word_id`),
  KEY `search_matches_post_id_idx` (`post_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `search_matches`
--

LOCK TABLES `search_matches` WRITE;
/*!40000 ALTER TABLE `search_matches` DISABLE KEYS */;
/*!40000 ALTER TABLE `search_matches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `search_words`
--

DROP TABLE IF EXISTS `search_words`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_words` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `word` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`word`),
  KEY `search_words_id_idx` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4989 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `search_words`
--

LOCK TABLES `search_words` WRITE;
/*!40000 ALTER TABLE `search_words` DISABLE KEYS */;
/*!40000 ALTER TABLE `search_words` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teleport_locations`
--

DROP TABLE IF EXISTS `teleport_locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `teleport_locations` (
  `id` int(10) NOT NULL,
  `x` int(5) NOT NULL,
  `y` int(5) NOT NULL,
  `description` varchar(255) NOT NULL,
  `command` varchar(255) NOT NULL,
  `added_by` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teleport_locations`
--

LOCK TABLES `teleport_locations` WRITE;
/*!40000 ALTER TABLE `teleport_locations` DISABLE KEYS */;
INSERT INTO `teleport_locations` VALUES (1,226,447,'Edgeville','edgeville','Marwolf'),(2,110,508,'Varrock','varrock','Marwolf'),(3,122,648,'Lumbridge','lumbridge','Marwolf'),(4,214,632,'Draynor','draynor','Marwolf'),(5,291,636,'Falador','falador','Marwolf'),(6,438,500,'Catherby','catherby','Marwolf'),(7,549,594,'Ardougne','ardy','Marwolf'),(8,793,24,'Jail','jail','Marwolf'),(9,70,1640,'Modroom','modroom','Marwolf'),(10,1,3456,'Blackhole','blackhole','Marwolf'),(11,360,696,'Karamja','karamja','Marwolf'),(12,72,696,'Al Kharid','al','Marwolf'),(13,516,460,'Seers Village','seers','Marwolf'),(14,587,761,'Yanille','yanille','Marwolf');
/*!40000 ALTER TABLE `teleport_locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `topic_subscriptions`
--

DROP TABLE IF EXISTS `topic_subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `topic_subscriptions` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `topic_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`topic_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `topic_subscriptions`
--

LOCK TABLES `topic_subscriptions` WRITE;
/*!40000 ALTER TABLE `topic_subscriptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `topic_subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `topics`
--

DROP TABLE IF EXISTS `topics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `topics` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `poster` varchar(200) NOT NULL DEFAULT '',
  `subject` varchar(255) NOT NULL DEFAULT '',
  `posted` int(10) unsigned NOT NULL DEFAULT '0',
  `first_post_id` int(10) unsigned NOT NULL DEFAULT '0',
  `last_post` int(10) unsigned NOT NULL DEFAULT '0',
  `last_post_id` int(10) unsigned NOT NULL DEFAULT '0',
  `last_poster` varchar(200) DEFAULT NULL,
  `num_views` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `num_replies` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `closed` tinyint(1) NOT NULL DEFAULT '0',
  `sticky` tinyint(1) NOT NULL DEFAULT '0',
  `moved_to` int(10) unsigned DEFAULT NULL,
  `forum_id` int(10) unsigned NOT NULL DEFAULT '0',
  `question` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `topics_forum_id_idx` (`forum_id`),
  KEY `topics_moved_to_idx` (`moved_to`),
  KEY `topics_last_post_idx` (`last_post`),
  KEY `topics_first_post_id_idx` (`first_post_id`)
) ENGINE=MyISAM AUTO_INCREMENT=184 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `topics`
--

LOCK TABLES `topics` WRITE;
/*!40000 ALTER TABLE `topics` DISABLE KEYS */;
/*!40000 ALTER TABLE `topics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transactions` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `payer_status` varchar(255) NOT NULL,
  `payment_status` varchar(255) NOT NULL,
  `payer_email` varchar(255) NOT NULL,
  `payer_id` varchar(255) NOT NULL,
  `address_name` varchar(255) NOT NULL,
  `address_country` varchar(255) NOT NULL,
  `address_status` varchar(255) NOT NULL,
  `quantity` int(10) NOT NULL,
  `mc_gross` varchar(255) NOT NULL,
  `txn_id` varchar(255) NOT NULL,
  `txn_type` varchar(255) NOT NULL,
  `date` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` int(10) unsigned NOT NULL DEFAULT '3',
  `username` varchar(200) NOT NULL DEFAULT '',
  `password` varchar(40) NOT NULL DEFAULT '',
  `email` varchar(80) NOT NULL DEFAULT '',
  `title` varchar(50) DEFAULT NULL,
  `realname` varchar(40) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  `jabber` varchar(80) DEFAULT NULL,
  `icq` varchar(12) DEFAULT NULL,
  `msn` varchar(80) DEFAULT NULL,
  `aim` varchar(30) DEFAULT NULL,
  `yahoo` varchar(30) DEFAULT NULL,
  `location` varchar(30) DEFAULT NULL,
  `signature` text,
  `disp_topics` tinyint(3) unsigned DEFAULT NULL,
  `disp_posts` tinyint(3) unsigned DEFAULT NULL,
  `email_setting` tinyint(1) NOT NULL DEFAULT '1',
  `notify_with_post` tinyint(1) NOT NULL DEFAULT '0',
  `auto_notify` tinyint(1) NOT NULL DEFAULT '0',
  `show_smilies` tinyint(1) NOT NULL DEFAULT '1',
  `show_img` tinyint(1) NOT NULL DEFAULT '1',
  `show_img_sig` tinyint(1) NOT NULL DEFAULT '1',
  `show_avatars` tinyint(1) NOT NULL DEFAULT '1',
  `show_sig` tinyint(1) NOT NULL DEFAULT '1',
  `timezone` float NOT NULL DEFAULT '0',
  `dst` tinyint(1) NOT NULL DEFAULT '0',
  `time_format` tinyint(1) NOT NULL DEFAULT '0',
  `date_format` tinyint(1) NOT NULL DEFAULT '0',
  `language` varchar(25) NOT NULL DEFAULT 'English',
  `style` varchar(25) NOT NULL DEFAULT 'Air',
  `num_posts` int(10) unsigned NOT NULL DEFAULT '0',
  `last_post` int(10) unsigned DEFAULT NULL,
  `last_search` int(10) unsigned DEFAULT NULL,
  `last_email_sent` int(10) unsigned DEFAULT NULL,
  `last_report_sent` int(10) unsigned DEFAULT NULL,
  `registered` int(10) unsigned NOT NULL DEFAULT '0',
  `registration_ip` varchar(39) NOT NULL DEFAULT '0.0.0.0',
  `last_visit` int(10) unsigned NOT NULL DEFAULT '0',
  `admin_note` varchar(30) DEFAULT NULL,
  `activate_string` varchar(80) DEFAULT NULL,
  `activate_key` varchar(8) DEFAULT NULL,
  `sub_expires` int(10) NOT NULL DEFAULT '0',
  `sub_due` int(10) NOT NULL DEFAULT '0',
  `sub_remind` int(10) NOT NULL DEFAULT '0',
  `sub_ipn` int(10) DEFAULT NULL,
  `sub_given` int(10) DEFAULT NULL,
  `character_limit` int(3) NOT NULL DEFAULT '5',
  `muted` int(1) DEFAULT '0',
  `banned` int(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_username_idx` (`username`(25)),
  KEY `users_registered_idx` (`registered`)
) ENGINE=MyISAM AUTO_INCREMENT=799 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,0,'testing','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,0,1,1,1,1,1,0,0,0,0,'English','Air',0,NULL,NULL,NULL,NULL,0,'0.0.0.0',0,NULL,NULL,NULL,0,0,0,NULL,NULL,5,0,0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-06 21:00:26
