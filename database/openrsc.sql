-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: openrsc
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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
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
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (8,'General',0);
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
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comments` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `commenter` varchar(200) NOT NULL DEFAULT '',
  `commenter_id` int(10) unsigned NOT NULL DEFAULT '1',
  `commenter_ip` varchar(39) DEFAULT NULL,
  `commenter_email` varchar(80) DEFAULT NULL,
  `message` mediumtext,
  `admin_note` mediumtext,
  `hide_smilies` tinyint(1) NOT NULL DEFAULT '0',
  `commented` int(10) unsigned NOT NULL DEFAULT '0',
  `edited` int(10) unsigned DEFAULT NULL,
  `edited_by` varchar(200) DEFAULT NULL,
  `thread_id` int(10) unsigned NOT NULL DEFAULT '0',
  `marked` tinyint(1) NOT NULL DEFAULT '0',
  `soft` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `comments_thread_id_idx` (`thread_id`),
  KEY `comments_multi_idx` (`commenter_id`,`thread_id`),
  KEY `commented` (`commented`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (2,'Marwolf',2,'127.0.0.1',NULL,'This has been made possible thanks to those who released the RSCLegacy source. I have been working hard to clean it up and update a whole lot of things.',NULL,0,1527784187,NULL,NULL,19,0,0),(3,'Marwolf',2,'127.0.0.1',NULL,'Hey everyone!',NULL,0,1527784708,NULL,NULL,20,0,0);
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
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
INSERT INTO `config` VALUES ('o_cur_version','2.0.7'),('o_core_version','2.0.5771'),('o_code_name','Emerald'),('o_database_revision','91.26'),('o_searchindex_revision','2.0'),('o_parser_revision','11.4.2'),('o_board_title','Open RSC'),('o_board_slogan','A RuneScape Classic Private Server'),('o_board_description',''),('o_board_tags','runescape classic,rsc'),('o_timezone','America/New_York'),('o_time_format','H:i'),('o_date_format','j M Y'),('o_timeout_visit','1800'),('o_timeout_online','300'),('o_show_user_info','1'),('o_show_comment_count','1'),('o_signatures','1'),('o_smilies_sig','1'),('o_make_links','1'),('o_default_lang','English'),('o_default_style','RSCLegacy'),('o_default_user_group','4'),('o_disp_threads','30'),('o_disp_comments','20'),('o_indent_num_spaces','4'),('o_quote_depth','3'),('o_allow_center','1'),('o_allow_size','1'),('o_allow_spoiler','1'),('o_users_online','1'),('o_censoring','1'),('o_ranks','1'),('o_has_commented','1'),('o_thread_views','1'),('o_gzip','1'),('o_report_method','0'),('o_regs_report','0'),('o_default_email_setting','1'),('o_mailing_list','cleako@gmail.com'),('o_avatars','1'),('o_avatars_dir','img/avatars'),('o_avatars_width','250'),('o_avatars_height','250'),('o_avatars_size','250000'),('o_search_all_forums','1'),('o_base_url','http://localhost/html'),('o_admin_email','cleako@gmail.com'),('o_webmaster_email','cleako@gmail.com'),('o_forum_subscriptions','1'),('o_thread_subscriptions','1'),('recaptcha_secret_key','6Lc2tAgUAAAAAD1UAHTe5p-a5K4OxoYnDtOB0jw9'),('o_allow_advanced_editor','0'),('o_allow_dialog_editor','0'),('o_first_run_backstage','1'),('o_smtp_host','smtp.gmail.com'),('o_smtp_user','cleako@gmail.com'),('o_smtp_pass',NULL),('o_smtp_ssl','1'),('o_regs_allow','1'),('o_regs_verify','0'),('o_enable_advanced_search','1'),('o_announcement','0'),('o_announcement_message','Enter your announcement here.'),('o_announcement_title','<b>Announcement</b>'),('o_announcement_type','default'),('o_rules','0'),('o_rules_message','Rules'),('o_maintenance','0'),('o_maintenance_message',''),('o_feed_type','2'),('o_feed_ttl','5'),('o_cookie_bar','0'),('o_cookie_bar_url','http://getluna.org/docs/cookies.php'),('o_moderated_by','1'),('o_admin_note','Needed to insert this into the database to correct a SQL issue with boards being displayed:\r\n\r\nSET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,&#039;ONLY_FULL_GROUP_BY&#039;,&#039;&#039;));'),('o_enable_inbox','1'),('o_message_per_page','10'),('o_max_receivers','5'),('o_inbox_notification','0'),('o_emoji','1'),('o_emoji_size','16'),('o_back_to_top','1'),('o_show_copyright','0'),('o_copyright_type','0'),('o_custom_copyright',NULL),('o_header_search','1'),('o_board_statistics','1'),('o_notification_flyout','1'),('recaptcha_site_key','6Lc2tAgUAAAAAKiW5FtVpH4u9Ueqw42IFuL1n2Dg'),('o_message_img_tag','1'),('o_message_all_caps','1'),('o_subject_all_caps','1'),('o_sig_all_caps','0'),('o_sig_img_tag','1'),('o_sig_length','400'),('o_sig_lines','4'),('o_allow_banned_email','1'),('o_allow_dupe_email','1'),('o_force_guest_email','1');
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
  `comment` tinyint(1) NOT NULL DEFAULT '1',
  `create_threads` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`group_id`,`forum_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_perms`
--

LOCK TABLES `forum_perms` WRITE;
/*!40000 ALTER TABLE `forum_perms` DISABLE KEYS */;
INSERT INTO `forum_perms` VALUES (2,16,1,1,0),(4,16,1,1,0),(9,16,1,1,0),(10,16,1,1,0);
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
INSERT INTO `forum_subscriptions` VALUES (2,7);
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
  `moderators` text,
  `num_threads` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `num_comments` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `last_comment` int(10) unsigned DEFAULT NULL,
  `last_comment_id` int(10) unsigned DEFAULT NULL,
  `last_commenter_id` int(10) DEFAULT NULL,
  `sort_by` tinyint(1) NOT NULL DEFAULT '0',
  `disp_position` int(10) NOT NULL DEFAULT '0',
  `cat_id` int(10) unsigned NOT NULL DEFAULT '0',
  `color` varchar(25) NOT NULL DEFAULT '#2788cb',
  `parent_id` int(11) DEFAULT '0',
  `solved` tinyint(1) NOT NULL DEFAULT '1',
  `icon` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forums`
