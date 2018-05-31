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
-- Database: `rscunity_logs`
--

-- --------------------------------------------------------

--
-- Table structure for table `game_bans`
--

CREATE TABLE `game_bans` (
  `id` int(10) NOT NULL,
  `account` int(11) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `player` varchar(40) NOT NULL,
  `reason` mediumtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_chat`
--

CREATE TABLE `game_chat` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `message` varchar(255) NOT NULL,
  `id` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_collect`
--

CREATE TABLE `game_collect` (
  `user` bigint(20) NOT NULL,
  `account` int(11) NOT NULL,
  `time` int(11) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `amount` bigint(11) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_connect`
--

CREATE TABLE `game_connect` (
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_death`
--

CREATE TABLE `game_death` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `time` int(10) NOT NULL,
  `x` int(4) NOT NULL,
  `y` int(4) NOT NULL,
  `id` int(6) NOT NULL,
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
  `item30_amount` int(4) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_drop`
--

CREATE TABLE `game_drop` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `x` int(4) NOT NULL,
  `y` int(4) NOT NULL,
  `item` int(4) NOT NULL,
  `amount` int(10) NOT NULL,
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_duel`
--

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
  `id` int(10) NOT NULL,
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
  `user2_amount8` int(10) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_error`
--

CREATE TABLE `game_error` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `error` varchar(255) NOT NULL,
  `id` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_event`
--

CREATE TABLE `game_event` (
  `id` int(10) NOT NULL,
  `user` varchar(40) NOT NULL,
  `account` int(11) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `message` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_exploit`
--

CREATE TABLE `game_exploit` (
  `user` bigint(20) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `exploit` varchar(500) NOT NULL,
  `id` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_generic`
--

CREATE TABLE `game_generic` (
  `message` varchar(255) NOT NULL,
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_global`
--

CREATE TABLE `game_global` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `message` varchar(255) NOT NULL,
  `id` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_login`
--

CREATE TABLE `game_login` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `uid` int(8) DEFAULT NULL,
  `time` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `id` int(6) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_pickup`
--

CREATE TABLE `game_pickup` (
  `user` varchar(40) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `x` int(4) NOT NULL,
  `y` int(4) NOT NULL,
  `item` int(4) NOT NULL,
  `amount` int(10) NOT NULL,
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_pm`
--

CREATE TABLE `game_pm` (
  `sender` varchar(40) NOT NULL,
  `sender_account` int(11) DEFAULT NULL,
  `sender_ip` varchar(15) NOT NULL,
  `reciever` varchar(40) NOT NULL,
  `reciever_account` int(11) DEFAULT NULL,
  `reciever_ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `message` varchar(255) NOT NULL,
  `id` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_redeem`
--

CREATE TABLE `game_redeem` (
  `user` bigint(20) NOT NULL,
  `account` int(11) NOT NULL,
  `time` int(11) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_report`
--

CREATE TABLE `game_report` (
  `id` int(10) NOT NULL,
  `user` bigint(20) NOT NULL,
  `account` int(11) DEFAULT NULL,
  `ip` varchar(15) NOT NULL,
  `reported` bigint(20) NOT NULL,
  `reported_account` int(11) DEFAULT NULL,
  `reported_ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `rule` smallint(2) NOT NULL,
  `resolved_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_report_actions`
--

CREATE TABLE `game_report_actions` (
  `id` int(10) NOT NULL,
  `report_id` int(10) NOT NULL,
  `account` int(10) NOT NULL,
  `time` int(10) NOT NULL,
  `action` tinyint(3) NOT NULL,
  `duration` int(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_report_comments`
--

CREATE TABLE `game_report_comments` (
  `id` int(10) NOT NULL,
  `report_id` int(10) NOT NULL,
  `account` int(10) NOT NULL,
  `date` int(10) NOT NULL,
  `message` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_script`
--

CREATE TABLE `game_script` (
  `id` int(11) UNSIGNED NOT NULL,
  `user` bigint(20) NOT NULL,
  `account` int(11) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(11) NOT NULL,
  `script` varchar(15) NOT NULL,
  `target` bigint(20) NOT NULL,
  `status` tinyint(1) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_shop`
--

CREATE TABLE `game_shop` (
  `user` bigint(30) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `account` int(10) NOT NULL,
  `item_id` int(11) NOT NULL,
  `item_amount` int(11) NOT NULL,
  `action` int(11) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `game_trade`
--

CREATE TABLE `game_trade` (
  `user1` varchar(40) NOT NULL,
  `account1` int(11) DEFAULT NULL,
  `user1_ip` varchar(15) NOT NULL,
  `user2` varchar(40) NOT NULL,
  `account2` int(11) DEFAULT NULL,
  `user2_ip` varchar(15) NOT NULL,
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL,
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
  `user2_amount12` int(10) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `loader_error`
--

CREATE TABLE `loader_error` (
  `id` int(10) NOT NULL,
  `message` text NOT NULL,
  `os` varchar(255) NOT NULL,
  `os_version` varchar(255) NOT NULL,
  `java_vendor` varchar(255) NOT NULL,
  `java_version` varchar(255) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `timestamp` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_bank_wipe_recovery`
--

CREATE TABLE `web_bank_wipe_recovery` (
  `owner` int(6) NOT NULL,
  `id` smallint(4) NOT NULL,
  `amount` int(10) NOT NULL DEFAULT '1',
  `slot` smallint(3) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `web_create`
--

CREATE TABLE `web_create` (
  `id` int(6) NOT NULL,
  `user` varchar(25) NOT NULL,
  `owner` int(6) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_delete`
--

CREATE TABLE `web_delete` (
  `id` int(4) NOT NULL,
  `user` varchar(25) NOT NULL,
  `owner` int(6) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_highscores`
--

CREATE TABLE `web_highscores` (
  `id` int(11) NOT NULL,
  `user` varchar(25) CHARACTER SET latin1 NOT NULL,
  `owner` int(6) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) CHARACTER SET latin1 NOT NULL DEFAULT '0.0.0.0',
  `hs_pref` varchar(255) CHARACTER SET latin1 NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `web_inv_wipe_recovery`
--

CREATE TABLE `web_inv_wipe_recovery` (
  `user` varchar(40) NOT NULL,
  `id` smallint(4) NOT NULL,
  `amount` int(10) NOT NULL DEFAULT '1',
  `wielded` tinyint(1) NOT NULL DEFAULT '0',
  `slot` tinyint(2) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `web_legacy_transfer`
--

CREATE TABLE `web_legacy_transfer` (
  `id` int(11) NOT NULL,
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
  `transferKey` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `web_login`
--

CREATE TABLE `web_login` (
  `ip` varchar(15) NOT NULL,
  `count` int(5) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_logins`
--

CREATE TABLE `web_logins` (
  `account` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `time` int(10) NOT NULL,
  `id` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_password`
--

CREATE TABLE `web_password` (
  `id` int(11) NOT NULL,
  `user` varchar(25) NOT NULL,
  `owner` int(6) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `password` varchar(32) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_recovery`
--

CREATE TABLE `web_recovery` (
  `account` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `date` int(10) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_reduce`
--

CREATE TABLE `web_reduce` (
  `user` bigint(23) NOT NULL,
  `time` int(10) NOT NULL,
  `xp_before` int(10) NOT NULL,
  `xp_after` int(10) NOT NULL,
  `stat` varchar(20) NOT NULL,
  `id` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_rename`
--

CREATE TABLE `web_rename` (
  `id` int(10) NOT NULL,
  `owner` int(10) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `old_hash` varchar(255) NOT NULL,
  `new_hash` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `web_rename_old`
--

CREATE TABLE `web_rename_old` (
  `id` int(10) NOT NULL,
  `user_id` int(10) NOT NULL,
  `old` varchar(12) NOT NULL,
  `old_hash` bigint(20) NOT NULL,
  `new` varchar(12) NOT NULL,
  `new_hash` bigint(20) NOT NULL,
  `time` int(10) NOT NULL,
  `account` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_staff_actions`
--

CREATE TABLE `web_staff_actions` (
  `id` int(10) NOT NULL,
  `owner` int(10) NOT NULL,
  `user` varchar(40) NOT NULL,
  `staff_id` int(10) NOT NULL,
  `ip` varchar(15) CHARACTER SET latin1 NOT NULL DEFAULT '0.0.0.0',
  `action_type` varchar(255) CHARACTER SET latin1 NOT NULL,
  `action_date` varchar(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `web_statistics`
--

CREATE TABLE `web_statistics` (
  `id` int(10) NOT NULL,
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
  `posts_today` int(4) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_transfer`
--

CREATE TABLE `web_transfer` (
  `id` int(4) NOT NULL,
  `user` varchar(25) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `from` int(6) NOT NULL,
  `to` int(6) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_v1transfer`
--

CREATE TABLE `web_v1transfer` (
  `id` int(4) NOT NULL,
  `user` varchar(25) NOT NULL,
  `date` int(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `from` int(6) NOT NULL,
  `to` int(6) NOT NULL,
  `transfer_log` text NOT NULL,
  `new_username` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `web_visit`
--

CREATE TABLE `web_visit` (
  `id` int(11) NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `time` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `game_bans`
--
ALTER TABLE `game_bans`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `game_chat`
--
ALTER TABLE `game_chat`
  ADD PRIMARY KEY (`id`),
  ADD KEY `message` (`message`),
  ADD KEY `ip` (`ip`),
  ADD KEY `user` (`user`);

--
-- Indexes for table `game_collect`
--
ALTER TABLE `game_collect`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`,`account`);

--
-- Indexes for table `game_connect`
--
ALTER TABLE `game_connect`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ip` (`ip`);

--
-- Indexes for table `game_death`
--
ALTER TABLE `game_death`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`),
  ADD KEY `ip` (`ip`);

--
-- Indexes for table `game_drop`
--
ALTER TABLE `game_drop`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`),
  ADD KEY `ip` (`ip`),
  ADD KEY `item` (`item`),
  ADD KEY `amount` (`amount`);

--
-- Indexes for table `game_duel`
--
ALTER TABLE `game_duel`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user1` (`user1`),
  ADD KEY `user1_ip` (`user1_ip`),
  ADD KEY `user2` (`user2`),
  ADD KEY `user2_ip` (`user2_ip`);

--
-- Indexes for table `game_error`
--
ALTER TABLE `game_error`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`),
  ADD KEY `ip` (`ip`);

--
-- Indexes for table `game_event`
--
ALTER TABLE `game_event`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `game_exploit`
--
ALTER TABLE `game_exploit`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`);

--
-- Indexes for table `game_generic`
--
ALTER TABLE `game_generic`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `game_global`
--
ALTER TABLE `game_global`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`),
  ADD KEY `ip` (`ip`),
  ADD KEY `message` (`message`);

--
-- Indexes for table `game_login`
--
ALTER TABLE `game_login`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`),
  ADD KEY `ip` (`ip`);

--
-- Indexes for table `game_pickup`
--
ALTER TABLE `game_pickup`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`),
  ADD KEY `ip` (`ip`),
  ADD KEY `item` (`item`),
  ADD KEY `amount` (`amount`);

--
-- Indexes for table `game_pm`
--
ALTER TABLE `game_pm`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sender` (`sender`),
  ADD KEY `sender_ip` (`sender_ip`),
  ADD KEY `reciever` (`reciever`),
  ADD KEY `reciever_ip` (`reciever_ip`),
  ADD KEY `message` (`message`);

--
-- Indexes for table `game_redeem`
--
ALTER TABLE `game_redeem`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`,`account`);

--
-- Indexes for table `game_report`
--
ALTER TABLE `game_report`
  ADD PRIMARY KEY (`id`),
  ADD KEY `rule` (`rule`),
  ADD KEY `reported` (`reported`),
  ADD KEY `user` (`user`),
  ADD KEY `reported_ip` (`reported_ip`),
  ADD KEY `ip` (`ip`);

--
-- Indexes for table `game_report_actions`
--
ALTER TABLE `game_report_actions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `game_report_comments`
--
ALTER TABLE `game_report_comments`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `game_script`
--
ALTER TABLE `game_script`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`);

--
-- Indexes for table `game_shop`
--
ALTER TABLE `game_shop`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`);

--
-- Indexes for table `game_trade`
--
ALTER TABLE `game_trade`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user1` (`user1`),
  ADD KEY `user1_ip` (`user1_ip`),
  ADD KEY `user2` (`user2`),
  ADD KEY `user2_ip` (`user2_ip`);

--
-- Indexes for table `loader_error`
--
ALTER TABLE `loader_error`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `web_bank_wipe_recovery`
--
ALTER TABLE `web_bank_wipe_recovery`
  ADD KEY `owner` (`owner`),
  ADD KEY `id` (`id`);

--
-- Indexes for table `web_create`
--
ALTER TABLE `web_create`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`),
  ADD KEY `owner` (`owner`),
  ADD KEY `ip` (`ip`);

--
-- Indexes for table `web_delete`
--
ALTER TABLE `web_delete`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `web_highscores`
--
ALTER TABLE `web_highscores`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`),
  ADD KEY `owner` (`owner`),
  ADD KEY `ip` (`ip`),
  ADD KEY `hs_pref` (`hs_pref`);

--
-- Indexes for table `web_inv_wipe_recovery`
--
ALTER TABLE `web_inv_wipe_recovery`
  ADD KEY `user` (`user`);

--
-- Indexes for table `web_legacy_transfer`
--
ALTER TABLE `web_legacy_transfer`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `web_login`
--
ALTER TABLE `web_login`
  ADD KEY `ip` (`ip`),
  ADD KEY `count` (`count`),
  ADD KEY `ip_2` (`ip`);

--
-- Indexes for table `web_logins`
--
ALTER TABLE `web_logins`
  ADD PRIMARY KEY (`id`),
  ADD KEY `account` (`account`);

--
-- Indexes for table `web_password`
--
ALTER TABLE `web_password`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`),
  ADD KEY `owner` (`owner`),
  ADD KEY `ip` (`ip`),
  ADD KEY `password` (`password`);

--
-- Indexes for table `web_recovery`
--
ALTER TABLE `web_recovery`
  ADD PRIMARY KEY (`id`),
  ADD KEY `account` (`account`,`ip`,`date`);

--
-- Indexes for table `web_reduce`
--
ALTER TABLE `web_reduce`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`,`time`);

--
-- Indexes for table `web_rename`
--
ALTER TABLE `web_rename`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`),
  ADD KEY `ip` (`ip`);

--
-- Indexes for table `web_rename_old`
--
ALTER TABLE `web_rename_old`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `id` (`id`),
  ADD KEY `user_id_2` (`user_id`),
  ADD KEY `old` (`old`),
  ADD KEY `old_hash` (`old_hash`),
  ADD KEY `new` (`new`),
  ADD KEY `new_hash` (`new_hash`),
  ADD KEY `account` (`account`),
  ADD KEY `ip` (`ip`);

--
-- Indexes for table `web_staff_actions`
--
ALTER TABLE `web_staff_actions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `web_statistics`
--
ALTER TABLE `web_statistics`
  ADD PRIMARY KEY (`id`),
  ADD KEY `time` (`time`);

--
-- Indexes for table `web_transfer`
--
ALTER TABLE `web_transfer`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `web_v1transfer`
--
ALTER TABLE `web_v1transfer`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `web_visit`
--
ALTER TABLE `web_visit`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ip` (`ip`),
  ADD KEY `ip_2` (`ip`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `game_bans`
--
ALTER TABLE `game_bans`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `game_chat`
--
ALTER TABLE `game_chat`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2714890;
--
-- AUTO_INCREMENT for table `game_collect`
--
ALTER TABLE `game_collect`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=90;
--
-- AUTO_INCREMENT for table `game_connect`
--
ALTER TABLE `game_connect`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `game_death`
--
ALTER TABLE `game_death`
  MODIFY `id` int(6) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1007128;
--
-- AUTO_INCREMENT for table `game_drop`
--
ALTER TABLE `game_drop`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=496013;
--
-- AUTO_INCREMENT for table `game_duel`
--
ALTER TABLE `game_duel`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=551932;
--
-- AUTO_INCREMENT for table `game_error`
--
ALTER TABLE `game_error`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33536;
--
-- AUTO_INCREMENT for table `game_event`
--
ALTER TABLE `game_event`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=382368;
--
-- AUTO_INCREMENT for table `game_exploit`
--
ALTER TABLE `game_exploit`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13756616;
--
-- AUTO_INCREMENT for table `game_generic`
--
ALTER TABLE `game_generic`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36780;
--
-- AUTO_INCREMENT for table `game_global`
--
ALTER TABLE `game_global`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=478867;
--
-- AUTO_INCREMENT for table `game_login`
--
ALTER TABLE `game_login`
  MODIFY `id` int(6) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4182434;
--
-- AUTO_INCREMENT for table `game_pickup`
--
ALTER TABLE `game_pickup`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3375365;
--
-- AUTO_INCREMENT for table `game_pm`
--
ALTER TABLE `game_pm`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11725351;
--
-- AUTO_INCREMENT for table `game_redeem`
--
ALTER TABLE `game_redeem`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=206;
--
-- AUTO_INCREMENT for table `game_report`
--
ALTER TABLE `game_report`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `game_report_actions`
--
ALTER TABLE `game_report_actions`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `game_report_comments`
--
ALTER TABLE `game_report_comments`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `game_script`
--
ALTER TABLE `game_script`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `game_shop`
--
ALTER TABLE `game_shop`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=75549;
--
-- AUTO_INCREMENT for table `game_trade`
--
ALTER TABLE `game_trade`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2333814;
--
-- AUTO_INCREMENT for table `loader_error`
--
ALTER TABLE `loader_error`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `web_create`
--
ALTER TABLE `web_create`
  MODIFY `id` int(6) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=89367;
--
-- AUTO_INCREMENT for table `web_delete`
--
ALTER TABLE `web_delete`
  MODIFY `id` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11249;
--
-- AUTO_INCREMENT for table `web_highscores`
--
ALTER TABLE `web_highscores`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43586;
--
-- AUTO_INCREMENT for table `web_legacy_transfer`
--
ALTER TABLE `web_legacy_transfer`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `web_logins`
--
ALTER TABLE `web_logins`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `web_password`
--
ALTER TABLE `web_password`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14842;
--
-- AUTO_INCREMENT for table `web_recovery`
--
ALTER TABLE `web_recovery`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `web_reduce`
--
ALTER TABLE `web_reduce`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `web_rename`
--
ALTER TABLE `web_rename`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2152;
--
-- AUTO_INCREMENT for table `web_rename_old`
--
ALTER TABLE `web_rename_old`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1443;
--
-- AUTO_INCREMENT for table `web_staff_actions`
--
ALTER TABLE `web_staff_actions`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9438;
--
-- AUTO_INCREMENT for table `web_statistics`
--
ALTER TABLE `web_statistics`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `web_transfer`
--
ALTER TABLE `web_transfer`
  MODIFY `id` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1274;
--
-- AUTO_INCREMENT for table `web_v1transfer`
--
ALTER TABLE `web_v1transfer`
  MODIFY `id` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8271;
--
-- AUTO_INCREMENT for table `web_visit`
--
ALTER TABLE `web_visit`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=156415;COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
