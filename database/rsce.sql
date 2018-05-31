-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Aug 02, 2017 at 11:46 PM
-- Server version: 5.5.52-MariaDB
-- PHP Version: 7.0.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `rscunity`
--

-- --------------------------------------------------------

--
-- Table structure for table `bans`
--

CREATE TABLE `bans` (
  `id` int(10) UNSIGNED NOT NULL,
  `username` varchar(200) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `email` varchar(80) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `expire` int(10) UNSIGNED DEFAULT NULL,
  `ban_creator` int(10) UNSIGNED NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(10) UNSIGNED NOT NULL,
  `cat_name` varchar(80) NOT NULL DEFAULT 'New Category',
  `disp_position` int(10) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `censoring`
--

CREATE TABLE `censoring` (
  `id` int(10) UNSIGNED NOT NULL,
  `search_for` varchar(60) NOT NULL DEFAULT '',
  `replace_with` varchar(60) NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `config`
--

CREATE TABLE `config` (
  `conf_name` varchar(255) NOT NULL DEFAULT '',
  `conf_value` text
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `forums`
--

CREATE TABLE `forums` (
  `id` int(10) UNSIGNED NOT NULL,
  `forum_name` varchar(80) NOT NULL DEFAULT 'New forum',
  `forum_desc` text,
  `redirect_url` varchar(100) DEFAULT NULL,
  `moderators` text,
  `num_topics` mediumint(8) UNSIGNED NOT NULL DEFAULT '0',
  `num_posts` mediumint(8) UNSIGNED NOT NULL DEFAULT '0',
  `last_post` int(10) UNSIGNED DEFAULT NULL,
  `last_post_id` int(10) UNSIGNED DEFAULT NULL,
  `last_poster` varchar(200) DEFAULT NULL,
  `sort_by` tinyint(1) NOT NULL DEFAULT '0',
  `disp_position` int(10) NOT NULL DEFAULT '0',
  `cat_id` int(10) UNSIGNED NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `forum_perms`
--

CREATE TABLE `forum_perms` (
  `group_id` int(10) NOT NULL DEFAULT '0',
  `forum_id` int(10) NOT NULL DEFAULT '0',
  `read_forum` tinyint(1) NOT NULL DEFAULT '1',
  `post_replies` tinyint(1) NOT NULL DEFAULT '1',
  `post_topics` tinyint(1) NOT NULL DEFAULT '1',
  `post_polls` tinyint(1) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `forum_subscriptions`
--

CREATE TABLE `forum_subscriptions` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `forum_id` int(10) UNSIGNED NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE `groups` (
  `g_id` int(10) UNSIGNED NOT NULL,
  `g_title` varchar(50) NOT NULL DEFAULT '',
  `g_user_title` varchar(50) DEFAULT NULL,
  `g_promote_min_posts` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `g_promote_next_group` int(10) UNSIGNED NOT NULL DEFAULT '0',
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
  `g_post_polls` smallint(5) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `highscores`
--

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
  `deaths_rank` int(10) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
  `id` int(10) UNSIGNED NOT NULL,
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
  `popup` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `online`
--

CREATE TABLE `online` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `ident` varchar(200) NOT NULL DEFAULT '',
  `logged` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `idle` tinyint(1) NOT NULL DEFAULT '0',
  `last_post` int(10) UNSIGNED DEFAULT NULL,
  `last_search` int(10) UNSIGNED DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `polls`
--

CREATE TABLE `polls` (
  `id` int(10) UNSIGNED NOT NULL,
  `pollid` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `options` text NOT NULL,
  `voters` text,
  `ptype` tinyint(4) NOT NULL DEFAULT '0',
  `votes` text,
  `created` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `edited` int(10) UNSIGNED DEFAULT NULL,
  `edited_by` varchar(200) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

CREATE TABLE `posts` (
  `id` int(10) UNSIGNED NOT NULL,
  `poster` varchar(200) NOT NULL DEFAULT '',
  `poster_id` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `poster_ip` varchar(39) DEFAULT NULL,
  `poster_email` varchar(80) DEFAULT NULL,
  `message` mediumtext,
  `hide_smilies` tinyint(1) NOT NULL DEFAULT '0',
  `posted` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `edited` int(10) UNSIGNED DEFAULT NULL,
  `edited_by` varchar(200) DEFAULT NULL,
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `recovery_questions`
--

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
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

CREATE TABLE `reports` (
  `id` int(10) UNSIGNED NOT NULL,
  `post_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `forum_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `reported_by` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `created` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `message` text,
  `zapped` int(10) UNSIGNED DEFAULT NULL,
  `zapped_by` int(10) UNSIGNED DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `rscd_auctions`
--

CREATE TABLE `rscd_auctions` (
  `id` int(11) NOT NULL,
  `player` bigint(18) NOT NULL,
  `owner` int(5) NOT NULL,
  `state` tinyint(1) NOT NULL,
  `item` int(5) NOT NULL,
  `amount` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  `expiration` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `rscd_bank`
--

CREATE TABLE `rscd_bank` (
  `owner` int(6) NOT NULL,
  `id` smallint(4) NOT NULL,
  `amount` int(10) NOT NULL DEFAULT '1',
  `slot` smallint(3) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `rscd_curstats`
--

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
  `id` int(10) UNSIGNED NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- Table structure for table `rscd_experience`
--

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
  `id` int(10) UNSIGNED NOT NULL,
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
  `total_xp` int(10) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `rscd_friends`
--

CREATE TABLE `rscd_friends` (
  `id` int(10) NOT NULL,
  `user` varchar(40) NOT NULL,
  `friend` varchar(40) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `rscd_ignores`
--

CREATE TABLE `rscd_ignores` (
  `id` int(10) NOT NULL,
  `user` varchar(40) NOT NULL,
  `ignore` varchar(40) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `rscd_invitems`
--

CREATE TABLE `rscd_invitems` (
  `user` varchar(40) NOT NULL,
  `id` smallint(4) NOT NULL,
  `amount` int(10) NOT NULL DEFAULT '1',
  `wielded` tinyint(1) NOT NULL DEFAULT '0',
  `slot` tinyint(2) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `rscd_players`
--

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
  `id` int(10) NOT NULL,
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
  `quests` longblob
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 9216 kB' ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- Table structure for table `rscd_quests`
--

CREATE TABLE `rscd_quests` (
  `id` int(10) NOT NULL,
  `user` varchar(30) NOT NULL,
  `quest_id` tinyint(3) NOT NULL,
  `quest_stage` tinyint(3) NOT NULL,
  `finished` tinyint(1) NOT NULL DEFAULT '0',
  `quest_points` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `search_cache`
--

CREATE TABLE `search_cache` (
  `id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `ident` varchar(200) NOT NULL DEFAULT '',
  `search_data` mediumtext
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_matches`
--

CREATE TABLE `search_matches` (
  `post_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `word_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `subject_match` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_words`
--

CREATE TABLE `search_words` (
  `id` int(10) UNSIGNED NOT NULL,
  `word` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `teleport_locations`
--

CREATE TABLE `teleport_locations` (
  `id` int(10) NOT NULL,
  `x` int(5) NOT NULL,
  `y` int(5) NOT NULL,
  `description` varchar(255) NOT NULL,
  `command` varchar(255) NOT NULL,
  `added_by` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `topics`
--

CREATE TABLE `topics` (
  `id` int(10) UNSIGNED NOT NULL,
  `poster` varchar(200) NOT NULL DEFAULT '',
  `subject` varchar(255) NOT NULL DEFAULT '',
  `posted` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `first_post_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `last_post` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `last_post_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `last_poster` varchar(200) DEFAULT NULL,
  `num_views` mediumint(8) UNSIGNED NOT NULL DEFAULT '0',
  `num_replies` mediumint(8) UNSIGNED NOT NULL DEFAULT '0',
  `closed` tinyint(1) NOT NULL DEFAULT '0',
  `sticky` tinyint(1) NOT NULL DEFAULT '0',
  `moved_to` int(10) UNSIGNED DEFAULT NULL,
  `forum_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `question` varchar(255) NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `topic_subscriptions`
--

CREATE TABLE `topic_subscriptions` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `id` int(10) NOT NULL,
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
  `date` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(10) UNSIGNED NOT NULL,
  `group_id` int(10) UNSIGNED NOT NULL DEFAULT '3',
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
  `disp_topics` tinyint(3) UNSIGNED DEFAULT NULL,
  `disp_posts` tinyint(3) UNSIGNED DEFAULT NULL,
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
  `num_posts` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `last_post` int(10) UNSIGNED DEFAULT NULL,
  `last_search` int(10) UNSIGNED DEFAULT NULL,
  `last_email_sent` int(10) UNSIGNED DEFAULT NULL,
  `last_report_sent` int(10) UNSIGNED DEFAULT NULL,
  `registered` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `registration_ip` varchar(39) NOT NULL DEFAULT '0.0.0.0',
  `last_visit` int(10) UNSIGNED NOT NULL DEFAULT '0',
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
  `banned` int(1) DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bans`
--
ALTER TABLE `bans`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bans_username_idx` (`username`(25));

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `censoring`
--
ALTER TABLE `censoring`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `config`
--
ALTER TABLE `config`
  ADD PRIMARY KEY (`conf_name`);

--
-- Indexes for table `forums`
--
ALTER TABLE `forums`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `forum_perms`
--
ALTER TABLE `forum_perms`
  ADD PRIMARY KEY (`group_id`,`forum_id`);

--
-- Indexes for table `forum_subscriptions`
--
ALTER TABLE `forum_subscriptions`
  ADD PRIMARY KEY (`user_id`,`forum_id`);

--
-- Indexes for table `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`g_id`);

--
-- Indexes for table `highscores`
--
ALTER TABLE `highscores`
  ADD KEY `user` (`user`,`owner`),
  ADD KEY `total_xp` (`total_xp`),
  ADD KEY `combat` (`combat`),
  ADD KEY `skill_total` (`skill_total`);

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `owner` (`owner`);

--
-- Indexes for table `online`
--
ALTER TABLE `online`
  ADD UNIQUE KEY `online_user_id_ident_idx` (`user_id`,`ident`(25)),
  ADD KEY `online_ident_idx` (`ident`(25)),
  ADD KEY `online_logged_idx` (`logged`);

--
-- Indexes for table `polls`
--
ALTER TABLE `polls`
  ADD PRIMARY KEY (`id`),
  ADD KEY `polls_pollid_idx` (`pollid`);

--
-- Indexes for table `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `posts_topic_id_idx` (`topic_id`),
  ADD KEY `posts_multi_idx` (`poster_id`,`topic_id`);

--
-- Indexes for table `recovery_questions`
--
ALTER TABLE `recovery_questions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `account` (`account`);

--
-- Indexes for table `reports`
--
ALTER TABLE `reports`
  ADD PRIMARY KEY (`id`),
  ADD KEY `reports_zapped_idx` (`zapped`);

--
-- Indexes for table `rscd_auctions`
--
ALTER TABLE `rscd_auctions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`);

--
-- Indexes for table `rscd_bank`
--
ALTER TABLE `rscd_bank`
  ADD KEY `owner` (`owner`),
  ADD KEY `id` (`id`);

--
-- Indexes for table `rscd_curstats`
--
ALTER TABLE `rscd_curstats`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user` (`user`);

--
-- Indexes for table `rscd_experience`
--
ALTER TABLE `rscd_experience`
  ADD PRIMARY KEY (`id`),
  ADD KEY `exp_attack` (`exp_attack`),
  ADD KEY `exp_defense` (`exp_defense`),
  ADD KEY `exp_strength` (`exp_strength`),
  ADD KEY `exp_hits` (`exp_hits`),
  ADD KEY `exp_ranged` (`exp_ranged`),
  ADD KEY `exp_prayer` (`exp_prayer`),
  ADD KEY `exp_magic` (`exp_magic`),
  ADD KEY `exp_cooking` (`exp_cooking`),
  ADD KEY `exp_woodcut` (`exp_woodcut`),
  ADD KEY `exp_fletching` (`exp_fletching`),
  ADD KEY `exp_fishing` (`exp_fishing`),
  ADD KEY `exp_firemaking` (`exp_firemaking`),
  ADD KEY `exp_crafting` (`exp_crafting`),
  ADD KEY `exp_smithing` (`exp_smithing`),
  ADD KEY `exp_mining` (`exp_mining`),
  ADD KEY `exp_herblaw` (`exp_herblaw`),
  ADD KEY `exp_agility` (`exp_agility`),
  ADD KEY `exp_thieving` (`exp_thieving`),
  ADD KEY `exp_runecrafting` (`exp_runecrafting`),
  ADD KEY `exp_total` (`total_xp`),
  ADD KEY `user` (`user`);

--
-- Indexes for table `rscd_friends`
--
ALTER TABLE `rscd_friends`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`),
  ADD KEY `friend` (`friend`);

--
-- Indexes for table `rscd_ignores`
--
ALTER TABLE `rscd_ignores`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`),
  ADD KEY `ignore` (`ignore`);

--
-- Indexes for table `rscd_invitems`
--
ALTER TABLE `rscd_invitems`
  ADD KEY `user` (`user`);

--
-- Indexes for table `rscd_players`
--
ALTER TABLE `rscd_players`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user` (`user`),
  ADD UNIQUE KEY `password` (`password`),
  ADD UNIQUE KEY `password_salt` (`password_salt`),
  ADD KEY `pass` (`pass`),
  ADD KEY `group_id` (`group_id`),
  ADD KEY `owner` (`owner`),
  ADD KEY `online` (`online`),
  ADD KEY `combat` (`combat`),
  ADD KEY `skill_total` (`skill_total`),
  ADD KEY `login_ip` (`login_ip`),
  ADD KEY `combat_rank` (`combat_rank`),
  ADD KEY `skill_total_rank` (`skill_total_rank`),
  ADD KEY `kills_rank` (`kills_rank`),
  ADD KEY `deaths_rank` (`deaths_rank`),
  ADD KEY `login_date` (`login_date`),
  ADD KEY `delete_date` (`delete_date`),
  ADD KEY `creation_date` (`creation_date`),
  ADD KEY `avatar_items` (`avatar_items`);

--
-- Indexes for table `rscd_quests`
--
ALTER TABLE `rscd_quests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `rscd_quests_user` (`user`);

--
-- Indexes for table `search_cache`
--
ALTER TABLE `search_cache`
  ADD PRIMARY KEY (`id`),
  ADD KEY `search_cache_ident_idx` (`ident`(8));

--
-- Indexes for table `search_matches`
--
ALTER TABLE `search_matches`
  ADD KEY `search_matches_word_id_idx` (`word_id`),
  ADD KEY `search_matches_post_id_idx` (`post_id`);

--
-- Indexes for table `search_words`
--
ALTER TABLE `search_words`
  ADD PRIMARY KEY (`word`),
  ADD KEY `search_words_id_idx` (`id`);

--
-- Indexes for table `topics`
--
ALTER TABLE `topics`
  ADD PRIMARY KEY (`id`),
  ADD KEY `topics_forum_id_idx` (`forum_id`),
  ADD KEY `topics_moved_to_idx` (`moved_to`),
  ADD KEY `topics_last_post_idx` (`last_post`),
  ADD KEY `topics_first_post_id_idx` (`first_post_id`);

--
-- Indexes for table `topic_subscriptions`
--
ALTER TABLE `topic_subscriptions`
  ADD PRIMARY KEY (`user_id`,`topic_id`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_username_idx` (`username`(25)),
  ADD KEY `users_registered_idx` (`registered`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bans`
--
ALTER TABLE `bans`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `censoring`
--
ALTER TABLE `censoring`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `forums`
--
ALTER TABLE `forums`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;
--
-- AUTO_INCREMENT for table `groups`
--
ALTER TABLE `groups`
  MODIFY `g_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `polls`
--
ALTER TABLE `polls`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `posts`
--
ALTER TABLE `posts`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=743;
--
-- AUTO_INCREMENT for table `recovery_questions`
--
ALTER TABLE `recovery_questions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `reports`
--
ALTER TABLE `reports`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `rscd_auctions`
--
ALTER TABLE `rscd_auctions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `rscd_curstats`
--
ALTER TABLE `rscd_curstats`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1337;
--
-- AUTO_INCREMENT for table `rscd_experience`
--
ALTER TABLE `rscd_experience`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1337;
--
-- AUTO_INCREMENT for table `rscd_friends`
--
ALTER TABLE `rscd_friends`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2359406;
--
-- AUTO_INCREMENT for table `rscd_ignores`
--
ALTER TABLE `rscd_ignores`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6943;
--
-- AUTO_INCREMENT for table `rscd_players`
--
ALTER TABLE `rscd_players`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1337;
--
-- AUTO_INCREMENT for table `rscd_quests`
--
ALTER TABLE `rscd_quests`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=341;
--
-- AUTO_INCREMENT for table `search_words`
--
ALTER TABLE `search_words`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4989;
--
-- AUTO_INCREMENT for table `topics`
--
ALTER TABLE `topics`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=184;
--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=97;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=799;COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