--

LOCK TABLES `forums` WRITE;
/*!40000 ALTER TABLE `forums` DISABLE KEYS */;
INSERT INTO `forums` VALUES (16,'Development News and Updates',NULL,NULL,1,1,1527784187,2,2,0,0,8,'#2788cb',0,1,''),(17,'The Pub','Talk about anything you want here',NULL,1,1,1527784708,3,2,0,0,8,'#2788cb',0,1,''),(18,'Development Requests','Ask for features here',NULL,0,0,NULL,NULL,NULL,0,0,8,'#2788cb',0,1,'');
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
  `g_moderator` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_edit_users` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_rename_users` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_change_passwords` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_ban_users` tinyint(1) NOT NULL DEFAULT '0',
  `g_read_board` tinyint(1) NOT NULL DEFAULT '1',
  `g_view_users` tinyint(1) NOT NULL DEFAULT '1',
  `g_comment` tinyint(1) NOT NULL DEFAULT '1',
  `g_create_threads` tinyint(1) NOT NULL DEFAULT '1',
  `g_edit_comments` tinyint(1) NOT NULL DEFAULT '1',
  `g_delete_comments` tinyint(1) NOT NULL DEFAULT '1',
  `g_delete_threads` tinyint(1) NOT NULL DEFAULT '1',
  `g_set_title` tinyint(1) NOT NULL DEFAULT '1',
  `g_search` tinyint(1) NOT NULL DEFAULT '1',
  `g_search_users` tinyint(1) NOT NULL DEFAULT '1',
  `g_send_email` tinyint(1) NOT NULL DEFAULT '1',
  `g_comment_flood` smallint(6) NOT NULL DEFAULT '30',
  `g_search_flood` smallint(6) NOT NULL DEFAULT '30',
  `g_email_flood` smallint(6) NOT NULL DEFAULT '60',
  `g_inbox` tinyint(1) NOT NULL DEFAULT '1',
  `g_inbox_limit` int(11) NOT NULL DEFAULT '20',
  `g_report_flood` smallint(6) NOT NULL DEFAULT '60',
  `g_soft_delete_view` tinyint(1) NOT NULL DEFAULT '1',
  `g_soft_delete_comments` tinyint(1) NOT NULL DEFAULT '1',
  `g_soft_delete_threads` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`g_id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
INSERT INTO `groups` VALUES (1,'Administrators','Administrator',1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,20,0,1,1,1),(2,'Moderators','Moderator',1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,20,0,1,1,1),(3,'Guests',NULL,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,60,30,0,1,20,0,0,0,0),(4,'Members','Member',0,0,0,0,0,1,1,1,1,1,1,1,0,1,1,1,60,30,60,1,20,60,0,0,0),(9,'Subscribers','Subscriber',0,0,0,0,0,1,1,1,1,1,1,1,0,1,1,1,60,30,60,1,20,60,0,0,0),(10,'Premium Subscribers','Premium Subscriber',0,0,0,0,0,1,1,1,1,1,1,1,0,1,1,1,60,30,60,1,20,60,0,0,0);
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `guides`
--

DROP TABLE IF EXISTS `guides`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guides` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(80) NOT NULL,
  `description` varchar(255) NOT NULL,
  `type` tinyint(1) NOT NULL,
  `difficulty` varchar(130) NOT NULL,
  `start_location` varchar(255) NOT NULL,
  `post` text NOT NULL,
  `poster` varchar(12) NOT NULL,
  `length` varchar(6) NOT NULL,
  `quest_points` int(2) unsigned NOT NULL,
  `guide_type` varchar(6) NOT NULL,
  `reqs` mediumtext,
  `items_needed` mediumtext,
  `rewards` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guides`
--

LOCK TABLES `guides` WRITE;
/*!40000 ALTER TABLE `guides` DISABLE KEYS */;
INSERT INTO `guides` VALUES (1,'Black Knights\' Fortress','The Black Knights are up to no good. You are hired by the white knights to spy on them and destroy their secret weapon.',0,'<i class=\'fa fa-star\'></i><i class=\'fa fa-star\'></i>','Falador','hejhejhej','Imposter','Medium',3,'Free',NULL,NULL,'1 Quest Point\r\n2500 thieving xpf'),(2,'Cook\'s Assistant','The Lumbridge Castle cook is in a mess. It is the Duke of Lumbridge\'s birthday and the cook is making the cake. He needs a lot of ingredients and doesn\'t have much time.',0,'<i class=\'fa fa-star\'></i>','Lumbridge','Speak with the Cook on Lumbridge Castle\'s first floor. He will tell the player that he needs ingredients to make a cake for the Duke of Lumbridge\'s birthday. He wants the player to retrieve flour, milk, and an egg, and to bring those items back to him. Before you set off and collect your ingredients, pick up the pot that\'s sitting nearby on a table, which you will need later. You\'ll also want to purchase a bucket from the nearby General Store.\r\n\r\n[b]Obtaining milk[/b]\r\nTo obtain milk, you simply have to use a bucket on a Cow. A bucket item spawn point can be found inside the chicken farm across the river north-west of Lumbridge, or you can simply purchase one from the General Store north of the castle. Cows can be found by following the path that leads north-west out of Lumbridge.\r\n[center][img]http://vignette1.wikia.nocookie.net/runescapeclassic/images/9/9a/IngredientLocationsForCooksQuest.png/revision/latest/scale-to-width-down/180?cb=20110818225925[/img][/center]\r\n\r\n[b]Obtaining flour[/b]\r\nBefore doing anything, make sure you have a pot in your inventory. Now, to obtain flour, follow the path that leads north-west out of Lumbridge. Go past the chicken farm and you\'ll see a field of wheat. Right-click on one and pick it, you\'ll obtain grain. Now, go into the windmill beside the field and go to the top floor. Use the grain on the hopper, and then operate the operate. The grain will go down the chute to the bottom of the windmill, so head there. At the bottom, you should see a pile of flour in the centre of the room. Simply use your pot on the pile of flour, and you now have flour in your inventory.\r\n\r\n[b]Obtaining an egg[/b]\r\nEggs can be found to the north-east of Lumbridge. Cross the bridge going across river and follow the path north. After a little while, you will come across a chicken farm on the west side of the path. Beside the little coup is a single egg item spawn. Pick it up, and if you now have all the items the cook needs, head back to him. The map is incorrect, the egg is located in the coop right next to where you get the bucket of milk from the cows.\r\n\r\n[b]The End[/b]\r\nOnce you have all three ingredients, speak to the cook in the castle. He\'ll take the ingredients and thank you for helping him, completing one of the easiest quests in the game.\r\n\r\n[b]Rewards[/b]\r\n[list]\r\n[*]1 Quest Point[/*]\r\n[*]Cooking experience[/*]\r\n[*]Access to the Cook\'s Range in Lumbridge Castle[/*]\r\n[/list]','Imposter','Short',1,'Free','None','<img src=\"img/items/22.png\" data-toggle=\"tooltip\" title=\"Milk\"><img src=\"img/items/136.png\" data-toggle=\"tooltip\" title=\"Flour\"><img src=\"img/items/19.png\" data-toggle=\"tooltip\" title=\"Egg\"> (These items are obtained during the quest)',NULL),(3,'Demon Slayer','A mighty demon is being summoned to destroy the city of Varrock. You are the one destined to stop him (or at least try).',0,'<i class=\'fa fa-star\'></i><i class=\'fa fa-star\'></i>','Varrock Square','Talk to the Gypsy in Varrock Square and have her predict your future for 1 coin. She will tell you that 150 years ago a demon named Delrith came to Varrock, but was quarantined by a hero named Wally who defeated him with a special sword named Silverlight and trapped him away. The Gypsy says that Darkwizards at the stone circle south of Varrock\'s are trying to resurrect him. She tells you that you are destined to kill the demon again using Silverlight which has been passed down to Sir Prysin, a knight in Varrock Palace.','Davve','Medium',3,'Free',NULL,NULL,NULL),(4,'Starting the Game','Are you having trouble starting Wolf Kingdom?',1,'<i class=\'fa fa-star\'></i>','NULL','Hello we are a legacy.','Imposter','Short',0,'Free',NULL,NULL,NULL),(5,'Create Your Character','Create your character on Wolf Kingdom and begin your adventure!',1,'<i class=\'fa fa-star\'></i>','NULL','We are creating a char yes?','Imposter','Short',0,'Free',NULL,NULL,NULL);
/*!40000 ALTER TABLE `guides` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hof_categories`
--

DROP TABLE IF EXISTS `hof_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hof_categories` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `hof_name` varchar(80) NOT NULL DEFAULT 'New Hof Category',
  `hof_position` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hof_categories`
--

LOCK TABLES `hof_categories` WRITE;
/*!40000 ALTER TABLE `hof_categories` DISABLE KEYS */;
INSERT INTO `hof_categories` VALUES (1,'Quests',0),(2,'NPC Kills',0),(3,'Drops',0),(4,'Achievement Diary',0),(5,'Skills',0),(6,'Capes',0);
/*!40000 ALTER TABLE `hof_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hof_entrys`
--

DROP TABLE IF EXISTS `hof_entrys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hof_entrys` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `accomplishment` varchar(60) NOT NULL,
  `category` tinyint(1) unsigned NOT NULL,
  `username` varchar(12) DEFAULT NULL,
  `time` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hof_entrys`
--

LOCK TABLES `hof_entrys` WRITE;
/*!40000 ALTER TABLE `hof_entrys` DISABLE KEYS */;
INSERT INTO `hof_entrys` VALUES (7,'First player to complete all quests',1,'Imposter','18/11/16'),(8,'First player to complete all F2P quests',1,'-','-'),(9,'First player to pick up a Rune Large Helmet',3,'Mud','16/11/16'),(10,'First player to get 99 Fishing level',5,'-','-'),(11,'First player who completed Dragon Slayer',1,'-','-'),(12,'First to loot Rune Long Sword',3,'-','-'),(14,'First player to get Fishing Cape',6,NULL,NULL);
/*!40000 ALTER TABLE `hof_entrys` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menu` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `url` varchar(200) NOT NULL DEFAULT '',
  `name` varchar(200) NOT NULL DEFAULT '',
  `disp_position` int(10) NOT NULL DEFAULT '0',
  `visible` int(10) NOT NULL DEFAULT '1',
  `sys_entry` int(10) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,'index.php','Index',1,1,1),(2,'userlist.php','Users',2,1,1),(3,'search.php','Search',3,1,1);
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
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
  `last_comment` int(10) DEFAULT '0',
  `last_comment_id` int(10) DEFAULT '0',
  `last_commenter` varchar(255) NOT NULL DEFAULT '0',
  `owner` int(11) NOT NULL DEFAULT '0',
  `subject` varchar(255) NOT NULL,
  `message` mediumtext NOT NULL,
  `hide_smilies` tinyint(1) NOT NULL DEFAULT '0',
  `show_message` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(200) NOT NULL,
  `receiver` varchar(200) DEFAULT NULL,
  `sender_id` int(10) NOT NULL DEFAULT '0',
  `receiver_id` varchar(255) DEFAULT '0',
  `sender_ip` varchar(39) DEFAULT NULL,
  `commented` int(10) NOT NULL,
  `showed` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notifications` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL DEFAULT '0',
  `message` varchar(255) NOT NULL DEFAULT '0',
  `icon` varchar(255) NOT NULL DEFAULT '0',
  `link` varchar(255) NOT NULL DEFAULT '0',
  `time` int(11) NOT NULL DEFAULT '0',
  `viewed` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (2,2,'Adventurer! You have created a RSCLegacy character: marwolf!','fa-user-plus','char_manager.php?id=2',1527788945,1),(3,2,'Adventurer! You have created a RSCLegacy character: testing!','fa-user-plus','char_manager.php?id=2',1529508602,0),(4,2,'Adventurer! You have created a RSCLegacy character: marwolf!','fa-user-plus','char_manager.php?id=2',1529508625,0),(5,2,'Adventurer! You have created a RSCLegacy character: nipper!','fa-user-plus','char_manager.php?id=2',1529509682,0);
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
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
  `last_comment` int(10) unsigned DEFAULT NULL,
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
INSERT INTO `online` VALUES (2,'Marwolf',1529510069,0,NULL,NULL);
/*!40000 ALTER TABLE `online` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `poll_results`
--

DROP TABLE IF EXISTS `poll_results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `poll_results` (
  `primary_id` int(10) NOT NULL AUTO_INCREMENT,
  `poll_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user` int(5) unsigned NOT NULL,
  `user_ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `option_selected` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`primary_id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `poll_results`
--

LOCK TABLES `poll_results` WRITE;
/*!40000 ALTER TABLE `poll_results` DISABLE KEYS */;
INSERT INTO `poll_results` VALUES (7,1,2,'::1',1);
/*!40000 ALTER TABLE `poll_results` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `polls`
--

DROP TABLE IF EXISTS `polls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `polls` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `started_by` int(5) unsigned NOT NULL,
  `started_when` int(10) unsigned NOT NULL DEFAULT '0',
  `question` varchar(140) NOT NULL,
  `poll_closed` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `option_1` varchar(140) NOT NULL,
  `option_2` varchar(140) NOT NULL,
  `option_3` varchar(140) NOT NULL,
  `option_4` varchar(140) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `polls`
--

LOCK TABLES `polls` WRITE;
/*!40000 ALTER TABLE `polls` DISABLE KEYS */;
INSERT INTO `polls` VALUES (1,2,1490528282,'The closest server to real RSC?',1,'Wolf Kingdom','???','','');
/*!40000 ALTER TABLE `polls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ranks`
--

DROP TABLE IF EXISTS `ranks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ranks` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `rank` varchar(50) NOT NULL DEFAULT '',
  `min_comments` mediumint(8) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ranks`
--

LOCK TABLES `ranks` WRITE;
/*!40000 ALTER TABLE `ranks` DISABLE KEYS */;
INSERT INTO `ranks` VALUES (1,'New member',0),(2,'Member',10);
/*!40000 ALTER TABLE `ranks` ENABLE KEYS */;
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
  `comment_id` int(10) unsigned NOT NULL DEFAULT '0',
  `thread_id` int(10) unsigned NOT NULL DEFAULT '0',
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
) ENGINE=MyISAM AUTO_INCREMENT=1341 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_curstats`
--

LOCK TABLES `rscd_curstats` WRITE;
/*!40000 ALTER TABLE `rscd_curstats` DISABLE KEYS */;
INSERT INTO `rscd_curstats` VALUES ('51697882930',1,1,1,10,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1338),('33458708176',1,1,1,10,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1339),('988515402',1,1,1,10,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1340);
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
) ENGINE=MyISAM AUTO_INCREMENT=1341 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_experience`
--

LOCK TABLES `rscd_experience` WRITE;
/*!40000 ALTER TABLE `rscd_experience` DISABLE KEYS */;
INSERT INTO `rscd_experience` VALUES ('51697882930',0,0,0,1154,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1338,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('33458708176',0,0,0,1154,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1339,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('988515402',0,0,0,1154,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1340,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
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
  `pass` char(128) NOT NULL,
  `password_salt` char(30) DEFAULT NULL,
  `creation_date` int(10) NOT NULL DEFAULT '0',
  `creation_ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `login_date` int(10) NOT NULL DEFAULT '0',
  `logout_date` bigint(10) NOT NULL DEFAULT '0',
  `death_time` bigint(10) NOT NULL DEFAULT '0',
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
  `highscoreopt` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `banned` varchar(255) NOT NULL DEFAULT '0',
  `forum_active` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user` (`user`),
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
) ENGINE=MyISAM AUTO_INCREMENT=1341 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 9216 kB';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rscd_players`
--

LOCK TABLES `rscd_players` WRITE;
/*!40000 ALTER TABLE `rscd_players` DISABLE KEYS */;
INSERT INTO `rscd_players` VALUES ('51697882930','testing',0,2,3,0,27,0,225,447,0,0,0,0,0,0,1,1,0,0,0,0,1,2,8,14,0,1,2,1,0,'7b41d6191387f2ad7846669662929cfdd1b2f65a5838ef616ffb846d0c069070556155849e8952066c6ea241c38188ac8fd3db7f4bbdd52a30954f13d4baff67','EQGw0LcFd656Xzo7',1529508602,'127.0.0.1',0,0,0,'0.0.0.0',0,0,1338,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,0,0,0,0,0,0,0,0,0,0,0,'0',NULL,NULL,0,'0',0),('33458708176','marwolf',0,2,3,NULL,27,NULL,225,447,0,0,0,0,0,0,1,1,0,0,0,0,1,2,8,14,0,1,2,1,0,'309d2fefdb34753ebe9f41874338a64e6ed747ece67db1b68edaf4ff70ec0174cb896639c3149d512a97a8466e9a8a194dcfce550f2323c3ba8a9f2d104ae5cb','TBa8QCmwntxdAD7w',1529508625,'127.0.0.1',0,0,0,'0.0.0.0',0,NULL,1339,0,0,NULL,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,0,0,0,0,0,0,0,0,0,0,0,NULL,NULL,NULL,0,'0',0),('988515402','nipper',4,2,3,NULL,27,NULL,225,447,0,0,0,0,0,0,1,1,0,0,0,0,1,2,8,14,0,1,2,1,0,'10e3ac259e990c1d9aa4ca46903c63990cbd758d6c50e418cd0df983e84415017e9a11170194b212a056242a338276940f6241b8bb1494acf05b858aee6f9c1e','p0mHvSuJ6uq8OZbA',1529509682,'127.0.0.1',0,0,0,'0.0.0.0',0,NULL,1340,0,0,NULL,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,0,0,0,0,0,0,0,0,0,0,0,NULL,NULL,NULL,0,'0',1);
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
INSERT INTO `search_cache` VALUES (396365122,'Marwolf','a:6:{s:10:\"search_ids\";s:131:\"a:10:{i:0;s:2:\"17\";i:1;s:2:\"16\";i:2;s:2:\"12\";i:3;s:2:\"10\";i:4;s:1:\"7\";i:5;s:1:\"6\";i:6;s:1:\"5\";i:7;s:1:\"4\";i:8;s:1:\"3\";i:9;s:1:\"2\";}\";s:8:\"num_hits\";i:10;s:7:\"sort_by\";i:0;s:8:\"sort_dir\";s:4:\"DESC\";s:7:\"show_as\";s:7:\"threads\";s:11:\"search_type\";a:2:{i:0;s:6:\"action\";i:1;s:15:\"show_unanswered\";}}'),(512314439,'Marwolf','a:6:{s:10:\"search_ids\";s:19:\"a:1:{i:0;s:2:\"13\";}\";s:8:\"num_hits\";i:1;s:7:\"sort_by\";i:0;s:8:\"sort_dir\";s:4:\"DESC\";s:7:\"show_as\";s:7:\"threads\";s:11:\"search_type\";a:3:{i:0;s:6:\"action\";i:1;s:18:\"show_subscriptions\";i:2;i:2;}}');
/*!40000 ALTER TABLE `search_cache` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `search_matches`
--

DROP TABLE IF EXISTS `search_matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_matches` (
  `comment_id` int(10) unsigned NOT NULL DEFAULT '0',
  `word_id` int(10) unsigned NOT NULL DEFAULT '0',
  `subject_match` tinyint(1) NOT NULL DEFAULT '0',
  KEY `search_matches_word_id_idx` (`word_id`),
  KEY `search_matches_comment_id_idx` (`comment_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `search_matches`
--

LOCK TABLES `search_matches` WRITE;
/*!40000 ALTER TABLE `search_matches` DISABLE KEYS */;
INSERT INTO `search_matches` VALUES (2,1,0),(2,2,0),(2,3,0),(2,4,0),(2,5,0),(2,6,0),(2,7,0),(2,8,0),(2,9,0),(2,10,0),(2,11,0),(2,12,0),(2,13,1),(2,4,1),(2,5,1),(3,14,0),(3,15,1),(3,16,1);
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
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `search_words`
--

LOCK TABLES `search_words` WRITE;
/*!40000 ALTER TABLE `search_words` DISABLE KEYS */;
INSERT INTO `search_words` VALUES (1,'made'),(2,'possible'),(3,'released'),(4,'rsclegacy'),(5,'source'),(6,'working'),(7,'hard'),(8,'clean'),(9,'update'),(10,'whole'),(11,'lot'),(12,'things'),(13,'release'),(14,'hey'),(15,'introduce'),(16,'yourself');
/*!40000 ALTER TABLE `search_words` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop`
--

DROP TABLE IF EXISTS `shop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shop` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `item_id` mediumint(5) unsigned NOT NULL,
  `product_name` varchar(60) NOT NULL,
  `product_desc` text NOT NULL,
  `product_category` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `redirect_url` varchar(60) NOT NULL,
  `product_image` varchar(60) NOT NULL,
  `product_price` mediumint(5) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop`
--

LOCK TABLES `shop` WRITE;
/*!40000 ALTER TABLE `shop` DISABLE KEYS */;
INSERT INTO `shop` VALUES (1,0,'Character Slot','Extra character slot.\r\nThis service gives you [b]ONE[/b] extra character slot to your character manager.\r\n\r\nThe default character slot is 6 and maximum capacity to have per forum account is 10.',0,'','img/icons/character_slot.png',540),(2,0,'Character Deletion','',0,'char_manager.php','img/icons/delete_character.png',100),(3,0,'Character Renaming','Character renamer. \r\nRename your character in your character manager page. \r\n\r\nRemember changing your name results in your previous name being available for others.',0,'char_manager.php?setting=character_renaming','img/icons/rename_character.png',200),(4,2092,'Gold Subscription','The gold subscription token.\r\n\r\nUse this token in order to activate your subscription features and faster experience rate. This token last for 30 days.\r\n\r\nHere\'s the full list of what this item include:\r\n[list]\r\n[*]-Faster xp rate[/*]\r\n[*]-Slower fatigue[/*]\r\n[/list]\r\n\r\nTo get the full maximum subscriber benefits use this token together with the Premium subscription token.',2,'','img/items/2092.png',540),(5,2094,'Premium Subscription','The premium subscription token.\r\n\r\nThis token last for 30 days and can only be used if you already have a gold subscription active.\r\n\r\nHere\'s the full list of what this item include:\r\n[list]\r\n[*]-Faster xp rate[/*]\r\n[*]-Slower fatigue[/*]\r\n[/list]',2,'','img/items/2094.png',1000),(6,2110,'Spotted Cape','A cosmetic item, a spotted cape.',1,'','img/items/2110.png',100);
/*!40000 ALTER TABLE `shop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_logs`
--

DROP TABLE IF EXISTS `shop_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shop_logs` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `package` varchar(60) NOT NULL,
  `product_id` mediumint(5) unsigned NOT NULL,
  `price` int(10) unsigned NOT NULL,
  `quantity` tinyint(2) unsigned NOT NULL,
  `creation` int(10) unsigned NOT NULL DEFAULT '0',
  `forum_name` varchar(12) NOT NULL,
  `game_name` varchar(12) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_logs`
--

LOCK TABLES `shop_logs` WRITE;
/*!40000 ALTER TABLE `shop_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_logs` ENABLE KEYS */;
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
-- Table structure for table `thread_subscriptions`
--

DROP TABLE IF EXISTS `thread_subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thread_subscriptions` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `thread_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`thread_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thread_subscriptions`
--

LOCK TABLES `thread_subscriptions` WRITE;
/*!40000 ALTER TABLE `thread_subscriptions` DISABLE KEYS */;
INSERT INTO `thread_subscriptions` VALUES (2,0);
/*!40000 ALTER TABLE `thread_subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `threads`
--

DROP TABLE IF EXISTS `threads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `threads` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `commenter` varchar(200) NOT NULL DEFAULT '',
  `subject` varchar(255) NOT NULL DEFAULT '',
  `commented` int(10) unsigned NOT NULL DEFAULT '0',
  `first_comment_id` int(10) unsigned NOT NULL DEFAULT '0',
  `last_comment` int(10) unsigned NOT NULL DEFAULT '0',
  `last_comment_id` int(10) unsigned NOT NULL DEFAULT '0',
  `last_commenter` varchar(200) DEFAULT NULL,
  `num_views` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `num_replies` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `last_commenter_id` int(10) DEFAULT NULL,
  `closed` tinyint(1) NOT NULL DEFAULT '0',
  `pinned` tinyint(1) NOT NULL DEFAULT '0',
  `important` tinyint(1) NOT NULL DEFAULT '0',
  `moved_to` int(10) unsigned DEFAULT NULL,
  `forum_id` int(10) unsigned NOT NULL DEFAULT '0',
  `soft` tinyint(1) NOT NULL DEFAULT '0',
  `solved` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `threads_forum_id_idx` (`forum_id`),
  KEY `threads_moved_to_idx` (`moved_to`),
  KEY `threads_last_comment_idx` (`last_comment`),
  KEY `threads_last_commenter_id` (`last_commenter`),
  KEY `threads_first_comment_id_idx` (`first_comment_id`),
  KEY `commented` (`commented`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `threads`
--

LOCK TABLES `threads` WRITE;
/*!40000 ALTER TABLE `threads` DISABLE KEYS */;
INSERT INTO `threads` VALUES (19,'Marwolf','Thanks to the RSCLegacy source release',1527784187,2,1527784187,2,'Marwolf',4,0,2,0,0,0,NULL,16,0,NULL),(20,'Marwolf','Introduce yourself',1527784708,3,1527784708,3,'Marwolf',1,0,2,0,0,0,NULL,17,0,NULL);
/*!40000 ALTER TABLE `threads` ENABLE KEYS */;
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
  `password` varchar(512) NOT NULL DEFAULT '',
  `salt` varchar(8) NOT NULL DEFAULT '',
  `email` varchar(80) NOT NULL DEFAULT '',
  `title` varchar(50) DEFAULT NULL,
  `realname` varchar(40) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  `facebook` varchar(50) DEFAULT NULL,
  `msn` varchar(80) DEFAULT NULL,
  `twitter` varchar(50) DEFAULT NULL,
  `google` varchar(50) DEFAULT NULL,
  `location` varchar(30) DEFAULT NULL,
  `signature` text,
  `disp_threads` tinyint(3) unsigned DEFAULT NULL,
  `disp_comments` tinyint(3) unsigned DEFAULT NULL,
  `email_setting` tinyint(1) NOT NULL DEFAULT '1',
  `notify_with_comment` tinyint(1) NOT NULL DEFAULT '0',
  `advanced_editor` tinyint(1) NOT NULL DEFAULT '1',
  `dialog_editor` tinyint(1) NOT NULL DEFAULT '1',
  `auto_notify` tinyint(1) NOT NULL DEFAULT '0',
  `show_smilies` tinyint(1) NOT NULL DEFAULT '1',
  `show_img` tinyint(1) NOT NULL DEFAULT '1',
  `show_img_sig` tinyint(1) NOT NULL DEFAULT '1',
  `show_avatars` tinyint(1) NOT NULL DEFAULT '1',
  `show_sig` tinyint(1) NOT NULL DEFAULT '1',
  `php_timezone` varchar(100) NOT NULL DEFAULT 'UTC',
  `time_format` tinyint(1) NOT NULL DEFAULT '0',
  `date_format` tinyint(1) NOT NULL DEFAULT '0',
  `language` varchar(25) NOT NULL DEFAULT 'English',
  `num_comments` int(10) unsigned NOT NULL DEFAULT '0',
  `last_comment` int(10) unsigned DEFAULT NULL,
  `last_search` int(10) unsigned DEFAULT NULL,
  `last_email_sent` int(10) unsigned DEFAULT NULL,
  `last_report_sent` int(10) unsigned DEFAULT NULL,
  `registered` int(10) unsigned NOT NULL DEFAULT '0',
  `registration_ip` varchar(39) NOT NULL DEFAULT '0.0.0.0',
  `last_visit` int(10) unsigned NOT NULL DEFAULT '0',
  `admin_note` varchar(30) DEFAULT NULL,
  `activate_string` varchar(128) DEFAULT NULL,
  `activate_key` varchar(8) DEFAULT NULL,
  `use_inbox` tinyint(1) NOT NULL DEFAULT '1',
  `notify_inbox` tinyint(1) NOT NULL DEFAULT '1',
  `notify_inbox_full` tinyint(1) NOT NULL DEFAULT '0',
  `num_inbox` int(10) unsigned NOT NULL DEFAULT '0',
  `jewels` int(10) unsigned NOT NULL DEFAULT '0',
  `teleport_stone` int(10) unsigned NOT NULL DEFAULT '0',
  `character_slots` tinyint(3) unsigned NOT NULL DEFAULT '6',
  `gold_time` int(10) unsigned NOT NULL DEFAULT '0',
  `premium_time` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_username_idx` (`username`(25)),
  KEY `users_registered_idx` (`registered`)
) ENGINE=MyISAM AUTO_INCREMENT=63 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,3,'Guest','Guest','','Guest',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'',NULL,NULL,1,0,0,0,0,1,1,1,1,1,'UTC',0,0,'English',0,NULL,NULL,NULL,NULL,0,'0.0.0.0',0,NULL,NULL,NULL,1,1,0,0,0,0,6,0,0),(2,1,'Marwolf','ab1c8e6e77f6e01716c8f7838807687c80eb45f2383940284a8a85710b21040d55af5b7d03c964b74341b1ef2878a36e42958f158e3e3b3dcdb0eda75125154a','UqqYG1aE','cleako@gmail.com',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,0,0,0,1,1,1,0,1,'America/New_York',4,5,'English',29,1527784708,0,1529509052,1469742274,0,'::1',1527789080,NULL,'1338','fp6C5J7u',1,1,0,200,20,50000,50,1487505329,1510583603);
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

-- Dump completed on 2018-06-20 11:55:01
