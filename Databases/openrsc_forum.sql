-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: mysql
-- Generation Time: Sep 06, 2018 at 01:43 AM
-- Server version: 10.3.9-MariaDB-1:10.3.9+maria~bionic
-- PHP Version: 7.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `openrsc_forum`
--
DROP DATABASE IF EXISTS `openrsc_forum`;
CREATE DATABASE IF NOT EXISTS `openrsc_forum` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `openrsc_forum`;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_acl_groups`
--

DROP TABLE IF EXISTS `phpbb_acl_groups`;
CREATE TABLE IF NOT EXISTS `phpbb_acl_groups` (
  `group_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `auth_option_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `auth_role_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `auth_setting` tinyint(2) NOT NULL DEFAULT 0,
  KEY `group_id` (`group_id`),
  KEY `auth_opt_id` (`auth_option_id`),
  KEY `auth_role_id` (`auth_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_acl_groups`
--

INSERT INTO `phpbb_acl_groups` (`group_id`, `forum_id`, `auth_option_id`, `auth_role_id`, `auth_setting`) VALUES
(1, 0, 91, 0, 1),
(1, 0, 100, 0, 1),
(1, 0, 118, 0, 1),
(5, 0, 0, 5, 0),
(5, 0, 0, 1, 0),
(2, 0, 0, 6, 0),
(3, 0, 0, 6, 0),
(4, 0, 0, 5, 0),
(4, 0, 0, 10, 0),
(7, 0, 0, 23, 0),
(5, 8, 0, 10, 0),
(5, 4, 0, 14, 0),
(6, 4, 0, 19, 0),
(4, 4, 0, 14, 0),
(1, 4, 0, 17, 0),
(7, 4, 0, 24, 0),
(2, 4, 0, 21, 0),
(3, 4, 0, 21, 0),
(5, 8, 0, 14, 0),
(6, 8, 0, 19, 0),
(4, 8, 0, 17, 0),
(1, 8, 0, 17, 0),
(7, 8, 0, 17, 0),
(2, 8, 0, 17, 0),
(3, 8, 0, 17, 0),
(5, 9, 0, 14, 0),
(6, 9, 0, 19, 0),
(4, 9, 0, 16, 0),
(1, 9, 0, 17, 0),
(7, 9, 0, 17, 0),
(2, 9, 0, 17, 0),
(5, 10, 0, 17, 0),
(6, 10, 0, 17, 0),
(4, 10, 0, 17, 0),
(1, 10, 0, 17, 0),
(7, 10, 0, 17, 0),
(2, 10, 0, 17, 0),
(5, 13, 0, 17, 0),
(6, 13, 0, 17, 0),
(4, 13, 0, 17, 0),
(1, 13, 0, 17, 0),
(7, 13, 0, 17, 0),
(2, 13, 0, 17, 0),
(5, 17, 0, 17, 0),
(6, 17, 0, 17, 0),
(4, 17, 0, 17, 0),
(1, 17, 0, 17, 0),
(7, 17, 0, 17, 0),
(2, 17, 0, 17, 0),
(5, 0, 127, 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_acl_options`
--

DROP TABLE IF EXISTS `phpbb_acl_options`;
CREATE TABLE IF NOT EXISTS `phpbb_acl_options` (
  `auth_option_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `auth_option` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `is_global` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `is_local` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `founder_only` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`auth_option_id`),
  UNIQUE KEY `auth_option` (`auth_option`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_acl_options`
--

INSERT INTO `phpbb_acl_options` (`auth_option_id`, `auth_option`, `is_global`, `is_local`, `founder_only`) VALUES
(1, 'f_', 0, 1, 0),
(2, 'f_announce', 0, 1, 0),
(3, 'f_announce_global', 0, 1, 0),
(4, 'f_attach', 0, 1, 0),
(5, 'f_bbcode', 0, 1, 0),
(6, 'f_bump', 0, 1, 0),
(7, 'f_delete', 0, 1, 0),
(8, 'f_download', 0, 1, 0),
(9, 'f_edit', 0, 1, 0),
(10, 'f_email', 0, 1, 0),
(11, 'f_flash', 0, 1, 0),
(12, 'f_icons', 0, 1, 0),
(13, 'f_ignoreflood', 0, 1, 0),
(14, 'f_img', 0, 1, 0),
(15, 'f_list', 0, 1, 0),
(16, 'f_list_topics', 0, 1, 0),
(17, 'f_noapprove', 0, 1, 0),
(18, 'f_poll', 0, 1, 0),
(19, 'f_post', 0, 1, 0),
(20, 'f_postcount', 0, 1, 0),
(21, 'f_print', 0, 1, 0),
(22, 'f_read', 0, 1, 0),
(23, 'f_reply', 0, 1, 0),
(24, 'f_report', 0, 1, 0),
(25, 'f_search', 0, 1, 0),
(26, 'f_sigs', 0, 1, 0),
(27, 'f_smilies', 0, 1, 0),
(28, 'f_sticky', 0, 1, 0),
(29, 'f_subscribe', 0, 1, 0),
(30, 'f_user_lock', 0, 1, 0),
(31, 'f_vote', 0, 1, 0),
(32, 'f_votechg', 0, 1, 0),
(33, 'f_softdelete', 0, 1, 0),
(34, 'm_', 1, 1, 0),
(35, 'm_approve', 1, 1, 0),
(36, 'm_chgposter', 1, 1, 0),
(37, 'm_delete', 1, 1, 0),
(38, 'm_edit', 1, 1, 0),
(39, 'm_info', 1, 1, 0),
(40, 'm_lock', 1, 1, 0),
(41, 'm_merge', 1, 1, 0),
(42, 'm_move', 1, 1, 0),
(43, 'm_report', 1, 1, 0),
(44, 'm_split', 1, 1, 0),
(45, 'm_softdelete', 1, 1, 0),
(46, 'm_ban', 1, 0, 0),
(47, 'm_pm_report', 1, 0, 0),
(48, 'm_warn', 1, 0, 0),
(49, 'a_', 1, 0, 0),
(50, 'a_aauth', 1, 0, 0),
(51, 'a_attach', 1, 0, 0),
(52, 'a_authgroups', 1, 0, 0),
(53, 'a_authusers', 1, 0, 0),
(54, 'a_backup', 1, 0, 0),
(55, 'a_ban', 1, 0, 0),
(56, 'a_bbcode', 1, 0, 0),
(57, 'a_board', 1, 0, 0),
(58, 'a_bots', 1, 0, 0),
(59, 'a_clearlogs', 1, 0, 0),
(60, 'a_email', 1, 0, 0),
(61, 'a_extensions', 1, 0, 0),
(62, 'a_fauth', 1, 0, 0),
(63, 'a_forum', 1, 0, 0),
(64, 'a_forumadd', 1, 0, 0),
(65, 'a_forumdel', 1, 0, 0),
(66, 'a_group', 1, 0, 0),
(67, 'a_groupadd', 1, 0, 0),
(68, 'a_groupdel', 1, 0, 0),
(69, 'a_icons', 1, 0, 0),
(70, 'a_jabber', 1, 0, 0),
(71, 'a_language', 1, 0, 0),
(72, 'a_mauth', 1, 0, 0),
(73, 'a_modules', 1, 0, 0),
(74, 'a_names', 1, 0, 0),
(75, 'a_phpinfo', 1, 0, 0),
(76, 'a_profile', 1, 0, 0),
(77, 'a_prune', 1, 0, 0),
(78, 'a_ranks', 1, 0, 0),
(79, 'a_reasons', 1, 0, 0),
(80, 'a_roles', 1, 0, 0),
(81, 'a_search', 1, 0, 0),
(82, 'a_server', 1, 0, 0),
(83, 'a_styles', 1, 0, 0),
(84, 'a_switchperm', 1, 0, 0),
(85, 'a_uauth', 1, 0, 0),
(86, 'a_user', 1, 0, 0),
(87, 'a_userdel', 1, 0, 0),
(88, 'a_viewauth', 1, 0, 0),
(89, 'a_viewlogs', 1, 0, 0),
(90, 'a_words', 1, 0, 0),
(91, 'u_', 1, 0, 0),
(92, 'u_attach', 1, 0, 0),
(93, 'u_chgavatar', 1, 0, 0),
(94, 'u_chgcensors', 1, 0, 0),
(95, 'u_chgemail', 1, 0, 0),
(96, 'u_chggrp', 1, 0, 0),
(97, 'u_chgname', 1, 0, 0),
(98, 'u_chgpasswd', 1, 0, 0),
(99, 'u_chgprofileinfo', 1, 0, 0),
(100, 'u_download', 1, 0, 0),
(101, 'u_hideonline', 1, 0, 0),
(102, 'u_ignoreflood', 1, 0, 0),
(103, 'u_masspm', 1, 0, 0),
(104, 'u_masspm_group', 1, 0, 0),
(105, 'u_pm_attach', 1, 0, 0),
(106, 'u_pm_bbcode', 1, 0, 0),
(107, 'u_pm_delete', 1, 0, 0),
(108, 'u_pm_download', 1, 0, 0),
(109, 'u_pm_edit', 1, 0, 0),
(110, 'u_pm_emailpm', 1, 0, 0),
(111, 'u_pm_flash', 1, 0, 0),
(112, 'u_pm_forward', 1, 0, 0),
(113, 'u_pm_img', 1, 0, 0),
(114, 'u_pm_printpm', 1, 0, 0),
(115, 'u_pm_smilies', 1, 0, 0),
(116, 'u_readpm', 1, 0, 0),
(117, 'u_savedrafts', 1, 0, 0),
(118, 'u_search', 1, 0, 0),
(119, 'u_sendemail', 1, 0, 0),
(120, 'u_sendim', 1, 0, 0),
(121, 'u_sendpm', 1, 0, 0),
(122, 'u_sig', 1, 0, 0),
(123, 'u_viewonline', 1, 0, 0),
(124, 'u_viewprofile', 1, 0, 0),
(125, 'f_mediaembed', 0, 1, 0),
(126, 'u_pm_mediaembed', 1, 0, 0),
(127, 'a_link_accounts', 1, 0, 0),
(128, 'u_link_accounts', 1, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_acl_roles`
--

DROP TABLE IF EXISTS `phpbb_acl_roles`;
CREATE TABLE IF NOT EXISTS `phpbb_acl_roles` (
  `role_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `role_description` text COLLATE utf8_bin NOT NULL,
  `role_type` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `role_order` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`role_id`),
  KEY `role_type` (`role_type`),
  KEY `role_order` (`role_order`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_acl_roles`
--

INSERT INTO `phpbb_acl_roles` (`role_id`, `role_name`, `role_description`, `role_type`, `role_order`) VALUES
(1, 'ROLE_ADMIN_STANDARD', 'ROLE_DESCRIPTION_ADMIN_STANDARD', 'a_', 1),
(2, 'ROLE_ADMIN_FORUM', 'ROLE_DESCRIPTION_ADMIN_FORUM', 'a_', 3),
(3, 'ROLE_ADMIN_USERGROUP', 'ROLE_DESCRIPTION_ADMIN_USERGROUP', 'a_', 4),
(4, 'ROLE_ADMIN_FULL', 'ROLE_DESCRIPTION_ADMIN_FULL', 'a_', 2),
(5, 'ROLE_USER_FULL', 'ROLE_DESCRIPTION_USER_FULL', 'u_', 3),
(6, 'ROLE_USER_STANDARD', 'ROLE_DESCRIPTION_USER_STANDARD', 'u_', 1),
(7, 'ROLE_USER_LIMITED', 'ROLE_DESCRIPTION_USER_LIMITED', 'u_', 2),
(8, 'ROLE_USER_NOPM', 'ROLE_DESCRIPTION_USER_NOPM', 'u_', 4),
(9, 'ROLE_USER_NOAVATAR', 'ROLE_DESCRIPTION_USER_NOAVATAR', 'u_', 5),
(10, 'ROLE_MOD_FULL', 'ROLE_DESCRIPTION_MOD_FULL', 'm_', 3),
(11, 'ROLE_MOD_STANDARD', 'ROLE_DESCRIPTION_MOD_STANDARD', 'm_', 1),
(12, 'ROLE_MOD_SIMPLE', 'ROLE_DESCRIPTION_MOD_SIMPLE', 'm_', 2),
(13, 'ROLE_MOD_QUEUE', 'ROLE_DESCRIPTION_MOD_QUEUE', 'm_', 4),
(14, 'ROLE_FORUM_FULL', 'ROLE_DESCRIPTION_FORUM_FULL', 'f_', 7),
(15, 'ROLE_FORUM_STANDARD', 'ROLE_DESCRIPTION_FORUM_STANDARD', 'f_', 5),
(16, 'ROLE_FORUM_NOACCESS', 'ROLE_DESCRIPTION_FORUM_NOACCESS', 'f_', 1),
(17, 'ROLE_FORUM_READONLY', 'ROLE_DESCRIPTION_FORUM_READONLY', 'f_', 2),
(18, 'ROLE_FORUM_LIMITED', 'ROLE_DESCRIPTION_FORUM_LIMITED', 'f_', 3),
(19, 'ROLE_FORUM_BOT', 'ROLE_DESCRIPTION_FORUM_BOT', 'f_', 9),
(20, 'ROLE_FORUM_ONQUEUE', 'ROLE_DESCRIPTION_FORUM_ONQUEUE', 'f_', 8),
(21, 'ROLE_FORUM_POLLS', 'ROLE_DESCRIPTION_FORUM_POLLS', 'f_', 6),
(22, 'ROLE_FORUM_LIMITED_POLLS', 'ROLE_DESCRIPTION_FORUM_LIMITED_POLLS', 'f_', 4),
(23, 'ROLE_USER_NEW_MEMBER', 'ROLE_DESCRIPTION_USER_NEW_MEMBER', 'u_', 6),
(24, 'ROLE_FORUM_NEW_MEMBER', 'ROLE_DESCRIPTION_FORUM_NEW_MEMBER', 'f_', 10);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_acl_roles_data`
--

DROP TABLE IF EXISTS `phpbb_acl_roles_data`;
CREATE TABLE IF NOT EXISTS `phpbb_acl_roles_data` (
  `role_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `auth_option_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `auth_setting` tinyint(2) NOT NULL DEFAULT 0,
  PRIMARY KEY (`role_id`,`auth_option_id`),
  KEY `ath_op_id` (`auth_option_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_acl_roles_data`
--

INSERT INTO `phpbb_acl_roles_data` (`role_id`, `auth_option_id`, `auth_setting`) VALUES
(1, 49, 1),
(1, 51, 1),
(1, 52, 1),
(1, 53, 1),
(1, 55, 1),
(1, 56, 1),
(1, 57, 1),
(1, 61, 1),
(1, 62, 1),
(1, 63, 1),
(1, 64, 1),
(1, 65, 1),
(1, 66, 1),
(1, 67, 1),
(1, 68, 1),
(1, 69, 1),
(1, 72, 1),
(1, 74, 1),
(1, 76, 1),
(1, 77, 1),
(1, 78, 1),
(1, 79, 1),
(1, 85, 1),
(1, 86, 1),
(1, 87, 1),
(1, 88, 1),
(1, 89, 1),
(1, 90, 1),
(1, 127, 1),
(2, 49, 1),
(2, 52, 1),
(2, 53, 1),
(2, 62, 1),
(2, 63, 1),
(2, 64, 1),
(2, 65, 1),
(2, 72, 1),
(2, 77, 1),
(2, 85, 1),
(2, 88, 1),
(2, 89, 1),
(3, 49, 1),
(3, 52, 1),
(3, 53, 1),
(3, 55, 1),
(3, 66, 1),
(3, 67, 1),
(3, 68, 1),
(3, 78, 1),
(3, 85, 1),
(3, 86, 1),
(3, 88, 1),
(3, 89, 1),
(4, 49, 1),
(4, 50, 1),
(4, 51, 1),
(4, 52, 1),
(4, 53, 1),
(4, 54, 1),
(4, 55, 1),
(4, 56, 1),
(4, 57, 1),
(4, 58, 1),
(4, 59, 1),
(4, 60, 1),
(4, 61, 1),
(4, 62, 1),
(4, 63, 1),
(4, 64, 1),
(4, 65, 1),
(4, 66, 1),
(4, 67, 1),
(4, 68, 1),
(4, 69, 1),
(4, 70, 1),
(4, 71, 1),
(4, 72, 1),
(4, 73, 1),
(4, 74, 1),
(4, 75, 1),
(4, 76, 1),
(4, 77, 1),
(4, 78, 1),
(4, 79, 1),
(4, 80, 1),
(4, 81, 1),
(4, 82, 1),
(4, 83, 1),
(4, 84, 1),
(4, 85, 1),
(4, 86, 1),
(4, 87, 1),
(4, 88, 1),
(4, 89, 1),
(4, 90, 1),
(5, 91, 1),
(5, 92, 1),
(5, 93, 1),
(5, 94, 1),
(5, 95, 1),
(5, 96, 1),
(5, 97, 1),
(5, 98, 1),
(5, 99, 1),
(5, 100, 1),
(5, 101, 1),
(5, 102, 1),
(5, 103, 1),
(5, 104, 1),
(5, 105, 1),
(5, 106, 1),
(5, 107, 1),
(5, 108, 1),
(5, 109, 1),
(5, 110, 1),
(5, 111, 1),
(5, 112, 1),
(5, 113, 1),
(5, 114, 1),
(5, 115, 1),
(5, 116, 1),
(5, 117, 1),
(5, 118, 1),
(5, 119, 1),
(5, 120, 1),
(5, 121, 1),
(5, 122, 1),
(5, 123, 1),
(5, 124, 1),
(5, 126, 1),
(6, 91, 1),
(6, 92, 1),
(6, 93, 1),
(6, 94, 1),
(6, 95, 1),
(6, 98, 1),
(6, 99, 1),
(6, 100, 1),
(6, 101, 1),
(6, 103, 1),
(6, 104, 1),
(6, 105, 1),
(6, 106, 1),
(6, 107, 1),
(6, 108, 1),
(6, 109, 1),
(6, 110, 1),
(6, 113, 1),
(6, 114, 1),
(6, 115, 1),
(6, 116, 1),
(6, 117, 1),
(6, 118, 1),
(6, 119, 1),
(6, 120, 1),
(6, 121, 1),
(6, 122, 1),
(6, 124, 1),
(6, 126, 1),
(6, 128, 1),
(7, 91, 1),
(7, 93, 1),
(7, 94, 1),
(7, 95, 1),
(7, 98, 1),
(7, 99, 1),
(7, 100, 1),
(7, 101, 1),
(7, 106, 1),
(7, 107, 1),
(7, 108, 1),
(7, 109, 1),
(7, 112, 1),
(7, 113, 1),
(7, 114, 1),
(7, 115, 1),
(7, 116, 1),
(7, 121, 1),
(7, 122, 1),
(7, 124, 1),
(8, 91, 1),
(8, 93, 1),
(8, 94, 1),
(8, 95, 1),
(8, 98, 1),
(8, 100, 1),
(8, 101, 1),
(8, 103, 0),
(8, 104, 0),
(8, 116, 0),
(8, 121, 0),
(8, 122, 1),
(8, 124, 1),
(9, 91, 1),
(9, 93, 0),
(9, 94, 1),
(9, 95, 1),
(9, 98, 1),
(9, 99, 1),
(9, 100, 1),
(9, 101, 1),
(9, 106, 1),
(9, 107, 1),
(9, 108, 1),
(9, 109, 1),
(9, 112, 1),
(9, 113, 1),
(9, 114, 1),
(9, 115, 1),
(9, 116, 1),
(9, 121, 1),
(9, 122, 1),
(9, 124, 1),
(10, 34, 1),
(10, 35, 1),
(10, 36, 1),
(10, 37, 1),
(10, 38, 1),
(10, 39, 1),
(10, 40, 1),
(10, 41, 1),
(10, 42, 1),
(10, 43, 1),
(10, 44, 1),
(10, 45, 1),
(10, 46, 1),
(10, 47, 1),
(10, 48, 1),
(11, 34, 1),
(11, 35, 1),
(11, 37, 1),
(11, 38, 1),
(11, 39, 1),
(11, 40, 1),
(11, 41, 1),
(11, 42, 1),
(11, 43, 1),
(11, 44, 1),
(11, 45, 1),
(11, 47, 1),
(11, 48, 1),
(12, 34, 1),
(12, 37, 1),
(12, 38, 1),
(12, 39, 1),
(12, 43, 1),
(12, 45, 1),
(12, 47, 1),
(13, 34, 1),
(13, 35, 1),
(13, 38, 1),
(14, 1, 1),
(14, 2, 1),
(14, 3, 1),
(14, 4, 1),
(14, 5, 1),
(14, 6, 1),
(14, 7, 1),
(14, 8, 1),
(14, 9, 1),
(14, 10, 1),
(14, 11, 1),
(14, 12, 1),
(14, 13, 1),
(14, 14, 1),
(14, 15, 1),
(14, 16, 1),
(14, 17, 1),
(14, 18, 1),
(14, 19, 1),
(14, 20, 1),
(14, 21, 1),
(14, 22, 1),
(14, 23, 1),
(14, 24, 1),
(14, 25, 1),
(14, 26, 1),
(14, 27, 1),
(14, 28, 1),
(14, 29, 1),
(14, 30, 1),
(14, 31, 1),
(14, 32, 1),
(14, 33, 1),
(14, 125, 1),
(15, 1, 1),
(15, 4, 1),
(15, 5, 1),
(15, 6, 1),
(15, 7, 1),
(15, 8, 1),
(15, 9, 1),
(15, 10, 1),
(15, 12, 1),
(15, 14, 1),
(15, 15, 1),
(15, 16, 1),
(15, 17, 1),
(15, 19, 1),
(15, 20, 1),
(15, 21, 1),
(15, 22, 1),
(15, 23, 1),
(15, 24, 1),
(15, 25, 1),
(15, 26, 1),
(15, 27, 1),
(15, 29, 1),
(15, 31, 1),
(15, 32, 1),
(15, 33, 1),
(15, 125, 1),
(16, 1, 0),
(17, 1, 1),
(17, 8, 1),
(17, 15, 1),
(17, 16, 1),
(17, 21, 1),
(17, 22, 1),
(17, 25, 1),
(17, 29, 1),
(18, 1, 1),
(18, 5, 1),
(18, 8, 1),
(18, 9, 1),
(18, 10, 1),
(18, 14, 1),
(18, 15, 1),
(18, 16, 1),
(18, 17, 1),
(18, 19, 1),
(18, 20, 1),
(18, 21, 1),
(18, 22, 1),
(18, 23, 1),
(18, 24, 1),
(18, 25, 1),
(18, 26, 1),
(18, 27, 1),
(18, 29, 1),
(18, 31, 1),
(18, 33, 1),
(19, 1, 1),
(19, 8, 1),
(19, 15, 1),
(19, 16, 1),
(19, 21, 1),
(19, 22, 1),
(20, 1, 1),
(20, 4, 1),
(20, 5, 1),
(20, 8, 1),
(20, 9, 1),
(20, 10, 1),
(20, 14, 1),
(20, 15, 1),
(20, 16, 1),
(20, 17, 0),
(20, 19, 1),
(20, 20, 1),
(20, 21, 1),
(20, 22, 1),
(20, 23, 1),
(20, 24, 1),
(20, 25, 1),
(20, 26, 1),
(20, 27, 1),
(20, 29, 1),
(20, 31, 1),
(20, 33, 1),
(20, 125, 1),
(21, 1, 1),
(21, 4, 1),
(21, 5, 1),
(21, 6, 1),
(21, 7, 1),
(21, 8, 1),
(21, 9, 1),
(21, 10, 1),
(21, 12, 1),
(21, 14, 1),
(21, 15, 1),
(21, 16, 1),
(21, 17, 1),
(21, 18, 1),
(21, 19, 1),
(21, 20, 1),
(21, 21, 1),
(21, 22, 1),
(21, 23, 1),
(21, 24, 1),
(21, 25, 1),
(21, 26, 1),
(21, 27, 1),
(21, 29, 1),
(21, 31, 1),
(21, 32, 1),
(21, 33, 1),
(21, 125, 1),
(22, 1, 1),
(22, 5, 1),
(22, 8, 1),
(22, 9, 1),
(22, 10, 1),
(22, 14, 1),
(22, 15, 1),
(22, 16, 1),
(22, 17, 1),
(22, 18, 1),
(22, 19, 1),
(22, 20, 1),
(22, 21, 1),
(22, 22, 1),
(22, 23, 1),
(22, 24, 1),
(22, 25, 1),
(22, 26, 1),
(22, 27, 1),
(22, 29, 1),
(22, 31, 1),
(22, 33, 1),
(23, 99, 0),
(23, 103, 0),
(23, 104, 0),
(23, 121, 0),
(24, 17, 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_acl_users`
--

DROP TABLE IF EXISTS `phpbb_acl_users`;
CREATE TABLE IF NOT EXISTS `phpbb_acl_users` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `auth_option_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `auth_role_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `auth_setting` tinyint(2) NOT NULL DEFAULT 0,
  KEY `user_id` (`user_id`),
  KEY `auth_option_id` (`auth_option_id`),
  KEY `auth_role_id` (`auth_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_acl_users`
--

INSERT INTO `phpbb_acl_users` (`user_id`, `forum_id`, `auth_option_id`, `auth_role_id`, `auth_setting`) VALUES
(2, 0, 0, 5, 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_attachments`
--

DROP TABLE IF EXISTS `phpbb_attachments`;
CREATE TABLE IF NOT EXISTS `phpbb_attachments` (
  `attach_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `post_msg_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `in_message` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `poster_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `is_orphan` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `physical_filename` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `real_filename` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `download_count` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `attach_comment` text COLLATE utf8_bin NOT NULL,
  `extension` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '',
  `mimetype` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '',
  `filesize` int(20) UNSIGNED NOT NULL DEFAULT 0,
  `filetime` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `thumbnail` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`attach_id`),
  KEY `filetime` (`filetime`),
  KEY `post_msg_id` (`post_msg_id`),
  KEY `topic_id` (`topic_id`),
  KEY `poster_id` (`poster_id`),
  KEY `is_orphan` (`is_orphan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_banlist`
--

DROP TABLE IF EXISTS `phpbb_banlist`;
CREATE TABLE IF NOT EXISTS `phpbb_banlist` (
  `ban_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `ban_userid` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `ban_ip` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `ban_email` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '',
  `ban_start` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `ban_end` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `ban_exclude` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `ban_reason` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `ban_give_reason` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`ban_id`),
  KEY `ban_end` (`ban_end`),
  KEY `ban_user` (`ban_userid`,`ban_exclude`),
  KEY `ban_email` (`ban_email`,`ban_exclude`),
  KEY `ban_ip` (`ban_ip`,`ban_exclude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_bbcodes`
--

DROP TABLE IF EXISTS `phpbb_bbcodes`;
CREATE TABLE IF NOT EXISTS `phpbb_bbcodes` (
  `bbcode_id` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `bbcode_tag` varchar(16) COLLATE utf8_bin NOT NULL DEFAULT '',
  `bbcode_helpline` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `display_on_posting` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `bbcode_match` text COLLATE utf8_bin NOT NULL,
  `bbcode_tpl` mediumtext COLLATE utf8_bin NOT NULL,
  `first_pass_match` mediumtext COLLATE utf8_bin NOT NULL,
  `first_pass_replace` mediumtext COLLATE utf8_bin NOT NULL,
  `second_pass_match` mediumtext COLLATE utf8_bin NOT NULL,
  `second_pass_replace` mediumtext COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`bbcode_id`),
  KEY `display_on_post` (`display_on_posting`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_bbcodes`
--

INSERT INTO `phpbb_bbcodes` (`bbcode_id`, `bbcode_tag`, `bbcode_helpline`, `display_on_posting`, `bbcode_match`, `bbcode_tpl`, `first_pass_match`, `first_pass_replace`, `second_pass_match`, `second_pass_replace`) VALUES
(13, 's', '', 1, '[s]{TEXT}[/s]', '<span style=\"text-decoration:line-through;\">{TEXT}</span>', '!\\[s\\](.*?)\\[/s\\]!ies', '\'[s:$uid]\'.str_replace(array(\"\\r\\n\", \'\\\"\', \'\\\'\', \'(\', \')\'), array(\"\\n\", \'\"\', \'&#39;\', \'&#40;\', \'&#41;\'), trim(\'${1}\')).\'[/s:$uid]\'', '!\\[s:$uid\\](.*?)\\[/s:$uid\\]!s', '<span style=\"text-decoration:line-through;\">${1}</span>');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_bookmarks`
--

DROP TABLE IF EXISTS `phpbb_bookmarks`;
CREATE TABLE IF NOT EXISTS `phpbb_bookmarks` (
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`topic_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_bots`
--

DROP TABLE IF EXISTS `phpbb_bots`;
CREATE TABLE IF NOT EXISTS `phpbb_bots` (
  `bot_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `bot_active` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `bot_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `bot_agent` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `bot_ip` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`bot_id`),
  KEY `bot_active` (`bot_active`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_bots`
--

INSERT INTO `phpbb_bots` (`bot_id`, `bot_active`, `bot_name`, `user_id`, `bot_agent`, `bot_ip`) VALUES
(1, 1, 'AdsBot [Google]', 3, 'AdsBot-Google', ''),
(2, 1, 'Alexa [Bot]', 4, 'ia_archiver', ''),
(3, 1, 'Alta Vista [Bot]', 5, 'Scooter/', ''),
(4, 1, 'Ask Jeeves [Bot]', 6, 'Ask Jeeves', ''),
(5, 1, 'Baidu [Spider]', 7, 'Baiduspider', ''),
(6, 1, 'Bing [Bot]', 8, 'bingbot/', ''),
(7, 1, 'Exabot [Bot]', 9, 'Exabot', ''),
(8, 1, 'FAST Enterprise [Crawler]', 10, 'FAST Enterprise Crawler', ''),
(9, 1, 'FAST WebCrawler [Crawler]', 11, 'FAST-WebCrawler/', ''),
(10, 1, 'Francis [Bot]', 12, 'http://www.neomo.de/', ''),
(11, 1, 'Gigabot [Bot]', 13, 'Gigabot/', ''),
(12, 1, 'Google Adsense [Bot]', 14, 'Mediapartners-Google', ''),
(13, 1, 'Google Desktop', 15, 'Google Desktop', ''),
(14, 1, 'Google Feedfetcher', 16, 'Feedfetcher-Google', ''),
(15, 1, 'Google [Bot]', 17, 'Googlebot', ''),
(16, 1, 'Heise IT-Markt [Crawler]', 18, 'heise-IT-Markt-Crawler', ''),
(17, 1, 'Heritrix [Crawler]', 19, 'heritrix/1.', ''),
(18, 1, 'IBM Research [Bot]', 20, 'ibm.com/cs/crawler', ''),
(19, 1, 'ICCrawler - ICjobs', 21, 'ICCrawler - ICjobs', ''),
(20, 1, 'ichiro [Crawler]', 22, 'ichiro/', ''),
(21, 1, 'Majestic-12 [Bot]', 23, 'MJ12bot/', ''),
(22, 1, 'Metager [Bot]', 24, 'MetagerBot/', ''),
(23, 1, 'MSN NewsBlogs', 25, 'msnbot-NewsBlogs/', ''),
(24, 1, 'MSN [Bot]', 26, 'msnbot/', ''),
(25, 1, 'MSNbot Media', 27, 'msnbot-media/', ''),
(26, 1, 'Nutch [Bot]', 28, 'http://lucene.apache.org/nutch/', ''),
(27, 1, 'Online link [Validator]', 29, 'online link validator', ''),
(28, 1, 'psbot [Picsearch]', 30, 'psbot/0', ''),
(29, 1, 'Sensis [Crawler]', 31, 'Sensis Web Crawler', ''),
(30, 1, 'SEO Crawler', 32, 'SEO search Crawler/', ''),
(31, 1, 'Seoma [Crawler]', 33, 'Seoma [SEO Crawler]', ''),
(32, 1, 'SEOSearch [Crawler]', 34, 'SEOsearch/', ''),
(33, 1, 'Snappy [Bot]', 35, 'Snappy/1.1 ( http://www.urltrends.com/ )', ''),
(34, 1, 'Steeler [Crawler]', 36, 'http://www.tkl.iis.u-tokyo.ac.jp/~crawler/', ''),
(35, 1, 'Telekom [Bot]', 37, 'crawleradmin.t-info@telekom.de', ''),
(36, 1, 'TurnitinBot [Bot]', 38, 'TurnitinBot/', ''),
(37, 1, 'Voyager [Bot]', 39, 'voyager/', ''),
(38, 1, 'W3 [Sitesearch]', 40, 'W3 SiteSearch Crawler', ''),
(39, 1, 'W3C [Linkcheck]', 41, 'W3C-checklink/', ''),
(40, 1, 'W3C [Validator]', 42, 'W3C_Validator', ''),
(41, 1, 'YaCy [Bot]', 43, 'yacybot', ''),
(42, 1, 'Yahoo MMCrawler [Bot]', 44, 'Yahoo-MMCrawler/', ''),
(43, 1, 'Yahoo Slurp [Bot]', 45, 'Yahoo! DE Slurp', ''),
(44, 1, 'Yahoo [Bot]', 46, 'Yahoo! Slurp', ''),
(45, 1, 'YahooSeeker [Bot]', 47, 'YahooSeeker/', '');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_captcha_answers`
--

DROP TABLE IF EXISTS `phpbb_captcha_answers`;
CREATE TABLE IF NOT EXISTS `phpbb_captcha_answers` (
  `question_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `answer_text` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  KEY `qid` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_captcha_questions`
--

DROP TABLE IF EXISTS `phpbb_captcha_questions`;
CREATE TABLE IF NOT EXISTS `phpbb_captcha_questions` (
  `question_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `strict` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `lang_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `lang_iso` varchar(30) COLLATE utf8_bin NOT NULL DEFAULT '',
  `question_text` text COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`question_id`),
  KEY `lang` (`lang_iso`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_config`
--

DROP TABLE IF EXISTS `phpbb_config`;
CREATE TABLE IF NOT EXISTS `phpbb_config` (
  `config_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `config_value` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `is_dynamic` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`config_name`),
  KEY `is_dynamic` (`is_dynamic`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_config`
--

INSERT INTO `phpbb_config` (`config_name`, `config_value`, `is_dynamic`) VALUES
('active_sessions', '0', 0),
('allow_attachments', '1', 0),
('allow_autologin', '1', 0),
('allow_avatar', '1', 0),
('allow_avatar_gravatar', '1', 0),
('allow_avatar_local', '1', 0),
('allow_avatar_remote', '0', 0),
('allow_avatar_remote_upload', '0', 0),
('allow_avatar_upload', '1', 0),
('allow_bbcode', '1', 0),
('allow_birthdays', '1', 0),
('allow_board_notifications', '1', 0),
('allow_bookmarks', '1', 0),
('allow_cdn', '0', 0),
('allow_emailreuse', '1', 0),
('allow_forum_notify', '1', 0),
('allow_live_searches', '1', 0),
('allow_mass_pm', '1', 0),
('allow_name_chars', 'USERNAME_CHARS_ANY', 0),
('allow_namechange', '1', 0),
('allow_nocensors', '0', 0),
('allow_password_reset', '1', 0),
('allow_pm_attach', '0', 0),
('allow_pm_report', '1', 0),
('allow_post_flash', '1', 0),
('allow_post_links', '1', 0),
('allow_privmsg', '1', 0),
('allow_quick_reply', '1', 0),
('allow_sig', '1', 0),
('allow_sig_bbcode', '1', 0),
('allow_sig_flash', '0', 0),
('allow_sig_img', '0', 0),
('allow_sig_links', '0', 0),
('allow_sig_pm', '1', 0),
('allow_sig_smilies', '1', 0),
('allow_smilies', '1', 0),
('allow_topic_notify', '1', 0),
('allow_user_recent_activity', '1', 0),
('allowed_schemes_links', 'http,https', 0),
('assets_version', '10', 0),
('attachment_quota', '52428800', 0),
('auth_bbcode_pm', '1', 0),
('auth_flash_pm', '0', 0),
('auth_img_pm', '1', 0),
('auth_method', 'db', 0),
('auth_oauth_bitly_key', '', 0),
('auth_oauth_bitly_secret', '', 0),
('auth_oauth_facebook_key', '', 0),
('auth_oauth_facebook_secret', '', 0),
('auth_oauth_google_key', '', 0),
('auth_oauth_google_secret', '', 0),
('auth_oauth_twitter_key', '', 0),
('auth_oauth_twitter_secret', '', 0),
('auth_smilies_pm', '1', 0),
('avatar_filesize', '153600', 0),
('avatar_gallery_path', 'images/avatars/gallery', 0),
('avatar_max_height', '150', 0),
('avatar_max_width', '150', 0),
('avatar_min_height', '20', 0),
('avatar_min_width', '20', 0),
('avatar_path', 'images/avatars/upload', 0),
('avatar_salt', '6898494b149be4cbf3cd3edb777aadb4', 0),
('bh_ban_email', '1', 0),
('bh_ban_ip', '0', 0),
('bh_ban_time', '0', 0),
('bh_del_avatar', '1', 0),
('bh_del_posts', '1', 0),
('bh_del_privmsgs', '1', 0),
('bh_del_profile', '1', 0),
('bh_del_signature', '1', 0),
('bh_group_id', '0', 0),
('bh_sfs_api_key', '', 0),
('board_contact', 'cleako@gmail.com', 0),
('board_contact_name', '', 0),
('board_disable', '0', 0),
('board_disable_msg', '', 0),
('board_email', 'cleako@gmail.com', 0),
('board_email_form', '1', 0),
('board_email_sig', '', 0),
('board_hide_emails', '1', 0),
('board_index_text', '', 0),
('board_startdate', '1536197775', 0),
('board_timezone', 'America/New_York', 0),
('browser_check', '1', 0),
('bump_interval', '10', 0),
('bump_type', 'd', 0),
('cache_gc', '7200', 0),
('cache_last_gc', '1536194755', 1),
('captcha_gd', '0', 0),
('captcha_gd_3d_noise', '1', 0),
('captcha_gd_fonts', '1', 0),
('captcha_gd_foreground_noise', '0', 0),
('captcha_gd_wave', '0', 0),
('captcha_gd_x_grid', '25', 0),
('captcha_gd_y_grid', '25', 0),
('captcha_plugin', 'core.captcha.plugins.recaptcha', 0),
('check_attachment_content', '1', 0),
('check_dnsbl', '0', 0),
('chg_passforce', '0', 0),
('confirm_refresh', '1', 0),
('contact_admin_form_enable', '', 0),
('cookie_domain', 'localhost', 0),
('cookie_name', 'phpbb3_openrsc_local', 0),
('cookie_notice', '0', 0),
('cookie_path', '/', 0),
('cookie_secure', '0', 0),
('coppa_enable', '0', 0),
('coppa_fax', '', 0),
('coppa_mail', '', 0),
('cron_lock', '0', 1),
('database_gc', '604800', 0),
('database_last_gc', '1536194759', 1),
('dbms_version', '10.1.29-MariaDB-6', 0),
('default_dateformat', 'D M d, Y g:i a', 0),
('default_lang', 'en', 0),
('default_style', '3', 0),
('delete_time', '0', 0),
('display_last_edited', '1', 0),
('display_last_subject', '1', 0),
('display_order', '0', 0),
('edit_time', '0', 0),
('email_check_mx', '1', 0),
('email_enable', '1', 0),
('email_force_sender', '0', 0),
('email_max_chunk_size', '50', 0),
('email_package_size', '20', 0),
('enable_confirm', '1', 0),
('enable_mod_rewrite', '0', 0),
('enable_pm_icons', '1', 0),
('enable_post_confirm', '1', 0),
('extension_force_unstable', '0', 0),
('feed_enable', '1', 0),
('feed_forum', '1', 0),
('feed_http_auth', '0', 0),
('feed_item_statistics', '1', 0),
('feed_limit_post', '15', 0),
('feed_limit_topic', '10', 0),
('feed_overall', '1', 0),
('feed_overall_forums', '1', 0),
('feed_topic', '1', 0),
('feed_topics_active', '1', 0),
('feed_topics_new', '1', 0),
('flood_interval', '15', 0),
('force_server_vars', '1', 0),
('form_token_lifetime', '7200', 0),
('form_token_mintime', '0', 0),
('form_token_sid_guests', '1', 0),
('forward_pm', '1', 0),
('forwarded_for_check', '0', 0),
('full_folder_action', '2', 0),
('fulltext_mysql_max_word_len', '84', 0),
('fulltext_mysql_min_word_len', '4', 0),
('fulltext_native_common_thres', '5', 0),
('fulltext_native_load_upd', '1', 0),
('fulltext_native_max_chars', '14', 0),
('fulltext_native_min_chars', '3', 0),
('fulltext_postgres_max_word_len', '254', 0),
('fulltext_postgres_min_word_len', '4', 0),
('fulltext_postgres_ts_name', 'simple', 0),
('fulltext_sphinx_id', 'c410dfb16beee7ce', 0),
('fulltext_sphinx_indexer_mem_limit', '512', 0),
('fulltext_sphinx_stopwords', '0', 0),
('gzip_compress', '0', 0),
('help_send_statistics', '', 0),
('help_send_statistics_time', '1529675843', 0),
('hot_threshold', '25', 0),
('icons_path', 'images/icons', 0),
('img_create_thumbnail', '0', 0),
('img_display_inlined', '1', 0),
('img_imagick', '', 0),
('img_link_height', '0', 0),
('img_link_width', '0', 0),
('img_max_height', '0', 0),
('img_max_thumb_width', '400', 0),
('img_max_width', '0', 0),
('img_min_thumb_filesize', '12000', 0),
('ip_check', '3', 0),
('ip_login_limit_max', '50', 0),
('ip_login_limit_time', '21600', 0),
('ip_login_limit_use_forwarded', '0', 0),
('jab_enable', '0', 0),
('jab_host', '', 0),
('jab_package_size', '20', 0),
('jab_password', '', 0),
('jab_port', '5222', 0),
('jab_use_ssl', '0', 0),
('jab_username', '', 0),
('last_queue_run', '0', 1),
('ldap_base_dn', '', 0),
('ldap_email', '', 0),
('ldap_password', '', 0),
('ldap_port', '', 0),
('ldap_server', '', 0),
('ldap_uid', '', 0),
('ldap_user', '', 0),
('ldap_user_filter', '', 0),
('legend_sort_groupname', '0', 0),
('limit_load', '0', 0),
('limit_search_load', '0', 0),
('load_anon_lastread', '0', 0),
('load_birthdays', '1', 0),
('load_cpf_memberlist', '1', 0),
('load_cpf_pm', '1', 0),
('load_cpf_viewprofile', '1', 0),
('load_cpf_viewtopic', '1', 0),
('load_db_lastread', '1', 0),
('load_db_track', '1', 0),
('load_jquery_url', '//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js', 0),
('load_jumpbox', '1', 0),
('load_moderators', '1', 0),
('load_notifications', '1', 0),
('load_online', '1', 0),
('load_online_guests', '1', 0),
('load_online_time', '5', 0),
('load_onlinetrack', '1', 0),
('load_search', '1', 0),
('load_tplcompile', '0', 0),
('load_unreads_search', '1', 0),
('load_user_activity', '1', 0),
('load_user_activity_limit', '5000', 0),
('max_attachments', '3', 0),
('max_attachments_pm', '1', 0),
('max_autologin_time', '0', 0),
('max_filesize', '262144', 0),
('max_filesize_pm', '262144', 0),
('max_login_attempts', '5', 0),
('max_name_chars', '20', 0),
('max_num_search_keywords', '10', 0),
('max_pass_chars', '100', 0),
('max_poll_options', '10', 0),
('max_post_chars', '60000', 0),
('max_post_font_size', '200', 0),
('max_post_img_height', '0', 0),
('max_post_img_width', '0', 0),
('max_post_smilies', '0', 0),
('max_post_urls', '0', 0),
('max_quote_depth', '3', 0),
('max_reg_attempts', '5', 0),
('max_sig_chars', '255', 0),
('max_sig_font_size', '200', 0),
('max_sig_img_height', '0', 0),
('max_sig_img_width', '0', 0),
('max_sig_smilies', '20', 0),
('max_sig_urls', '5', 0),
('media_embed_allow_sig', '0', 0),
('media_embed_bbcode', '1', 0),
('media_embed_parse_urls', '1', 0),
('mime_triggers', 'body|head|html|img|plaintext|a href|pre|script|table|title', 0),
('min_name_chars', '3', 0),
('min_pass_chars', '6', 0),
('min_post_chars', '1', 0),
('min_search_author_chars', '3', 0),
('new_member_group_default', '0', 0),
('new_member_post_limit', '3', 0),
('newest_user_colour', 'AA0000', 1),
('newest_user_id', '2', 1),
('newest_username', 'Administrator', 1),
('num_files', '0', 1),
('num_posts', '0', 1),
('num_topics', '0', 1),
('num_users', '1', 1),
('number_char_post', '100', 0),
('number_user_recent_activity', '3', 0),
('override_user_style', '0', 0),
('pass_complex', 'PASS_TYPE_ANY', 0),
('plupload_last_gc', '0', 1),
('plupload_salt', '24353f734ca10df4f04a8f59a87bb799', 0),
('pm_edit_time', '0', 0),
('pm_max_boxes', '4', 0),
('pm_max_msgs', '50', 0),
('pm_max_recipients', '0', 0),
('posts_per_page', '10', 0),
('print_pm', '1', 0),
('questionnaire_unique_id', 'c7a8d4d250e674b4', 0),
('queue_interval', '60', 0),
('rand_seed', 'f0372e84a35a25c8a4714b3796a5f181', 1),
('rand_seed_last_update', '1529771940', 1),
('ranks_path', 'images/ranks', 0),
('read_notification_expire_days', '30', 0),
('read_notification_gc', '86400', 0),
('read_notification_last_gc', '1536194657', 1),
('recaptcha_privkey', '6LdOSWAUAAAAAPqEsMaRJzj3VjUIhvQdnfNn8ZhC', 0),
('recaptcha_pubkey', '6LdOSWAUAAAAAJBOnf566Upc0nY9HM3YEBZ4Qzpa', 0),
('record_online_date', '1536197774', 1),
('record_online_users', '1', 1),
('referer_validation', '0', 0),
('remote_upload_verify', '0', 0),
('require_activation', '0', 0),
('script_path', '/board', 0),
('search_anonymous_interval', '0', 0),
('search_block_size', '250', 0),
('search_gc', '7200', 0),
('search_indexing_state', '', 1),
('search_interval', '0', 0),
('search_last_gc', '1536194939', 1),
('search_store_results', '1800', 0),
('search_type', '\\phpbb\\search\\fulltext_native', 0),
('secure_allow_deny', '1', 0),
('secure_allow_empty_referer', '1', 0),
('secure_downloads', '1', 0),
('senky_ura_only_mods', '', 0),
('server_name', 'localhost', 0),
('server_port', '80', 0),
('server_protocol', 'http://', 0),
('session_gc', '3600', 0),
('session_last_gc', '1536194667', 1),
('session_length', '3600', 0),
('show_user_recent_post', '1', 0),
('site_desc', 'Striving for a replica RSC game and more', 0),
('site_home_text', '', 0),
('site_home_url', '', 0),
('sitename', 'Open RSC', 0),
('smilies_path', 'images/smilies', 0),
('smilies_per_page', '50', 0),
('smtp_allow_self_signed', '0', 0),
('smtp_auth_method', 'LOGIN', 0),
('smtp_delivery', '1', 0),
('smtp_host', 'smtp.gmail.com', 0),
('smtp_password', 'YOUR_GMAIL_GENERATED_APP_PASSWORD', 0),
('smtp_port', '587', 0),
('smtp_username', 'openrsc.emailer@gmail.com', 0),
('smtp_verify_peer', '0', 0),
('smtp_verify_peer_name', '0', 0),
('teampage_forums', '1', 0),
('teampage_memberships', '1', 0),
('topics_per_page', '25', 0),
('tpl_allow_php', '0', 0),
('upload_dir_size', '0', 1),
('upload_icons_path', 'images/upload_icons', 0),
('upload_path', 'files', 0),
('use_system_cron', '0', 0),
('version', '3.2.2', 0),
('warnings_expire_days', '90', 0),
('warnings_gc', '14400', 0),
('warnings_last_gc', '1536194650', 1);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_config_text`
--

DROP TABLE IF EXISTS `phpbb_config_text`;
CREATE TABLE IF NOT EXISTS `phpbb_config_text` (
  `config_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `config_value` mediumtext COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`config_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_config_text`
--

INSERT INTO `phpbb_config_text` (`config_name`, `config_value`) VALUES
('contact_admin_info', ''),
('contact_admin_info_bitfield', ''),
('contact_admin_info_flags', '7'),
('contact_admin_info_uid', ''),
('media_embed_sites', '[\"abcnews\",\"amazon\",\"audioboom\",\"audiomack\",\"bandcamp\",\"bbcnews\",\"blab\",\"bleacherreport\",\"break\",\"brightcove\",\"cbsnews\",\"cnbc\",\"cnn\",\"cnnmoney\",\"collegehumor\",\"comedycentral\",\"coub\",\"dailymotion\",\"democracynow\",\"dumpert\",\"eighttracks\",\"espn\",\"facebook\",\"flickr\",\"foratv\",\"foxnews\",\"funnyordie\",\"gamespot\",\"gametrailers\",\"getty\",\"gfycat\",\"gifs\",\"gist\",\"globalnews\",\"gofundme\",\"googledrive\",\"googleplus\",\"googlesheets\",\"healthguru\",\"hudl\",\"hulu\",\"humortvnl\",\"ign\",\"imdb\",\"imgur\",\"indiegogo\",\"instagram\",\"internetarchive\",\"izlesene\",\"jwplatform\",\"khl\",\"kickstarter\",\"kissvideo\",\"libsyn\",\"livecap\",\"liveleak\",\"livestream\",\"mailru\",\"medium\",\"metacafe\",\"mixcloud\",\"mlb\",\"mrctv\",\"msnbc\",\"natgeochannel\",\"natgeovideo\",\"nbcnews\",\"nbcsports\",\"nhl\",\"npr\",\"nytimes\",\"oddshot\",\"orfium\",\"pastebin\",\"pinterest\",\"playstv\",\"podbean\",\"prezi\",\"reddit\",\"rutube\",\"scribd\",\"slideshare\",\"soundcloud\",\"sportsnet\",\"spotify\",\"steamstore\",\"stitcher\",\"strawpoll\",\"streamable\",\"teamcoco\",\"ted\",\"theatlantic\",\"theguardian\",\"theonion\",\"tinypic\",\"tmz\",\"traileraddict\",\"tumblr\",\"twitch\",\"twitter\",\"ustream\",\"vbox7\",\"veoh\",\"vevo\",\"viagame\",\"videodetective\",\"videomega\",\"vidme\",\"vimeo\",\"vine\",\"vk\",\"vocaroo\",\"vox\",\"washingtonpost\",\"wshh\",\"wsj\",\"xboxclips\",\"xboxdvr\",\"yahooscreen\",\"youku\",\"youtube\",\"zippyshare\"]');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_confirm`
--

DROP TABLE IF EXISTS `phpbb_confirm`;
CREATE TABLE IF NOT EXISTS `phpbb_confirm` (
  `confirm_id` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `session_id` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `confirm_type` tinyint(3) NOT NULL DEFAULT 0,
  `code` varchar(8) COLLATE utf8_bin NOT NULL DEFAULT '',
  `seed` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `attempts` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`session_id`,`confirm_id`),
  KEY `confirm_type` (`confirm_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_confirm`
--

INSERT INTO `phpbb_confirm` (`confirm_id`, `session_id`, `confirm_type`, `code`, `seed`, `attempts`) VALUES
('256e2b4d6e5c45c9ed4b65d1f287aa6f', 'e3bb54f5bae8f37cc70f6ce30628b90a', 1, '27IYQE', 276654176, 0),
('67e947a09bb1de77ffd0f22329e3c37d', 'e3bb54f5bae8f37cc70f6ce30628b90a', 1, '3K5FYV', 554515866, 0),
('d9777a61df38908f60469f357c8015e2', 'e3bb54f5bae8f37cc70f6ce30628b90a', 1, '63I7YUH', 1276002970, 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_disallow`
--

DROP TABLE IF EXISTS `phpbb_disallow`;
CREATE TABLE IF NOT EXISTS `phpbb_disallow` (
  `disallow_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `disallow_username` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`disallow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_drafts`
--

DROP TABLE IF EXISTS `phpbb_drafts`;
CREATE TABLE IF NOT EXISTS `phpbb_drafts` (
  `draft_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `save_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `draft_subject` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `draft_message` mediumtext COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`draft_id`),
  KEY `save_time` (`save_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_ext`
--

DROP TABLE IF EXISTS `phpbb_ext`;
CREATE TABLE IF NOT EXISTS `phpbb_ext` (
  `ext_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `ext_active` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `ext_state` text COLLATE utf8_bin NOT NULL,
  UNIQUE KEY `ext_name` (`ext_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_ext`
--

INSERT INTO `phpbb_ext` (`ext_name`, `ext_active`, `ext_state`) VALUES
('flerex/linkedaccounts', 1, 'b:0;'),
('phpbb/mediaembed', 1, 'b:0;'),
('phpbbmodders/banhammer', 1, 'b:0;'),
('rmcgirr83/activity24hours', 1, 'b:0;'),
('senky/userrecentactivity', 1, 'b:0;');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_extensions`
--

DROP TABLE IF EXISTS `phpbb_extensions`;
CREATE TABLE IF NOT EXISTS `phpbb_extensions` (
  `extension_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `extension` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`extension_id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_extensions`
--

INSERT INTO `phpbb_extensions` (`extension_id`, `group_id`, `extension`) VALUES
(1, 1, 'gif'),
(2, 1, 'png'),
(3, 1, 'jpeg'),
(4, 1, 'jpg'),
(5, 1, 'tif'),
(6, 1, 'tiff'),
(7, 1, 'tga'),
(8, 2, 'gtar'),
(9, 2, 'gz'),
(10, 2, 'tar'),
(11, 2, 'zip'),
(12, 2, 'rar'),
(13, 2, 'ace'),
(14, 2, 'torrent'),
(15, 2, 'tgz'),
(16, 2, 'bz2'),
(17, 2, '7z'),
(18, 3, 'txt'),
(19, 3, 'c'),
(20, 3, 'h'),
(21, 3, 'cpp'),
(22, 3, 'hpp'),
(23, 3, 'diz'),
(24, 3, 'csv'),
(25, 3, 'ini'),
(26, 3, 'log'),
(27, 3, 'js'),
(28, 3, 'xml'),
(29, 4, 'xls'),
(30, 4, 'xlsx'),
(31, 4, 'xlsm'),
(32, 4, 'xlsb'),
(33, 4, 'doc'),
(34, 4, 'docx'),
(35, 4, 'docm'),
(36, 4, 'dot'),
(37, 4, 'dotx'),
(38, 4, 'dotm'),
(39, 4, 'pdf'),
(40, 4, 'ai'),
(41, 4, 'ps'),
(42, 4, 'ppt'),
(43, 4, 'pptx'),
(44, 4, 'pptm'),
(45, 4, 'odg'),
(46, 4, 'odp'),
(47, 4, 'ods'),
(48, 4, 'odt'),
(49, 4, 'rtf'),
(50, 5, 'swf'),
(51, 6, 'mp3'),
(52, 6, 'mpeg'),
(53, 6, 'mpg'),
(54, 6, 'ogg'),
(55, 6, 'ogm');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_extension_groups`
--

DROP TABLE IF EXISTS `phpbb_extension_groups`;
CREATE TABLE IF NOT EXISTS `phpbb_extension_groups` (
  `group_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `cat_id` tinyint(2) NOT NULL DEFAULT 0,
  `allow_group` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `download_mode` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `upload_icon` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `max_filesize` int(20) UNSIGNED NOT NULL DEFAULT 0,
  `allowed_forums` text COLLATE utf8_bin NOT NULL,
  `allow_in_pm` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_extension_groups`
--

INSERT INTO `phpbb_extension_groups` (`group_id`, `group_name`, `cat_id`, `allow_group`, `download_mode`, `upload_icon`, `max_filesize`, `allowed_forums`, `allow_in_pm`) VALUES
(1, 'IMAGES', 1, 1, 1, '', 0, '', 0),
(2, 'ARCHIVES', 0, 0, 1, '', 0, '', 0),
(3, 'PLAIN_TEXT', 0, 0, 1, '', 0, '', 0),
(4, 'DOCUMENTS', 0, 0, 1, '', 0, '', 0),
(5, 'FLASH_FILES', 5, 0, 1, '', 0, '', 0),
(6, 'DOWNLOADABLE_FILES', 0, 0, 1, '', 0, '', 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_flerex_linkedaccounts`
--

DROP TABLE IF EXISTS `phpbb_flerex_linkedaccounts`;
CREATE TABLE IF NOT EXISTS `phpbb_flerex_linkedaccounts` (
  `user_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `linked_user_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `created_at` int(11) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`,`linked_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_forums`
--

DROP TABLE IF EXISTS `phpbb_forums`;
CREATE TABLE IF NOT EXISTS `phpbb_forums` (
  `forum_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `parent_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `left_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `right_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `forum_parents` mediumtext COLLATE utf8_bin NOT NULL,
  `forum_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_desc` text COLLATE utf8_bin NOT NULL,
  `forum_desc_bitfield` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_desc_options` int(11) UNSIGNED NOT NULL DEFAULT 7,
  `forum_desc_uid` varchar(8) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_link` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_password` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_style` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `forum_image` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_rules` text COLLATE utf8_bin NOT NULL,
  `forum_rules_link` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_rules_bitfield` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_rules_options` int(11) UNSIGNED NOT NULL DEFAULT 7,
  `forum_rules_uid` varchar(8) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_topics_per_page` tinyint(4) NOT NULL DEFAULT 0,
  `forum_type` tinyint(4) NOT NULL DEFAULT 0,
  `forum_status` tinyint(4) NOT NULL DEFAULT 0,
  `forum_last_post_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `forum_last_poster_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `forum_last_post_subject` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_last_post_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `forum_last_poster_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_last_poster_colour` varchar(6) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forum_flags` tinyint(4) NOT NULL DEFAULT 32,
  `display_on_index` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `enable_indexing` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `enable_icons` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `enable_prune` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `prune_next` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `prune_days` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `prune_viewed` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `prune_freq` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `display_subforum_list` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `forum_options` int(20) UNSIGNED NOT NULL DEFAULT 0,
  `forum_posts_approved` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `forum_posts_unapproved` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `forum_posts_softdeleted` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `forum_topics_approved` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `forum_topics_unapproved` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `forum_topics_softdeleted` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `enable_shadow_prune` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `prune_shadow_days` mediumint(8) UNSIGNED NOT NULL DEFAULT 7,
  `prune_shadow_freq` mediumint(8) UNSIGNED NOT NULL DEFAULT 1,
  `prune_shadow_next` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`forum_id`),
  KEY `left_right_id` (`left_id`,`right_id`),
  KEY `forum_lastpost_id` (`forum_last_post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_forums`
--

INSERT INTO `phpbb_forums` (`forum_id`, `parent_id`, `left_id`, `right_id`, `forum_parents`, `forum_name`, `forum_desc`, `forum_desc_bitfield`, `forum_desc_options`, `forum_desc_uid`, `forum_link`, `forum_password`, `forum_style`, `forum_image`, `forum_rules`, `forum_rules_link`, `forum_rules_bitfield`, `forum_rules_options`, `forum_rules_uid`, `forum_topics_per_page`, `forum_type`, `forum_status`, `forum_last_post_id`, `forum_last_poster_id`, `forum_last_post_subject`, `forum_last_post_time`, `forum_last_poster_name`, `forum_last_poster_colour`, `forum_flags`, `display_on_index`, `enable_indexing`, `enable_icons`, `enable_prune`, `prune_next`, `prune_days`, `prune_viewed`, `prune_freq`, `display_subforum_list`, `forum_options`, `forum_posts_approved`, `forum_posts_unapproved`, `forum_posts_softdeleted`, `forum_topics_approved`, `forum_topics_unapproved`, `forum_topics_softdeleted`, `enable_shadow_prune`, `prune_shadow_days`, `prune_shadow_freq`, `prune_shadow_next`) VALUES
(4, 10, 2, 3, 'a:1:{i:10;a:2:{i:0;s:17:\"Global Discussion\";i:1;i:0;}}', 'The Pub', '<t>Discuss anything you would like in here.</t>', '', 7, '', '', '', 0, '', '', '', '', 7, '', 0, 1, 0, 0, 0, '', 0, '', '', 112, 0, 1, 0, 0, 0, 7, 7, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 7, 1, 0),
(8, 13, 6, 7, '', 'Join us in Discord', '<t>Click the link for the invite.</t>', '', 7, '', 'https://discord.gg/94vVKND', '', 0, '', '', '', '', 7, '', 0, 2, 0, 0, 0, '', 0, '', '', 97, 1, 1, 0, 0, 0, 7, 7, 1, 1, 0, 3, 0, 0, 0, 0, 0, 0, 7, 1, 0),
(9, 13, 8, 9, '', 'GitHub Project', '<t>Fork the project, write code, and send pull requests to assist in development!</t>', '', 7, '', 'https://github.com/Marwolf/Open-RSC', '', 0, '', '', '', '', 7, '', 0, 2, 0, 0, 0, '', 0, '', '', 97, 0, 1, 0, 0, 0, 7, 7, 1, 1, 0, 4, 0, 0, 0, 0, 0, 0, 7, 1, 0),
(10, 0, 1, 4, '', 'Global Discussion', '', '', 7, '', '', '', 0, '', '', '', '', 7, '', 0, 0, 0, 0, 0, '', 0, '', '', 32, 0, 1, 0, 0, 0, 7, 7, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 7, 1, 0),
(13, 0, 5, 12, '', 'Links', '', '', 7, '', '', '', 0, '', '', '', '', 7, '', 0, 0, 0, 0, 0, '', 0, '', '', 32, 0, 1, 0, 0, 0, 7, 7, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 7, 1, 0),
(17, 13, 10, 11, '', 'Bug Tracker', '<t>Issues to fix are kept here.</t>', '', 7, '', 'https://github.com/Marwolf/Open-RSC/issues', '', 0, '', '', '', '', 7, '', 0, 2, 0, 0, 0, '', 0, '', '', 33, 0, 1, 0, 0, 0, 7, 7, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 7, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_forums_access`
--

DROP TABLE IF EXISTS `phpbb_forums_access`;
CREATE TABLE IF NOT EXISTS `phpbb_forums_access` (
  `forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `session_id` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`forum_id`,`user_id`,`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_forums_track`
--

DROP TABLE IF EXISTS `phpbb_forums_track`;
CREATE TABLE IF NOT EXISTS `phpbb_forums_track` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `mark_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`,`forum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_forums_track`
--

INSERT INTO `phpbb_forums_track` (`user_id`, `forum_id`, `mark_time`) VALUES
(2, 4, 1529715588);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_forums_watch`
--

DROP TABLE IF EXISTS `phpbb_forums_watch`;
CREATE TABLE IF NOT EXISTS `phpbb_forums_watch` (
  `forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `notify_status` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  KEY `forum_id` (`forum_id`),
  KEY `user_id` (`user_id`),
  KEY `notify_stat` (`notify_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_groups`
--

DROP TABLE IF EXISTS `phpbb_groups`;
CREATE TABLE IF NOT EXISTS `phpbb_groups` (
  `group_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_type` tinyint(4) NOT NULL DEFAULT 1,
  `group_founder_manage` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `group_skip_auth` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `group_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `group_desc` text COLLATE utf8_bin NOT NULL,
  `group_desc_bitfield` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `group_desc_options` int(11) UNSIGNED NOT NULL DEFAULT 7,
  `group_desc_uid` varchar(8) COLLATE utf8_bin NOT NULL DEFAULT '',
  `group_display` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `group_avatar` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `group_avatar_type` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `group_avatar_width` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `group_avatar_height` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `group_rank` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `group_colour` varchar(6) COLLATE utf8_bin NOT NULL DEFAULT '',
  `group_sig_chars` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `group_receive_pm` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `group_message_limit` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `group_legend` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `group_max_recipients` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`group_id`),
  KEY `group_legend_name` (`group_legend`,`group_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_groups`
--

INSERT INTO `phpbb_groups` (`group_id`, `group_type`, `group_founder_manage`, `group_skip_auth`, `group_name`, `group_desc`, `group_desc_bitfield`, `group_desc_options`, `group_desc_uid`, `group_display`, `group_avatar`, `group_avatar_type`, `group_avatar_width`, `group_avatar_height`, `group_rank`, `group_colour`, `group_sig_chars`, `group_receive_pm`, `group_message_limit`, `group_legend`, `group_max_recipients`) VALUES
(1, 3, 0, 0, 'GUESTS', '', '', 7, '', 0, '', '', 0, 0, 0, '', 0, 0, 0, 0, 5),
(2, 3, 0, 0, 'REGISTERED', '', '', 7, '', 0, '', '', 0, 0, 0, '', 0, 0, 0, 0, 5),
(3, 3, 0, 0, 'REGISTERED_COPPA', '', '', 7, '', 0, '', '', 0, 0, 0, '', 0, 0, 0, 0, 5),
(4, 3, 0, 0, 'GLOBAL_MODERATORS', '', '', 7, '', 0, '', '', 0, 0, 0, '00AA00', 0, 0, 0, 2, 0),
(5, 3, 1, 0, 'ADMINISTRATORS', '', '', 7, '', 0, '', '', 0, 0, 0, 'AA0000', 0, 0, 0, 1, 0),
(6, 3, 0, 0, 'BOTS', '', '', 7, '', 0, '', '', 0, 0, 0, '9E8DA7', 0, 0, 0, 0, 5),
(7, 3, 0, 0, 'NEWLY_REGISTERED', '', '', 7, '', 0, '', '', 0, 0, 0, '', 0, 0, 0, 0, 5);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_icons`
--

DROP TABLE IF EXISTS `phpbb_icons`;
CREATE TABLE IF NOT EXISTS `phpbb_icons` (
  `icons_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `icons_url` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `icons_width` tinyint(4) NOT NULL DEFAULT 0,
  `icons_height` tinyint(4) NOT NULL DEFAULT 0,
  `icons_alt` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `icons_order` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `display_on_posting` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (`icons_id`),
  KEY `display_on_posting` (`display_on_posting`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_icons`
--

INSERT INTO `phpbb_icons` (`icons_id`, `icons_url`, `icons_width`, `icons_height`, `icons_alt`, `icons_order`, `display_on_posting`) VALUES
(1, 'misc/fire.gif', 16, 16, '', 1, 1),
(2, 'smile/redface.gif', 16, 16, '', 9, 1),
(3, 'smile/mrgreen.gif', 16, 16, '', 10, 1),
(4, 'misc/heart.gif', 16, 16, '', 4, 1),
(5, 'misc/star.gif', 16, 16, '', 2, 1),
(6, 'misc/radioactive.gif', 16, 16, '', 3, 1),
(7, 'misc/thinking.gif', 16, 16, '', 5, 1),
(8, 'smile/info.gif', 16, 16, '', 8, 1),
(9, 'smile/question.gif', 16, 16, '', 6, 1),
(10, 'smile/alert.gif', 16, 16, '', 7, 1);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_lang`
--

DROP TABLE IF EXISTS `phpbb_lang`;
CREATE TABLE IF NOT EXISTS `phpbb_lang` (
  `lang_id` tinyint(4) NOT NULL AUTO_INCREMENT,
  `lang_iso` varchar(30) COLLATE utf8_bin NOT NULL DEFAULT '',
  `lang_dir` varchar(30) COLLATE utf8_bin NOT NULL DEFAULT '',
  `lang_english_name` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '',
  `lang_local_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `lang_author` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`lang_id`),
  KEY `lang_iso` (`lang_iso`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_lang`
--

INSERT INTO `phpbb_lang` (`lang_id`, `lang_iso`, `lang_dir`, `lang_english_name`, `lang_local_name`, `lang_author`) VALUES
(1, 'en', 'en', 'British English', 'British English', 'phpBB Limited');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_log`
--

DROP TABLE IF EXISTS `phpbb_log`;
CREATE TABLE IF NOT EXISTS `phpbb_log` (
  `log_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `log_type` tinyint(4) NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `post_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `reportee_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `log_ip` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `log_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `log_operation` text COLLATE utf8_bin NOT NULL,
  `log_data` mediumtext COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`log_id`),
  KEY `log_type` (`log_type`),
  KEY `forum_id` (`forum_id`),
  KEY `topic_id` (`topic_id`),
  KEY `reportee_id` (`reportee_id`),
  KEY `user_id` (`user_id`),
  KEY `log_time` (`log_time`)
) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_log`
--

INSERT INTO `phpbb_log` (`log_id`, `log_type`, `user_id`, `forum_id`, `topic_id`, `post_id`, `reportee_id`, `log_ip`, `log_time`, `log_operation`, `log_data`) VALUES
(1, 2, 1, 0, 0, 0, 0, '108.162.237.186', 1529675817, 'LOG_ERROR_EMAIL', 'a:1:{i:0;s:82:\"<strong>EMAIL/PHP/mail()</strong><br /><em>/install/app.php</em><br /><br /><br />\";}'),
(2, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529675817, 'LOG_INSTALL_INSTALLED', 'a:1:{i:0;s:5:\"3.2.2\";}'),
(3, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529675863, 'LOG_CONFIG_SERVER', ''),
(4, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529675884, 'LOG_CONFIG_SECURITY', ''),
(5, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529676479, 'LOG_DOWNLOAD_IP', 'a:1:{i:0;s:11:\"openrsc.com\";}'),
(6, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529676519, 'LOG_CONFIG_SETTINGS', ''),
(7, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529676584, 'LOG_CONFIG_AVATAR', ''),
(8, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529676635, 'LOG_CONFIG_SIGNATURE', ''),
(9, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529676653, 'LOG_CONFIG_FEED', ''),
(10, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529676682, 'LOG_CONFIG_REGISTRATION', ''),
(11, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529676736, 'LOG_CONFIG_VISUAL', ''),
(12, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529676748, 'LOG_CONFIG_VISUAL', ''),
(13, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529676766, 'LOG_CONFIG_ATTACH', ''),
(14, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529676998, 'LOG_STYLE_ADD', 'a:1:{i:0;s:25:\"prosilver Special Edition\";}'),
(15, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677180, 'LOG_STYLE_ADD', 'a:1:{i:0;s:6:\"PBWoW3\";}'),
(16, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677248, 'LOG_CONFIG_POST', ''),
(17, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677293, 'LOG_CONFIG_POST', ''),
(18, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677325, 'LOG_ATTACH_EXTGROUP_EDIT', 'a:1:{i:0;s:8:\"Archives\";}'),
(19, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677496, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(20, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677520, 'LOG_CONFIG_COOKIE', ''),
(21, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677585, 'LOG_PROFILE_FIELD_REMOVED', 'a:1:{i:0;s:9:\"phpbb_aol\";}'),
(22, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677589, 'LOG_PROFILE_FIELD_REMOVED', 'a:1:{i:0;s:9:\"phpbb_icq\";}'),
(23, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677592, 'LOG_PROFILE_FIELD_REMOVED', 'a:1:{i:0;s:11:\"phpbb_yahoo\";}'),
(24, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677598, 'LOG_PROFILE_FIELD_REMOVED', 'a:1:{i:0;s:16:\"phpbb_googleplus\";}'),
(25, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677603, 'LOG_PROFILE_FIELD_REMOVED', 'a:1:{i:0;s:16:\"phpbb_occupation\";}'),
(26, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677608, 'LOG_PROFILE_FIELD_REMOVED', 'a:1:{i:0;s:15:\"phpbb_interests\";}'),
(27, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677616, 'LOG_PROFILE_FIELD_REMOVED', 'a:1:{i:0;s:14:\"phpbb_facebook\";}'),
(28, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677619, 'LOG_PROFILE_FIELD_REMOVED', 'a:1:{i:0;s:13:\"phpbb_twitter\";}'),
(29, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677622, 'LOG_PROFILE_FIELD_REMOVED', 'a:1:{i:0;s:13:\"phpbb_youtube\";}'),
(30, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677717, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(31, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677739, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:11:\"Information\";}'),
(32, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677766, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:4:\"News\";}'),
(33, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677828, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:4:\"News\";i:1;s:42:\"<span class=\"sep\">Global moderators</span>\";}'),
(34, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677870, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:4:\"News\";i:1;s:139:\"<span class=\"sep\">Registered users</span>, <span class=\"sep\">Registered COPPA users</span>, <span class=\"sep\">Newly registered users</span>\";}'),
(35, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677907, 'LOG_FORUM_ADD', 'a:1:{i:0;s:7:\"General\";}'),
(36, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677932, 'LOG_FORUM_ADD', 'a:1:{i:0;s:7:\"The Pub\";}'),
(37, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677932, 'LOG_FORUM_COPIED_PERMISSIONS', 'a:2:{i:0;s:7:\"General\";i:1;s:7:\"The Pub\";}'),
(38, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529677979, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:7:\"General\";}'),
(39, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678045, 'LOG_FORUM_ADD', 'a:1:{i:0;s:8:\"Requests\";}'),
(40, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678045, 'LOG_FORUM_COPIED_PERMISSIONS', 'a:2:{i:0;s:7:\"General\";i:1;s:8:\"Requests\";}'),
(41, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678105, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:7:\"General\";}'),
(42, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678141, 'LOG_ACL_ADD_GROUP_LOCAL_F_', 'a:2:{i:0;s:45:\"Information, News, General, The Pub, Requests\";i:1;s:39:\"<span class=\"sep\">Administrators</span>\";}'),
(43, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678171, 'LOG_ACL_ADD_GROUP_LOCAL_F_', 'a:2:{i:0;s:45:\"Information, News, General, The Pub, Requests\";i:1;s:29:\"<span class=\"sep\">Bots</span>\";}'),
(44, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678225, 'LOG_ACL_ADD_GROUP_LOCAL_F_', 'a:2:{i:0;s:45:\"Information, News, General, The Pub, Requests\";i:1;s:42:\"<span class=\"sep\">Global moderators</span>\";}'),
(45, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678242, 'LOG_ACL_ADD_GROUP_LOCAL_F_', 'a:2:{i:0;s:45:\"Information, News, General, The Pub, Requests\";i:1;s:31:\"<span class=\"sep\">Guests</span>\";}'),
(46, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678276, 'LOG_ACL_ADD_GROUP_LOCAL_F_', 'a:2:{i:0;s:45:\"Information, News, General, The Pub, Requests\";i:1;s:47:\"<span class=\"sep\">Newly registered users</span>\";}'),
(47, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678329, 'LOG_ACL_ADD_GROUP_LOCAL_F_', 'a:2:{i:0;s:45:\"Information, News, General, The Pub, Requests\";i:1;s:41:\"<span class=\"sep\">Registered users</span>\";}'),
(48, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678376, 'LOG_FORUM_ADD', 'a:1:{i:0;s:11:\"Development\";}'),
(49, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678489, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:11:\"Development\";i:1;s:196:\"<span class=\"sep\">Guests</span>, <span class=\"sep\">Global moderators</span>, <span class=\"sep\">Administrators</span>, <span class=\"sep\">Bots</span>, <span class=\"sep\">Newly registered users</span>\";}'),
(50, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678489, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:11:\"Development\";i:1;s:41:\"<span class=\"sep\">Registered users</span>\";}'),
(51, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678502, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:11:\"Development\";i:1;s:47:\"<span class=\"sep\">Newly registered users</span>\";}'),
(52, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678523, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:45:\"Information, News, General, The Pub, Requests\";i:1;s:47:\"<span class=\"sep\">Newly registered users</span>\";}'),
(53, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678523, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:11:\"Development\";i:1;s:47:\"<span class=\"sep\">Newly registered users</span>\";}'),
(54, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678575, 'LOG_FORUM_ADD', 'a:1:{i:0;s:22:\"Development Discussion\";}'),
(55, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678575, 'LOG_FORUM_COPIED_PERMISSIONS', 'a:2:{i:0;s:11:\"Development\";i:1;s:22:\"Development Discussion\";}'),
(56, 2, 1, 0, 0, 0, 0, '108.162.237.186', 1529678785, 'LOG_ERROR_EMAIL', 'a:1:{i:0;s:74:\"<strong>EMAIL/PHP/mail()</strong><br /><em>/ucp.php</em><br /><br /><br />\";}'),
(57, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678836, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(58, 2, 2, 0, 0, 0, 0, '108.162.237.186', 1529678858, 'LOG_ERROR_EMAIL', 'a:1:{i:0;s:80:\"<strong>EMAIL/PHP/mail()</strong><br /><em>/adm/index.php</em><br /><br /><br />\";}'),
(59, 0, 2, 0, 0, 0, 0, '108.162.237.186', 1529678864, 'LOG_CONFIG_EMAIL', ''),
(60, 2, 1, 0, 0, 0, 0, '162.158.122.134', 1529678870, 'LOG_ERROR_EMAIL', 'a:1:{i:0;s:74:\"<strong>EMAIL/PHP/mail()</strong><br /><em>/ucp.php</em><br /><br /><br />\";}'),
(61, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529679053, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(62, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529679078, 'LOG_USER_ACTIVE', 'a:1:{i:0;s:5:\"Kenix\";}'),
(64, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529679083, 'LOG_USER_USER_UPDATE', 'a:1:{i:0;s:5:\"Kenix\";}'),
(65, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529679125, 'LOG_GROUP_DEFAULTS', 'a:2:{i:0;s:14:\"Administrators\";i:1;s:5:\"Kenix\";}'),
(66, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529679125, 'LOG_MODS_ADDED', 'a:2:{i:0;s:14:\"Administrators\";i:1;s:5:\"Kenix\";}'),
(67, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529679941, 'LOG_CONFIG_EMAIL', ''),
(68, 2, 1, 0, 0, 0, 0, '172.68.78.6', 1529679966, 'LOG_ERROR_EMAIL', 'a:1:{i:0;s:74:\"<strong>EMAIL/PHP/mail()</strong><br /><em>/ucp.php</em><br /><br /><br />\";}'),
(69, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529680120, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(70, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529680139, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:4:\"News\";}'),
(71, 2, 2, 0, 0, 0, 0, '172.68.78.6', 1529680147, 'LOG_ERROR_EMAIL', 'a:1:{i:0;s:80:\"<strong>EMAIL/PHP/mail()</strong><br /><em>/adm/index.php</em><br /><br /><br />\";}'),
(72, 2, 2, 0, 0, 0, 0, '172.68.78.6', 1529680273, 'LOG_ERROR_EMAIL', 'a:1:{i:0;s:80:\"<strong>EMAIL/PHP/mail()</strong><br /><em>/adm/index.php</em><br /><br /><br />\";}'),
(73, 0, 1, 0, 0, 0, 0, '162.158.122.134', 1529680351, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(74, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529680421, 'LOG_CONFIG_EMAIL', ''),
(75, 2, 2, 0, 0, 0, 0, '172.68.78.6', 1529680436, 'LOG_ERROR_EMAIL', 'a:1:{i:0;s:172:\"<strong>EMAIL/SMTP</strong><br /><em>/adm/index.php</em><br /><br />Could not get mail server response codes.<h1>Backtrace</h1><p>Connecting to smtp.gmail.com:465</p><br />\";}'),
(76, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529680532, 'LOG_CONFIG_EMAIL', ''),
(78, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529680600, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(79, 0, 2, 0, 0, 0, 0, '172.68.78.6', 1529680617, 'LOG_USER_DELETED', 'a:1:{i:0;s:4:\"wolf\";}'),
(81, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710660, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(82, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710673, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:4:\"News\";}'),
(83, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710686, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:7:\"The Pub\";}'),
(84, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710693, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:8:\"Requests\";}'),
(85, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710711, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:22:\"Development Discussion\";}'),
(86, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710724, 'LOG_FORUM_DEL_FORUM', 'a:1:{i:0;s:11:\"Information\";}'),
(87, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710728, 'LOG_FORUM_DEL_FORUM', 'a:1:{i:0;s:7:\"General\";}'),
(88, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710733, 'LOG_FORUM_DEL_FORUM', 'a:1:{i:0;s:11:\"Development\";}'),
(89, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710785, 'LOG_FORUM_ADD', 'a:1:{i:0;s:18:\"Join us in Discord\";}'),
(90, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710818, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:18:\"Join us in Discord\";}'),
(91, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710830, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:18:\"Join us in Discord\";}'),
(92, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710830, 'LOG_FORUM_COPIED_PERMISSIONS', 'a:2:{i:0;s:4:\"News\";i:1;s:18:\"Join us in Discord\";}'),
(93, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529710843, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:18:\"Join us in Discord\";}'),
(94, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529711155, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:4:\"News\";i:1;s:196:\"<span class=\"sep\">Guests</span>, <span class=\"sep\">Global moderators</span>, <span class=\"sep\">Administrators</span>, <span class=\"sep\">Bots</span>, <span class=\"sep\">Newly registered users</span>\";}'),
(95, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529711155, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:13:\"News, The Pub\";i:1;s:206:\"<span class=\"sep\">Registered users</span>, <span class=\"sep\">Registered COPPA users</span>, <span class=\"sep\">Global moderators</span>, <span class=\"sep\">Administrators</span>, <span class=\"sep\">Bots</span>\";}'),
(96, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529711155, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:17:\"The Pub, Requests\";i:1;s:213:\"<span class=\"sep\">Guests</span>, <span class=\"sep\">Registered users</span>, <span class=\"sep\">Registered COPPA users</span>, <span class=\"sep\">Administrators</span>, <span class=\"sep\">Newly registered users</span>\";}'),
(97, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529711155, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:8:\"Requests\";i:1;s:198:\"<span class=\"sep\">Guests</span>, <span class=\"sep\">Registered users</span>, <span class=\"sep\">Global moderators</span>, <span class=\"sep\">Bots</span>, <span class=\"sep\">Newly registered users</span>\";}'),
(98, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529711155, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:32:\"Requests, Development Discussion\";i:1;s:196:\"<span class=\"sep\">Guests</span>, <span class=\"sep\">Registered COPPA users</span>, <span class=\"sep\">Global moderators</span>, <span class=\"sep\">Administrators</span>, <span class=\"sep\">Bots</span>\";}'),
(99, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529711156, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:42:\"Development Discussion, Join us in Discord\";i:1;s:211:\"<span class=\"sep\">Registered users</span>, <span class=\"sep\">Registered COPPA users</span>, <span class=\"sep\">Administrators</span>, <span class=\"sep\">Bots</span>, <span class=\"sep\">Newly registered users</span>\";}'),
(100, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529711156, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:18:\"Join us in Discord\";i:1;s:216:\"<span class=\"sep\">Guests</span>, <span class=\"sep\">Registered users</span>, <span class=\"sep\">Registered COPPA users</span>, <span class=\"sep\">Global moderators</span>, <span class=\"sep\">Newly registered users</span>\";}'),
(101, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529713872, 'LOG_FORUM_ADD', 'a:1:{i:0;s:23:\"Open RSC GitHub Project\";}'),
(102, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529713900, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:23:\"Open RSC GitHub Project\";i:1;s:196:\"<span class=\"sep\">Guests</span>, <span class=\"sep\">Global moderators</span>, <span class=\"sep\">Administrators</span>, <span class=\"sep\">Bots</span>, <span class=\"sep\">Newly registered users</span>\";}'),
(103, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529713901, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:23:\"Open RSC GitHub Project\";i:1;s:41:\"<span class=\"sep\">Registered users</span>\";}'),
(104, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529714098, 'LOG_CONFIG_POST', ''),
(105, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529714129, 'LOG_BBCODE_ADD', 'a:1:{i:0;s:1:\"s\";}'),
(106, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529715199, 'LOG_FORUM_ADD', 'a:1:{i:0;s:6:\"Global\";}'),
(107, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529715248, 'LOG_FORUM_ADD', 'a:1:{i:0;s:14:\"Distinct Games\";}'),
(108, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529715269, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:4:\"News\";}'),
(109, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529715289, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:7:\"The Pub\";}'),
(110, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529715373, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:6:\"Global\";i:1;s:196:\"<span class=\"sep\">Guests</span>, <span class=\"sep\">Global moderators</span>, <span class=\"sep\">Administrators</span>, <span class=\"sep\">Bots</span>, <span class=\"sep\">Newly registered users</span>\";}'),
(111, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529715373, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:22:\"Global, Distinct Games\";i:1;s:190:\"<span class=\"sep\">Guests</span>, <span class=\"sep\">Registered users</span>, <span class=\"sep\">Global moderators</span>, <span class=\"sep\">Administrators</span>, <span class=\"sep\">Bots</span>\";}'),
(112, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529715373, 'LOG_ACL_ADD_FORUM_LOCAL_F_', 'a:2:{i:0;s:14:\"Distinct Games\";i:1;s:90:\"<span class=\"sep\">Registered users</span>, <span class=\"sep\">Newly registered users</span>\";}'),
(113, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529716204, 'LOG_FORUM_ADD', 'a:1:{i:0;s:5:\"Media\";}'),
(114, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529716204, 'LOG_FORUM_COPIED_PERMISSIONS', 'a:2:{i:0;s:7:\"The Pub\";i:1;s:5:\"Media\";}'),
(115, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723235, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:8:\"Requests\";}'),
(116, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723257, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:22:\"Development Discussion\";}'),
(117, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723289, 'LOG_FORUM_ADD', 'a:1:{i:0;s:5:\"Links\";}'),
(118, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723289, 'LOG_FORUM_COPIED_PERMISSIONS', 'a:2:{i:0;s:6:\"Global\";i:1;s:5:\"Links\";}'),
(119, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723298, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:18:\"Join us in Discord\";}'),
(120, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723305, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:23:\"Open RSC GitHub Project\";}'),
(121, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723312, 'LOG_FORUM_MOVE_DOWN', 'a:2:{i:0;s:14:\"Distinct Games\";i:1;s:5:\"Links\";}'),
(122, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723328, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:14:\"Distinct Games\";}'),
(123, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723328, 'LOG_FORUM_COPIED_PERMISSIONS', 'a:2:{i:0;s:6:\"Global\";i:1;s:14:\"Distinct Games\";}'),
(124, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723455, 'LOG_FORUM_ADD', 'a:1:{i:0;s:15:\"Traditional RSC\";}'),
(125, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723455, 'LOG_FORUM_COPIED_PERMISSIONS', 'a:2:{i:0;s:7:\"The Pub\";i:1;s:15:\"Traditional RSC\";}'),
(126, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723458, 'LOG_FORUM_ADD', 'a:1:{i:0;s:13:\"Open World PK\";}'),
(127, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723458, 'LOG_FORUM_COPIED_PERMISSIONS', 'a:2:{i:0;s:7:\"The Pub\";i:1;s:13:\"Open World PK\";}'),
(128, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723688, 'LOG_FORUM_ADD', 'a:1:{i:0;s:12:\"Survival RSC\";}'),
(129, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723688, 'LOG_FORUM_COPIED_PERMISSIONS', 'a:2:{i:0;s:7:\"The Pub\";i:1;s:12:\"Survival RSC\";}'),
(130, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723772, 'LOG_FORUM_ADD', 'a:1:{i:0;s:20:\"Open RSC Bug Tracker\";}'),
(131, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723772, 'LOG_FORUM_COPIED_PERMISSIONS', 'a:2:{i:0;s:5:\"Links\";i:1;s:20:\"Open RSC Bug Tracker\";}'),
(132, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723789, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:14:\"GitHub Project\";}'),
(133, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723795, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:11:\"Bug Tracker\";}'),
(134, 0, 2, 0, 0, 0, 0, '108.162.237.150', 1529723812, 'LOG_FORUM_EDIT', 'a:1:{i:0;s:17:\"Global Discussion\";}'),
(135, 0, 2, 0, 0, 0, 0, '127.0.0.1', 1529771370, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(136, 0, 2, 0, 0, 0, 0, '127.0.0.1', 1529771548, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(137, 0, 2, 0, 0, 0, 0, '127.0.0.1', 1529771762, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(138, 0, 2, 0, 0, 0, 0, '127.0.0.1', 1529771788, 'LOG_CONFIG_SERVER', ''),
(139, 0, 2, 0, 0, 0, 0, '127.0.0.1', 1529771821, 'LOG_USER_USER_UPDATE', 'a:1:{i:0;s:5:\"Kenix\";}'),
(140, 0, 2, 0, 0, 0, 0, '127.0.0.1', 1529771826, 'LOG_USER_DELETED', 'a:1:{i:0;s:5:\"Kenix\";}'),
(141, 0, 2, 0, 0, 0, 0, '127.0.0.1', 1529771834, 'LOG_USER_DELETED', 'a:1:{i:0;s:11:\"cjdickmeyer\";}'),
(142, 0, 2, 0, 0, 0, 0, '127.0.0.1', 1529771842, 'LOG_RESET_ONLINE', ''),
(143, 0, 2, 0, 0, 0, 0, '127.0.0.1', 1529771845, 'LOG_RESYNC_STATS', ''),
(144, 0, 2, 0, 0, 0, 0, '127.0.0.1', 1529771850, 'LOG_PURGE_SESSIONS', ''),
(145, 0, 2, 0, 0, 0, 0, '127.0.0.1', 1529771853, 'LOG_PURGE_CACHE', ''),
(146, 3, 2, 0, 0, 0, 2, '127.0.0.1', 1529771940, 'LOG_USER_NEW_PASSWORD', 'a:1:{i:0;s:7:\"Marwolf\";}'),
(147, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194670, 'LOG_ADMIN_AUTH_SUCCESS', ''),
(148, 3, 2, 0, 0, 0, 2, '192.168.240.1', 1536194745, 'LOG_USER_UPDATE_NAME', 'a:2:{i:0;s:7:\"Marwolf\";i:1;s:4:\"root\";}'),
(149, 3, 2, 0, 0, 0, 2, '192.168.240.1', 1536194745, 'LOG_USER_UPDATE_EMAIL', 'a:3:{i:0;s:7:\"Marwolf\";i:1;s:16:\"cleako@gmail.com\";i:2;s:24:\"openrsc.mailer@gmail.com\";}'),
(150, 3, 2, 0, 0, 0, 2, '192.168.240.1', 1536194745, 'LOG_USER_NEW_PASSWORD', 'a:1:{i:0;s:7:\"Marwolf\";}'),
(151, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194745, 'LOG_USER_USER_UPDATE', 'a:1:{i:0;s:4:\"root\";}'),
(153, 1, 2, 4, 3, 0, 0, '192.168.240.1', 1536194778, 'LOG_DELETE_TOPIC', 'a:3:{i:0;s:19:\"Introduce yourself!\";i:1;s:4:\"root\";i:2;s:0:\"\";}'),
(156, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194877, 'LOG_SEARCH_INDEX_CREATED', 'a:1:{i:0;s:15:\"Sphinx Fulltext\";}'),
(157, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194880, 'LOG_SEARCH_INDEX_CREATED', 'a:1:{i:0;s:14:\"MySQL Fulltext\";}'),
(158, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194883, 'LOG_SEARCH_INDEX_CREATED', 'a:1:{i:0;s:21:\"phpBB Native Fulltext\";}'),
(159, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194900, 'LOG_SEARCH_INDEX_REMOVED', 'a:1:{i:0;s:14:\"MySQL Fulltext\";}'),
(160, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194907, 'LOG_SEARCH_INDEX_CREATED', 'a:1:{i:0;s:14:\"MySQL Fulltext\";}'),
(161, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194910, 'LOG_SEARCH_INDEX_CREATED', 'a:1:{i:0;s:21:\"phpBB Native Fulltext\";}'),
(162, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194939, 'LOG_SEARCH_INDEX_CREATED', 'a:1:{i:0;s:21:\"phpBB Native Fulltext\";}'),
(163, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194981, 'LOG_RESET_ONLINE', ''),
(164, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194983, 'LOG_RESET_DATE', ''),
(165, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194986, 'LOG_RESYNC_STATS', ''),
(166, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194990, 'LOG_RESYNC_POST_MARKING', ''),
(167, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194994, 'LOG_RESYNC_POSTCOUNTS', ''),
(168, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194996, 'LOG_PURGE_SESSIONS', ''),
(169, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536194999, 'LOG_PURGE_CACHE', ''),
(170, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536195037, 'LOG_CONFIG_SERVER', ''),
(171, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536195084, 'LOG_CONFIG_SETTINGS', ''),
(172, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536196070, 'LOG_MODULE_ADD', 'a:1:{i:0;s:21:\"ACP_PHPBB_MEDIA_EMBED\";}'),
(173, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536196070, 'LOG_MODULE_ADD', 'a:1:{i:0;s:30:\"ACP_PHPBB_MEDIA_EMBED_SETTINGS\";}'),
(174, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536196070, 'LOG_MODULE_ADD', 'a:1:{i:0;s:28:\"ACP_PHPBB_MEDIA_EMBED_MANAGE\";}'),
(175, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536196070, 'LOG_EXT_ENABLE', 'a:1:{i:0;s:16:\"phpbb/mediaembed\";}'),
(176, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197309, 'LOG_EXT_ENABLE', 'a:1:{i:0;s:25:\"rmcgirr83/activity24hours\";}'),
(177, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197317, 'LOG_EXT_DISABLE', 'a:1:{i:0;s:16:\"phpbb/mediaembed\";}'),
(178, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197324, 'LOG_EXT_ENABLE', 'a:1:{i:0;s:16:\"phpbb/mediaembed\";}'),
(179, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197328, 'LOG_MODULE_ADD', 'a:1:{i:0;s:12:\"ACP_BH_TITLE\";}'),
(180, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197328, 'LOG_MODULE_ADD', 'a:1:{i:0;s:15:\"ACP_BH_SETTINGS\";}'),
(181, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197328, 'LOG_EXT_ENABLE', 'a:1:{i:0;s:22:\"phpbbmodders/banhammer\";}'),
(182, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197333, 'LOG_MODULE_ADD', 'a:1:{i:0;s:19:\"ADM_LINKED_ACCOUNTS\";}'),
(183, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197333, 'LOG_MODULE_ADD', 'a:1:{i:0;s:28:\"ADM_LINKED_ACCOUNTS_OVERVIEW\";}'),
(184, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197333, 'LOG_MODULE_ADD', 'a:1:{i:0;s:30:\"ADM_LINKED_ACCOUNTS_MANAGEMENT\";}'),
(185, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197333, 'LOG_MODULE_ADD', 'a:1:{i:0;s:15:\"LINKED_ACCOUNTS\";}'),
(186, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197333, 'LOG_MODULE_ADD', 'a:1:{i:0;s:26:\"LINKED_ACCOUNTS_MANAGEMENT\";}'),
(187, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197333, 'LOG_MODULE_ADD', 'a:1:{i:0;s:15:\"LINKING_ACCOUNT\";}'),
(188, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197334, 'LOG_EXT_ENABLE', 'a:1:{i:0;s:21:\"flerex/linkedaccounts\";}'),
(189, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197338, 'LOG_MODULE_ADD', 'a:1:{i:0;s:30:\"ACP_USER_RECENT_ACTIVITY_TITLE\";}'),
(190, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197338, 'LOG_MODULE_ADD', 'a:1:{i:0;s:12:\"ACP_SETTINGS\";}'),
(191, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197338, 'LOG_EXT_ENABLE', 'a:1:{i:0;s:24:\"senky/userrecentactivity\";}'),
(192, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197398, 'LOG_REASON_REMOVED', 'a:1:{i:0;s:5:\"warez\";}'),
(193, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197444, 'BH_SETTINGS_SUCCESS', ''),
(194, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197588, 'LOG_CONFIG_VISUAL', ''),
(195, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197623, 'LOG_CONFIG_VISUAL', ''),
(196, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197632, 'LOG_CONFIG_VISUAL', ''),
(197, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197637, 'LOG_CONFIG_VISUAL', ''),
(198, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197665, 'LOG_CONFIG_REGISTRATION', ''),
(199, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197685, 'LOG_FORUM_DEL_POSTS', 'a:1:{i:0;s:15:\"Traditional RSC\";}'),
(200, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197687, 'LOG_FORUM_SYNC', 'a:1:{i:0;s:13:\"Open World PK\";}'),
(201, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197690, 'LOG_FORUM_DEL_POSTS', 'a:1:{i:0;s:13:\"Open World PK\";}'),
(202, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197694, 'LOG_FORUM_DEL_POSTS', 'a:1:{i:0;s:12:\"Survival RSC\";}'),
(203, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197700, 'LOG_FORUM_DEL_FORUM', 'a:1:{i:0;s:14:\"Distinct Games\";}'),
(204, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197713, 'LOG_FORUM_DEL_POSTS', 'a:1:{i:0;s:4:\"News\";}'),
(205, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197722, 'LOG_FORUM_DEL_POSTS', 'a:1:{i:0;s:5:\"Media\";}'),
(206, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197733, 'LOG_FORUM_DEL_POSTS', 'a:1:{i:0;s:8:\"Requests\";}'),
(207, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197741, 'LOG_FORUM_DEL_POSTS', 'a:1:{i:0;s:22:\"Development Discussion\";}'),
(208, 3, 2, 0, 0, 0, 2, '192.168.240.1', 1536197763, 'LOG_USER_UPDATE_NAME', 'a:2:{i:0;s:4:\"root\";i:1;s:13:\"Administrator\";}'),
(209, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197763, 'LOG_USER_USER_UPDATE', 'a:1:{i:0;s:13:\"Administrator\";}'),
(210, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197774, 'LOG_RESET_ONLINE', ''),
(211, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197776, 'LOG_RESET_DATE', ''),
(212, 0, 2, 0, 0, 0, 0, '192.168.240.1', 1536197779, 'LOG_PURGE_CACHE', '');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_login_attempts`
--

DROP TABLE IF EXISTS `phpbb_login_attempts`;
CREATE TABLE IF NOT EXISTS `phpbb_login_attempts` (
  `attempt_ip` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `attempt_browser` varchar(150) COLLATE utf8_bin NOT NULL DEFAULT '',
  `attempt_forwarded_for` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `attempt_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `username` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '0',
  `username_clean` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '0',
  KEY `att_ip` (`attempt_ip`,`attempt_time`),
  KEY `att_for` (`attempt_forwarded_for`,`attempt_time`),
  KEY `att_time` (`attempt_time`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_migrations`
--

DROP TABLE IF EXISTS `phpbb_migrations`;
CREATE TABLE IF NOT EXISTS `phpbb_migrations` (
  `migration_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `migration_depends_on` text COLLATE utf8_bin NOT NULL,
  `migration_schema_done` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `migration_data_done` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `migration_data_state` text COLLATE utf8_bin NOT NULL,
  `migration_start_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `migration_end_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`migration_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_migrations`
--

INSERT INTO `phpbb_migrations` (`migration_name`, `migration_depends_on`, `migration_schema_done`, `migration_data_done`, `migration_data_state`, `migration_start_time`, `migration_end_time`) VALUES
('\\flerex\\linkedaccounts\\migrations\\release_0_1_0_data', 'a:1:{i:0;s:55:\"\\flerex\\linkedaccounts\\migrations\\release_0_1_0_schemas\";}', 1, 1, '', 1536197333, 1536197334),
('\\flerex\\linkedaccounts\\migrations\\release_0_1_0_schemas', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v320\\v320\";}', 1, 1, '', 1536197333, 1536197333),
('\\flerex\\linkedaccounts\\migrations\\release_0_2_0_data', 'a:1:{i:0;s:55:\"\\flerex\\linkedaccounts\\migrations\\release_0_1_0_schemas\";}', 1, 1, '', 1536197333, 1536197333),
('\\phpbb\\db\\migration\\data\\v30x\\local_url_bbcode', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_12_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_0', 'a:0:{}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_1', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_1_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_10', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_10_rc3\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_10_rc1', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_9\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_10_rc2', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_10_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_10_rc3', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_10_rc2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11_rc2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11_rc1', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_10\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11_rc2', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_12', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_12_rc3\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_12_rc1', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_12_rc2', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_12_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_12_rc3', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_12_rc2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_13', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_13_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_13_pl1', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_13\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_13_rc1', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_12\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_14', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_14_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_14_rc1', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_13\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_1_rc1', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_0\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_2', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_2_rc2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_2_rc1', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_2_rc2', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_2_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_3', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_3_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_3_rc1', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_4', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_4_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_4_rc1', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_3\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_5', 'a:1:{i:0;s:52:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_5_rc1part2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_5_rc1', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_4\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_5_rc1part2', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_5_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_6', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_6_rc4\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_6_rc1', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_5\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_6_rc2', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_6_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_6_rc3', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_6_rc2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_6_rc4', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_6_rc3\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_7', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_7_rc2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_7_pl1', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_7\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_7_rc1', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_6\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_7_rc2', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_7_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_8', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_8_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_8_rc1', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_7_pl1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_9', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_9_rc4\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_9_rc1', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_8\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_9_rc2', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_9_rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_9_rc3', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_9_rc2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v30x\\release_3_0_9_rc4', 'a:1:{i:0;s:47:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_9_rc3\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\acp_prune_users_module', 'a:1:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v310\\beta1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\acp_style_components_module', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\allow_cdn', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v310\\jquery_update\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\alpha1', 'a:18:{i:0;s:46:\"\\phpbb\\db\\migration\\data\\v30x\\local_url_bbcode\";i:1;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_12\";i:2;s:57:\"\\phpbb\\db\\migration\\data\\v310\\acp_style_components_module\";i:3;s:39:\"\\phpbb\\db\\migration\\data\\v310\\allow_cdn\";i:4;s:49:\"\\phpbb\\db\\migration\\data\\v310\\auth_provider_oauth\";i:5;s:37:\"\\phpbb\\db\\migration\\data\\v310\\avatars\";i:6;s:40:\"\\phpbb\\db\\migration\\data\\v310\\boardindex\";i:7;s:44:\"\\phpbb\\db\\migration\\data\\v310\\config_db_text\";i:8;s:45:\"\\phpbb\\db\\migration\\data\\v310\\forgot_password\";i:9;s:41:\"\\phpbb\\db\\migration\\data\\v310\\mod_rewrite\";i:10;s:49:\"\\phpbb\\db\\migration\\data\\v310\\mysql_fulltext_drop\";i:11;s:40:\"\\phpbb\\db\\migration\\data\\v310\\namespaces\";i:12;s:48:\"\\phpbb\\db\\migration\\data\\v310\\notifications_cron\";i:13;s:60:\"\\phpbb\\db\\migration\\data\\v310\\notification_options_reconvert\";i:14;s:38:\"\\phpbb\\db\\migration\\data\\v310\\plupload\";i:15;s:51:\"\\phpbb\\db\\migration\\data\\v310\\signature_module_auth\";i:16;s:52:\"\\phpbb\\db\\migration\\data\\v310\\softdelete_mcp_modules\";i:17;s:38:\"\\phpbb\\db\\migration\\data\\v310\\teampage\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\alpha2', 'a:2:{i:0;s:36:\"\\phpbb\\db\\migration\\data\\v310\\alpha1\";i:1;s:51:\"\\phpbb\\db\\migration\\data\\v310\\notifications_cron_p2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\alpha3', 'a:4:{i:0;s:36:\"\\phpbb\\db\\migration\\data\\v310\\alpha2\";i:1;s:42:\"\\phpbb\\db\\migration\\data\\v310\\avatar_types\";i:2;s:39:\"\\phpbb\\db\\migration\\data\\v310\\passwords\";i:3;s:48:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_types\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\auth_provider_oauth', 'a:0:{}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\auth_provider_oauth2', 'a:1:{i:0;s:49:\"\\phpbb\\db\\migration\\data\\v310\\auth_provider_oauth\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\avatar_types', 'a:2:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";i:1;s:37:\"\\phpbb\\db\\migration\\data\\v310\\avatars\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\avatars', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\beta1', 'a:7:{i:0;s:36:\"\\phpbb\\db\\migration\\data\\v310\\alpha3\";i:1;s:42:\"\\phpbb\\db\\migration\\data\\v310\\passwords_p2\";i:2;s:52:\"\\phpbb\\db\\migration\\data\\v310\\postgres_fulltext_drop\";i:3;s:63:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_change_load_settings\";i:4;s:51:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_location\";i:5;s:54:\"\\phpbb\\db\\migration\\data\\v310\\soft_delete_mod_convert2\";i:6;s:48:\"\\phpbb\\db\\migration\\data\\v310\\ucp_popuppm_module\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\beta2', 'a:3:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v310\\beta1\";i:1;s:52:\"\\phpbb\\db\\migration\\data\\v310\\acp_prune_users_module\";i:2;s:59:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_location_cleanup\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\beta3', 'a:6:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v310\\beta2\";i:1;s:50:\"\\phpbb\\db\\migration\\data\\v310\\auth_provider_oauth2\";i:2;s:48:\"\\phpbb\\db\\migration\\data\\v310\\board_contact_name\";i:3;s:44:\"\\phpbb\\db\\migration\\data\\v310\\jquery_update2\";i:4;s:50:\"\\phpbb\\db\\migration\\data\\v310\\live_searches_config\";i:5;s:49:\"\\phpbb\\db\\migration\\data\\v310\\prune_shadow_topics\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\beta4', 'a:3:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v310\\beta3\";i:1;s:69:\"\\phpbb\\db\\migration\\data\\v310\\extensions_version_check_force_unstable\";i:2;s:58:\"\\phpbb\\db\\migration\\data\\v310\\reset_missing_captcha_plugin\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\board_contact_name', 'a:1:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v310\\beta2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\boardindex', 'a:0:{}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\bot_update', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\rc6\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\captcha_plugins', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\rc2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\config_db_text', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\contact_admin_acp_module', 'a:0:{}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\contact_admin_form', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v310\\config_db_text\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\dev', 'a:5:{i:0;s:40:\"\\phpbb\\db\\migration\\data\\v310\\extensions\";i:1;s:45:\"\\phpbb\\db\\migration\\data\\v310\\style_update_p2\";i:2;s:41:\"\\phpbb\\db\\migration\\data\\v310\\timezone_p2\";i:3;s:52:\"\\phpbb\\db\\migration\\data\\v310\\reported_posts_display\";i:4;s:46:\"\\phpbb\\db\\migration\\data\\v310\\migrations_table\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\extensions', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\extensions_version_check_force_unstable', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\forgot_password', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\gold', 'a:2:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\rc6\";i:1;s:40:\"\\phpbb\\db\\migration\\data\\v310\\bot_update\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\jquery_update', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\jquery_update2', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v310\\jquery_update\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\live_searches_config', 'a:0:{}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\migrations_table', 'a:0:{}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\mod_rewrite', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\mysql_fulltext_drop', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\namespaces', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\notification_options_reconvert', 'a:1:{i:0;s:54:\"\\phpbb\\db\\migration\\data\\v310\\notifications_schema_fix\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\notifications', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\notifications_cron', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v310\\notifications\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\notifications_cron_p2', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v310\\notifications_cron\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\notifications_schema_fix', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v310\\notifications\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\notifications_use_full_name', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\rc3\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\passwords', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\passwords_convert_p1', 'a:1:{i:0;s:42:\"\\phpbb\\db\\migration\\data\\v310\\passwords_p2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\passwords_convert_p2', 'a:1:{i:0;s:50:\"\\phpbb\\db\\migration\\data\\v310\\passwords_convert_p1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\passwords_p2', 'a:1:{i:0;s:39:\"\\phpbb\\db\\migration\\data\\v310\\passwords\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\plupload', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\postgres_fulltext_drop', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_aol', 'a:1:{i:0;s:56:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_yahoo_cleanup\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_aol_cleanup', 'a:1:{i:0;s:46:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_aol\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_change_load_settings', 'a:1:{i:0;s:54:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_aol_cleanup\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_cleanup', 'a:2:{i:0;s:52:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_interests\";i:1;s:53:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_occupation\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_contact_field', 'a:1:{i:0;s:56:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_on_memberlist\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_facebook', 'a:3:{i:0;s:56:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_contact_field\";i:1;s:55:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_show_novalue\";i:2;s:48:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_types\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_field_validation_length', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\rc3\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_googleplus', 'a:3:{i:0;s:56:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_contact_field\";i:1;s:55:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_show_novalue\";i:2;s:48:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_types\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_icq', 'a:1:{i:0;s:56:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_contact_field\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_icq_cleanup', 'a:1:{i:0;s:46:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_icq\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_interests', 'a:2:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_types\";i:1;s:55:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_show_novalue\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_location', 'a:2:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_types\";i:1;s:56:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_on_memberlist\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_location_cleanup', 'a:1:{i:0;s:51:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_location\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_occupation', 'a:1:{i:0;s:52:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_interests\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_on_memberlist', 'a:1:{i:0;s:50:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_cleanup\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_show_novalue', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_types\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_skype', 'a:3:{i:0;s:56:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_contact_field\";i:1;s:55:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_show_novalue\";i:2;s:48:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_types\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_twitter', 'a:3:{i:0;s:56:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_contact_field\";i:1;s:55:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_show_novalue\";i:2;s:48:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_types\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_types', 'a:1:{i:0;s:36:\"\\phpbb\\db\\migration\\data\\v310\\alpha2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_website', 'a:2:{i:0;s:56:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_on_memberlist\";i:1;s:54:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_icq_cleanup\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_website_cleanup', 'a:1:{i:0;s:50:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_website\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_wlm', 'a:1:{i:0;s:58:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_website_cleanup\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_wlm_cleanup', 'a:1:{i:0;s:46:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_wlm\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_yahoo', 'a:1:{i:0;s:54:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_wlm_cleanup\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_yahoo_cleanup', 'a:1:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_yahoo\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\profilefield_youtube', 'a:3:{i:0;s:56:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_contact_field\";i:1;s:55:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_show_novalue\";i:2;s:48:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_types\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\prune_shadow_topics', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\rc1', 'a:9:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v310\\beta4\";i:1;s:54:\"\\phpbb\\db\\migration\\data\\v310\\contact_admin_acp_module\";i:2;s:48:\"\\phpbb\\db\\migration\\data\\v310\\contact_admin_form\";i:3;s:50:\"\\phpbb\\db\\migration\\data\\v310\\passwords_convert_p2\";i:4;s:51:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_facebook\";i:5;s:53:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_googleplus\";i:6;s:48:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_skype\";i:7;s:50:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_twitter\";i:8;s:50:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_youtube\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\rc2', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\rc3', 'a:5:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\rc2\";i:1;s:45:\"\\phpbb\\db\\migration\\data\\v310\\captcha_plugins\";i:2;s:53:\"\\phpbb\\db\\migration\\data\\v310\\rename_too_long_indexes\";i:3;s:41:\"\\phpbb\\db\\migration\\data\\v310\\search_type\";i:4;s:49:\"\\phpbb\\db\\migration\\data\\v310\\topic_sort_username\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\rc4', 'a:2:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\rc3\";i:1;s:57:\"\\phpbb\\db\\migration\\data\\v310\\notifications_use_full_name\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\rc5', 'a:3:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\rc4\";i:1;s:66:\"\\phpbb\\db\\migration\\data\\v310\\profilefield_field_validation_length\";i:2;s:53:\"\\phpbb\\db\\migration\\data\\v310\\remove_acp_styles_cache\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\rc6', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\rc5\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\remove_acp_styles_cache', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\rc4\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\rename_too_long_indexes', 'a:1:{i:0;s:43:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_0\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\reported_posts_display', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\reset_missing_captcha_plugin', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\search_type', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\signature_module_auth', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\soft_delete_mod_convert', 'a:1:{i:0;s:36:\"\\phpbb\\db\\migration\\data\\v310\\alpha3\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\soft_delete_mod_convert2', 'a:1:{i:0;s:53:\"\\phpbb\\db\\migration\\data\\v310\\soft_delete_mod_convert\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\softdelete_mcp_modules', 'a:2:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";i:1;s:43:\"\\phpbb\\db\\migration\\data\\v310\\softdelete_p2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\softdelete_p1', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\softdelete_p2', 'a:2:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";i:1;s:43:\"\\phpbb\\db\\migration\\data\\v310\\softdelete_p1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\style_update_p1', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\style_update_p2', 'a:1:{i:0;s:45:\"\\phpbb\\db\\migration\\data\\v310\\style_update_p1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\teampage', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\timezone', 'a:1:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_11\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\timezone_p2', 'a:1:{i:0;s:38:\"\\phpbb\\db\\migration\\data\\v310\\timezone\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\topic_sort_username', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v310\\ucp_popuppm_module', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v310\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\add_jabber_ssl_context_config_options', 'a:1:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v31x\\v3110\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\add_latest_topics_index', 'a:1:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v31x\\v3110\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\add_log_time_index', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v319\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\add_smtp_ssl_context_config_options', 'a:1:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v31x\\v3110\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\increase_size_of_dateformat', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v317\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\increase_size_of_emotion', 'a:1:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v31x\\v3110\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\m_pm_report', 'a:1:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v316rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\m_softdelete_global', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v311\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\plupload_last_gc_dynamic', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v312\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\profilefield_remove_underscore_from_alpha', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v311\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\profilefield_yahoo_update_url', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v312\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\remove_duplicate_migrations', 'a:1:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v31x\\v3110\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\style_update', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v310\\gold\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\update_custom_bbcodes_with_idn', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v312\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\update_hashes', 'a:1:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v31x\\v3110\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v311', 'a:2:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v310\\gold\";i:1;s:42:\"\\phpbb\\db\\migration\\data\\v31x\\style_update\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v3110', 'a:1:{i:0;s:38:\"\\phpbb\\db\\migration\\data\\v31x\\v3110rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v3110rc1', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v319\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v3111', 'a:1:{i:0;s:38:\"\\phpbb\\db\\migration\\data\\v31x\\v3111rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v3111rc1', 'a:8:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v31x\\v3110\";i:1;s:48:\"\\phpbb\\db\\migration\\data\\v31x\\add_log_time_index\";i:2;s:54:\"\\phpbb\\db\\migration\\data\\v31x\\increase_size_of_emotion\";i:3;s:67:\"\\phpbb\\db\\migration\\data\\v31x\\add_jabber_ssl_context_config_options\";i:4;s:65:\"\\phpbb\\db\\migration\\data\\v31x\\add_smtp_ssl_context_config_options\";i:5;s:43:\"\\phpbb\\db\\migration\\data\\v31x\\update_hashes\";i:6;s:57:\"\\phpbb\\db\\migration\\data\\v31x\\remove_duplicate_migrations\";i:7;s:53:\"\\phpbb\\db\\migration\\data\\v31x\\add_latest_topics_index\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v3112', 'a:1:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v31x\\v3111\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v312', 'a:1:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v312rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v312rc1', 'a:2:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v311\";i:1;s:49:\"\\phpbb\\db\\migration\\data\\v31x\\m_softdelete_global\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v313', 'a:1:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v313rc2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v313rc1', 'a:5:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_13_rc1\";i:1;s:54:\"\\phpbb\\db\\migration\\data\\v31x\\plupload_last_gc_dynamic\";i:2;s:71:\"\\phpbb\\db\\migration\\data\\v31x\\profilefield_remove_underscore_from_alpha\";i:3;s:59:\"\\phpbb\\db\\migration\\data\\v31x\\profilefield_yahoo_update_url\";i:4;s:60:\"\\phpbb\\db\\migration\\data\\v31x\\update_custom_bbcodes_with_idn\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v313rc2', 'a:2:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_13_pl1\";i:1;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v313rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v314', 'a:2:{i:0;s:44:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_14\";i:1;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v314rc2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v314rc1', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v313\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v314rc2', 'a:2:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v30x\\release_3_0_14_rc1\";i:1;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v314rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v315', 'a:1:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v315rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v315rc1', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v314\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v316', 'a:1:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v316rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v316rc1', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v315\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v317', 'a:1:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v317rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v317pl1', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v317\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v317rc1', 'a:2:{i:0;s:41:\"\\phpbb\\db\\migration\\data\\v31x\\m_pm_report\";i:1;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v316\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v318', 'a:1:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v318rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v318rc1', 'a:2:{i:0;s:57:\"\\phpbb\\db\\migration\\data\\v31x\\increase_size_of_dateformat\";i:1;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v317pl1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v319', 'a:1:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v319rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v31x\\v319rc1', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v318\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\add_help_phpbb', 'a:1:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v320\\v320rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\allowed_schemes_links', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v320\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\announce_global_permission', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v320\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\cookie_notice', 'a:1:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v320\\v320rc2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\default_data_type_ids', 'a:2:{i:0;s:36:\"\\phpbb\\db\\migration\\data\\v320\\v320a2\";i:1;s:42:\"\\phpbb\\db\\migration\\data\\v320\\oauth_states\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\dev', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v316\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\font_awesome_update', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v320\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\icons_alt', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v320\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\log_post_id', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v320\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\notifications_board', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v320\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\oauth_states', 'a:1:{i:0;s:49:\"\\phpbb\\db\\migration\\data\\v310\\auth_provider_oauth\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\remote_upload_validation', 'a:1:{i:0;s:36:\"\\phpbb\\db\\migration\\data\\v320\\v320a2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\remove_outdated_media', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v320\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\remove_profilefield_wlm', 'a:1:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v320\\dev\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\report_id_auto_increment', 'a:1:{i:0;s:51:\"\\phpbb\\db\\migration\\data\\v320\\default_data_type_ids\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\text_reparser', 'a:2:{i:0;s:48:\"\\phpbb\\db\\migration\\data\\v310\\contact_admin_form\";i:1;s:51:\"\\phpbb\\db\\migration\\data\\v320\\allowed_schemes_links\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\v320', 'a:2:{i:0;s:54:\"\\phpbb\\db\\migration\\data\\v31x\\increase_size_of_emotion\";i:1;s:43:\"\\phpbb\\db\\migration\\data\\v320\\cookie_notice\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\v320a1', 'a:9:{i:0;s:33:\"\\phpbb\\db\\migration\\data\\v320\\dev\";i:1;s:51:\"\\phpbb\\db\\migration\\data\\v320\\allowed_schemes_links\";i:2;s:56:\"\\phpbb\\db\\migration\\data\\v320\\announce_global_permission\";i:3;s:53:\"\\phpbb\\db\\migration\\data\\v320\\remove_profilefield_wlm\";i:4;s:49:\"\\phpbb\\db\\migration\\data\\v320\\font_awesome_update\";i:5;s:39:\"\\phpbb\\db\\migration\\data\\v320\\icons_alt\";i:6;s:41:\"\\phpbb\\db\\migration\\data\\v320\\log_post_id\";i:7;s:51:\"\\phpbb\\db\\migration\\data\\v320\\remove_outdated_media\";i:8;s:49:\"\\phpbb\\db\\migration\\data\\v320\\notifications_board\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\v320a2', 'a:3:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v317rc1\";i:1;s:43:\"\\phpbb\\db\\migration\\data\\v320\\text_reparser\";i:2;s:36:\"\\phpbb\\db\\migration\\data\\v320\\v320a1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\v320b1', 'a:4:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v31x\\v317pl1\";i:1;s:36:\"\\phpbb\\db\\migration\\data\\v320\\v320a2\";i:2;s:57:\"\\phpbb\\db\\migration\\data\\v31x\\increase_size_of_dateformat\";i:3;s:51:\"\\phpbb\\db\\migration\\data\\v320\\default_data_type_ids\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\v320b2', 'a:3:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v318\";i:1;s:36:\"\\phpbb\\db\\migration\\data\\v320\\v320b1\";i:2;s:54:\"\\phpbb\\db\\migration\\data\\v320\\remote_upload_validation\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\v320rc1', 'a:3:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v319\";i:1;s:54:\"\\phpbb\\db\\migration\\data\\v320\\report_id_auto_increment\";i:2;s:36:\"\\phpbb\\db\\migration\\data\\v320\\v320b2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v320\\v320rc2', 'a:3:{i:0;s:57:\"\\phpbb\\db\\migration\\data\\v31x\\remove_duplicate_migrations\";i:1;s:48:\"\\phpbb\\db\\migration\\data\\v31x\\add_log_time_index\";i:2;s:44:\"\\phpbb\\db\\migration\\data\\v320\\add_help_phpbb\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\cookie_notice_p2', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v320\\v320\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\email_force_sender', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v32x\\v321\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\f_list_topics_permission_add', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v32x\\v321\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\fix_user_styles', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v320\\v320\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\load_user_activity_limit', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v320\\v320\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\merge_duplicate_bbcodes', 'a:0:{}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\update_prosilver_bitfield', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v32x\\v321\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_index_p1', 'a:1:{i:0;s:46:\"\\phpbb\\db\\migration\\data\\v32x\\cookie_notice_p2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_index_p2', 'a:1:{i:0;s:63:\"\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_index_p1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_index_p3', 'a:1:{i:0;s:63:\"\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_index_p2\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_reduce_column_sizes', 'a:1:{i:0;s:63:\"\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_index_p3\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_remove_duplicates', 'a:1:{i:0;s:65:\"\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_temp_index\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_temp_index', 'a:1:{i:0;s:74:\"\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_reduce_column_sizes\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_unique_index', 'a:1:{i:0;s:72:\"\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_remove_duplicates\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\v321', 'a:2:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v31x\\v3111\";i:1;s:37:\"\\phpbb\\db\\migration\\data\\v32x\\v321rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\v321rc1', 'a:4:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v320\\v320\";i:1;s:38:\"\\phpbb\\db\\migration\\data\\v31x\\v3111rc1\";i:2;s:54:\"\\phpbb\\db\\migration\\data\\v32x\\load_user_activity_limit\";i:3;s:67:\"\\phpbb\\db\\migration\\data\\v32x\\user_notifications_table_unique_index\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\v322', 'a:2:{i:0;s:35:\"\\phpbb\\db\\migration\\data\\v31x\\v3112\";i:1;s:37:\"\\phpbb\\db\\migration\\data\\v32x\\v322rc1\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\db\\migration\\data\\v32x\\v322rc1', 'a:5:{i:0;s:45:\"\\phpbb\\db\\migration\\data\\v32x\\fix_user_styles\";i:1;s:55:\"\\phpbb\\db\\migration\\data\\v32x\\update_prosilver_bitfield\";i:2;s:48:\"\\phpbb\\db\\migration\\data\\v32x\\email_force_sender\";i:3;s:58:\"\\phpbb\\db\\migration\\data\\v32x\\f_list_topics_permission_add\";i:4;s:53:\"\\phpbb\\db\\migration\\data\\v32x\\merge_duplicate_bbcodes\";}', 1, 1, '', 1529675817, 1529675817),
('\\phpbb\\mediaembed\\migrations\\m1_install_data', 'a:1:{i:0;s:37:\"\\phpbb\\db\\migration\\data\\v320\\v320rc1\";}', 1, 1, '', 1536196069, 1536196070),
('\\phpbb\\mediaembed\\migrations\\m2_signature_config', 'a:1:{i:0;s:44:\"\\phpbb\\mediaembed\\migrations\\m1_install_data\";}', 1, 1, '', 1536196070, 1536196070),
('\\phpbb\\mediaembed\\migrations\\m3_plain_urls_config', 'a:1:{i:0;s:44:\"\\phpbb\\mediaembed\\migrations\\m1_install_data\";}', 1, 1, '', 1536196070, 1536196070),
('\\phpbb\\mediaembed\\migrations\\m4_permissions', 'a:2:{i:0;s:44:\"\\phpbb\\mediaembed\\migrations\\m1_install_data\";i:1;s:49:\"\\phpbb\\mediaembed\\migrations\\m3_plain_urls_config\";}', 1, 1, '', 1536196070, 1536196070),
('\\phpbbmodders\\banhammer\\migrations\\install_banhammer', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v31x\\v312\";}', 1, 1, '', 1536197328, 1536197328),
('\\phpbbmodders\\banhammer\\migrations\\remove_config', 'a:1:{i:0;s:52:\"\\phpbbmodders\\banhammer\\migrations\\install_banhammer\";}', 1, 1, '', 1536197328, 1536197328),
('\\phpbbmodders\\banhammer\\migrations\\v101_data', 'a:1:{i:0;s:52:\"\\phpbbmodders\\banhammer\\migrations\\install_banhammer\";}', 1, 1, '', 1536197328, 1536197328),
('\\phpbbmodders\\banhammer\\migrations\\v104_data', 'a:1:{i:0;s:44:\"\\phpbbmodders\\banhammer\\migrations\\v101_data\";}', 1, 1, '', 1536197328, 1536197328),
('\\senky\\userrecentactivity\\migrations\\release_1_0_0', 'a:1:{i:0;s:34:\"\\phpbb\\db\\migration\\data\\v310\\gold\";}', 1, 1, '', 1536197338, 1536197338),
('\\senky\\userrecentactivity\\migrations\\release_2_1_0', 'a:1:{i:0;s:50:\"\\senky\\userrecentactivity\\migrations\\release_1_0_0\";}', 1, 1, '', 1536197338, 1536197338);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_moderator_cache`
--

DROP TABLE IF EXISTS `phpbb_moderator_cache`;
CREATE TABLE IF NOT EXISTS `phpbb_moderator_cache` (
  `forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `username` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `group_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `group_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `display_on_index` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  KEY `disp_idx` (`display_on_index`),
  KEY `forum_id` (`forum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_modules`
--

DROP TABLE IF EXISTS `phpbb_modules`;
CREATE TABLE IF NOT EXISTS `phpbb_modules` (
  `module_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `module_enabled` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `module_display` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `module_basename` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `module_class` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `parent_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `left_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `right_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `module_langname` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `module_mode` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `module_auth` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`module_id`),
  KEY `left_right_id` (`left_id`,`right_id`),
  KEY `module_enabled` (`module_enabled`),
  KEY `class_left_id` (`module_class`,`left_id`)
) ENGINE=InnoDB AUTO_INCREMENT=221 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_modules`
--

INSERT INTO `phpbb_modules` (`module_id`, `module_enabled`, `module_display`, `module_basename`, `module_class`, `parent_id`, `left_id`, `right_id`, `module_langname`, `module_mode`, `module_auth`) VALUES
(1, 1, 1, '', 'acp', 0, 1, 68, 'ACP_CAT_GENERAL', '', ''),
(2, 1, 1, '', 'acp', 1, 4, 17, 'ACP_QUICK_ACCESS', '', ''),
(3, 1, 1, '', 'acp', 1, 18, 45, 'ACP_BOARD_CONFIGURATION', '', ''),
(4, 1, 1, '', 'acp', 1, 46, 53, 'ACP_CLIENT_COMMUNICATION', '', ''),
(5, 1, 1, '', 'acp', 1, 54, 67, 'ACP_SERVER_CONFIGURATION', '', ''),
(6, 1, 1, '', 'acp', 0, 69, 88, 'ACP_CAT_FORUMS', '', ''),
(7, 1, 1, '', 'acp', 6, 70, 75, 'ACP_MANAGE_FORUMS', '', ''),
(8, 1, 1, '', 'acp', 6, 76, 87, 'ACP_FORUM_BASED_PERMISSIONS', '', ''),
(9, 1, 1, '', 'acp', 0, 89, 122, 'ACP_CAT_POSTING', '', ''),
(10, 1, 1, '', 'acp', 9, 90, 103, 'ACP_MESSAGES', '', ''),
(11, 1, 1, '', 'acp', 9, 110, 121, 'ACP_ATTACHMENTS', '', ''),
(12, 1, 1, '', 'acp', 0, 123, 180, 'ACP_CAT_USERGROUP', '', ''),
(13, 1, 1, '', 'acp', 12, 124, 159, 'ACP_CAT_USERS', '', ''),
(14, 1, 1, '', 'acp', 12, 160, 169, 'ACP_GROUPS', '', ''),
(15, 1, 1, '', 'acp', 12, 170, 179, 'ACP_USER_SECURITY', '', ''),
(16, 1, 1, '', 'acp', 0, 181, 230, 'ACP_CAT_PERMISSIONS', '', ''),
(17, 1, 1, '', 'acp', 16, 184, 193, 'ACP_GLOBAL_PERMISSIONS', '', ''),
(18, 1, 1, '', 'acp', 16, 194, 205, 'ACP_FORUM_BASED_PERMISSIONS', '', ''),
(19, 1, 1, '', 'acp', 16, 206, 215, 'ACP_PERMISSION_ROLES', '', ''),
(20, 1, 1, '', 'acp', 16, 216, 229, 'ACP_PERMISSION_MASKS', '', ''),
(21, 1, 1, '', 'acp', 0, 231, 246, 'ACP_CAT_CUSTOMISE', '', ''),
(22, 1, 1, '', 'acp', 21, 236, 241, 'ACP_STYLE_MANAGEMENT', '', ''),
(23, 1, 1, '', 'acp', 21, 232, 235, 'ACP_EXTENSION_MANAGEMENT', '', ''),
(24, 1, 1, '', 'acp', 21, 242, 245, 'ACP_LANGUAGE', '', ''),
(25, 1, 1, '', 'acp', 0, 247, 266, 'ACP_CAT_MAINTENANCE', '', ''),
(26, 1, 1, '', 'acp', 25, 248, 257, 'ACP_FORUM_LOGS', '', ''),
(27, 1, 1, '', 'acp', 25, 258, 265, 'ACP_CAT_DATABASE', '', ''),
(28, 1, 1, '', 'acp', 0, 267, 290, 'ACP_CAT_SYSTEM', '', ''),
(29, 1, 1, '', 'acp', 28, 268, 271, 'ACP_AUTOMATION', '', ''),
(30, 1, 1, '', 'acp', 28, 272, 281, 'ACP_GENERAL_TASKS', '', ''),
(31, 1, 1, '', 'acp', 28, 282, 289, 'ACP_MODULE_MANAGEMENT', '', ''),
(32, 1, 1, '', 'acp', 0, 291, 306, 'ACP_CAT_DOT_MODS', '', ''),
(33, 1, 1, '\\phpbb\\viglink\\acp\\viglink_module', 'acp', 3, 19, 20, 'ACP_VIGLINK_SETTINGS', 'settings', 'ext_phpbb/viglink && acl_a_board'),
(34, 1, 1, 'acp_attachments', 'acp', 3, 21, 22, 'ACP_ATTACHMENT_SETTINGS', 'attach', 'acl_a_attach'),
(35, 1, 1, 'acp_attachments', 'acp', 11, 111, 112, 'ACP_ATTACHMENT_SETTINGS', 'attach', 'acl_a_attach'),
(36, 1, 1, 'acp_attachments', 'acp', 11, 113, 114, 'ACP_MANAGE_EXTENSIONS', 'extensions', 'acl_a_attach'),
(37, 1, 1, 'acp_attachments', 'acp', 11, 115, 116, 'ACP_EXTENSION_GROUPS', 'ext_groups', 'acl_a_attach'),
(38, 1, 1, 'acp_attachments', 'acp', 11, 117, 118, 'ACP_ORPHAN_ATTACHMENTS', 'orphan', 'acl_a_attach'),
(39, 1, 1, 'acp_attachments', 'acp', 11, 119, 120, 'ACP_MANAGE_ATTACHMENTS', 'manage', 'acl_a_attach'),
(40, 1, 1, 'acp_ban', 'acp', 15, 171, 172, 'ACP_BAN_EMAILS', 'email', 'acl_a_ban'),
(41, 1, 1, 'acp_ban', 'acp', 15, 173, 174, 'ACP_BAN_IPS', 'ip', 'acl_a_ban'),
(42, 1, 1, 'acp_ban', 'acp', 15, 175, 176, 'ACP_BAN_USERNAMES', 'user', 'acl_a_ban'),
(43, 1, 1, 'acp_bbcodes', 'acp', 10, 91, 92, 'ACP_BBCODES', 'bbcodes', 'acl_a_bbcode'),
(44, 1, 1, 'acp_board', 'acp', 3, 23, 24, 'ACP_BOARD_SETTINGS', 'settings', 'acl_a_board'),
(45, 1, 1, 'acp_board', 'acp', 3, 25, 26, 'ACP_BOARD_FEATURES', 'features', 'acl_a_board'),
(46, 1, 1, 'acp_board', 'acp', 3, 27, 28, 'ACP_AVATAR_SETTINGS', 'avatar', 'acl_a_board'),
(47, 1, 1, 'acp_board', 'acp', 3, 29, 30, 'ACP_MESSAGE_SETTINGS', 'message', 'acl_a_board'),
(48, 1, 1, 'acp_board', 'acp', 10, 93, 94, 'ACP_MESSAGE_SETTINGS', 'message', 'acl_a_board'),
(49, 1, 1, 'acp_board', 'acp', 3, 31, 32, 'ACP_POST_SETTINGS', 'post', 'acl_a_board'),
(50, 1, 1, 'acp_board', 'acp', 10, 95, 96, 'ACP_POST_SETTINGS', 'post', 'acl_a_board'),
(51, 1, 1, 'acp_board', 'acp', 3, 33, 34, 'ACP_SIGNATURE_SETTINGS', 'signature', 'acl_a_board'),
(52, 1, 1, 'acp_board', 'acp', 3, 35, 36, 'ACP_FEED_SETTINGS', 'feed', 'acl_a_board'),
(53, 1, 1, 'acp_board', 'acp', 3, 37, 38, 'ACP_REGISTER_SETTINGS', 'registration', 'acl_a_board'),
(54, 1, 1, 'acp_board', 'acp', 4, 47, 48, 'ACP_AUTH_SETTINGS', 'auth', 'acl_a_server'),
(55, 1, 1, 'acp_board', 'acp', 4, 49, 50, 'ACP_EMAIL_SETTINGS', 'email', 'acl_a_server'),
(56, 1, 1, 'acp_board', 'acp', 5, 55, 56, 'ACP_COOKIE_SETTINGS', 'cookie', 'acl_a_server'),
(57, 1, 1, 'acp_board', 'acp', 5, 57, 58, 'ACP_SERVER_SETTINGS', 'server', 'acl_a_server'),
(58, 1, 1, 'acp_board', 'acp', 5, 59, 60, 'ACP_SECURITY_SETTINGS', 'security', 'acl_a_server'),
(59, 1, 1, 'acp_board', 'acp', 5, 61, 62, 'ACP_LOAD_SETTINGS', 'load', 'acl_a_server'),
(60, 1, 1, 'acp_bots', 'acp', 30, 273, 274, 'ACP_BOTS', 'bots', 'acl_a_bots'),
(61, 1, 1, 'acp_captcha', 'acp', 3, 39, 40, 'ACP_VC_SETTINGS', 'visual', 'acl_a_board'),
(62, 1, 0, 'acp_captcha', 'acp', 3, 41, 42, 'ACP_VC_CAPTCHA_DISPLAY', 'img', 'acl_a_board'),
(63, 1, 1, 'acp_contact', 'acp', 3, 43, 44, 'ACP_CONTACT_SETTINGS', 'contact', 'acl_a_board'),
(64, 1, 1, 'acp_database', 'acp', 27, 259, 260, 'ACP_BACKUP', 'backup', 'acl_a_backup'),
(65, 1, 1, 'acp_database', 'acp', 27, 261, 262, 'ACP_RESTORE', 'restore', 'acl_a_backup'),
(66, 1, 1, 'acp_disallow', 'acp', 15, 177, 178, 'ACP_DISALLOW_USERNAMES', 'usernames', 'acl_a_names'),
(67, 1, 1, 'acp_email', 'acp', 30, 275, 276, 'ACP_MASS_EMAIL', 'email', 'acl_a_email && cfg_email_enable'),
(68, 1, 1, 'acp_extensions', 'acp', 23, 233, 234, 'ACP_EXTENSIONS', 'main', 'acl_a_extensions'),
(69, 1, 1, 'acp_forums', 'acp', 7, 71, 72, 'ACP_MANAGE_FORUMS', 'manage', 'acl_a_forum'),
(70, 1, 1, 'acp_groups', 'acp', 14, 161, 162, 'ACP_GROUPS_MANAGE', 'manage', 'acl_a_group'),
(71, 1, 1, 'acp_groups', 'acp', 14, 163, 164, 'ACP_GROUPS_POSITION', 'position', 'acl_a_group'),
(72, 1, 1, 'acp_help_phpbb', 'acp', 5, 63, 64, 'ACP_HELP_PHPBB', 'help_phpbb', 'acl_a_server'),
(73, 1, 1, 'acp_icons', 'acp', 10, 97, 98, 'ACP_ICONS', 'icons', 'acl_a_icons'),
(74, 1, 1, 'acp_icons', 'acp', 10, 99, 100, 'ACP_SMILIES', 'smilies', 'acl_a_icons'),
(75, 1, 1, 'acp_inactive', 'acp', 13, 125, 126, 'ACP_INACTIVE_USERS', 'list', 'acl_a_user'),
(76, 1, 1, 'acp_jabber', 'acp', 4, 51, 52, 'ACP_JABBER_SETTINGS', 'settings', 'acl_a_jabber'),
(77, 1, 1, 'acp_language', 'acp', 24, 243, 244, 'ACP_LANGUAGE_PACKS', 'lang_packs', 'acl_a_language'),
(78, 1, 1, 'acp_logs', 'acp', 26, 249, 250, 'ACP_ADMIN_LOGS', 'admin', 'acl_a_viewlogs'),
(79, 1, 1, 'acp_logs', 'acp', 26, 251, 252, 'ACP_MOD_LOGS', 'mod', 'acl_a_viewlogs'),
(80, 1, 1, 'acp_logs', 'acp', 26, 253, 254, 'ACP_USERS_LOGS', 'users', 'acl_a_viewlogs'),
(81, 1, 1, 'acp_logs', 'acp', 26, 255, 256, 'ACP_CRITICAL_LOGS', 'critical', 'acl_a_viewlogs'),
(82, 1, 1, 'acp_main', 'acp', 1, 2, 3, 'ACP_INDEX', 'main', ''),
(83, 1, 1, 'acp_modules', 'acp', 31, 283, 284, 'ACP', 'acp', 'acl_a_modules'),
(84, 1, 1, 'acp_modules', 'acp', 31, 285, 286, 'UCP', 'ucp', 'acl_a_modules'),
(85, 1, 1, 'acp_modules', 'acp', 31, 287, 288, 'MCP', 'mcp', 'acl_a_modules'),
(86, 1, 1, 'acp_permission_roles', 'acp', 19, 207, 208, 'ACP_ADMIN_ROLES', 'admin_roles', 'acl_a_roles && acl_a_aauth'),
(87, 1, 1, 'acp_permission_roles', 'acp', 19, 209, 210, 'ACP_USER_ROLES', 'user_roles', 'acl_a_roles && acl_a_uauth'),
(88, 1, 1, 'acp_permission_roles', 'acp', 19, 211, 212, 'ACP_MOD_ROLES', 'mod_roles', 'acl_a_roles && acl_a_mauth'),
(89, 1, 1, 'acp_permission_roles', 'acp', 19, 213, 214, 'ACP_FORUM_ROLES', 'forum_roles', 'acl_a_roles && acl_a_fauth'),
(90, 1, 1, 'acp_permissions', 'acp', 16, 182, 183, 'ACP_PERMISSIONS', 'intro', 'acl_a_authusers || acl_a_authgroups || acl_a_viewauth'),
(91, 1, 0, 'acp_permissions', 'acp', 20, 217, 218, 'ACP_PERMISSION_TRACE', 'trace', 'acl_a_viewauth'),
(92, 1, 1, 'acp_permissions', 'acp', 18, 195, 196, 'ACP_FORUM_PERMISSIONS', 'setting_forum_local', 'acl_a_fauth && (acl_a_authusers || acl_a_authgroups)'),
(93, 1, 1, 'acp_permissions', 'acp', 18, 197, 198, 'ACP_FORUM_PERMISSIONS_COPY', 'setting_forum_copy', 'acl_a_fauth && acl_a_authusers && acl_a_authgroups && acl_a_mauth'),
(94, 1, 1, 'acp_permissions', 'acp', 18, 199, 200, 'ACP_FORUM_MODERATORS', 'setting_mod_local', 'acl_a_mauth && (acl_a_authusers || acl_a_authgroups)'),
(95, 1, 1, 'acp_permissions', 'acp', 17, 185, 186, 'ACP_USERS_PERMISSIONS', 'setting_user_global', 'acl_a_authusers && (acl_a_aauth || acl_a_mauth || acl_a_uauth)'),
(96, 1, 1, 'acp_permissions', 'acp', 13, 129, 130, 'ACP_USERS_PERMISSIONS', 'setting_user_global', 'acl_a_authusers && (acl_a_aauth || acl_a_mauth || acl_a_uauth)'),
(97, 1, 1, 'acp_permissions', 'acp', 18, 201, 202, 'ACP_USERS_FORUM_PERMISSIONS', 'setting_user_local', 'acl_a_authusers && (acl_a_mauth || acl_a_fauth)'),
(98, 1, 1, 'acp_permissions', 'acp', 13, 131, 132, 'ACP_USERS_FORUM_PERMISSIONS', 'setting_user_local', 'acl_a_authusers && (acl_a_mauth || acl_a_fauth)'),
(99, 1, 1, 'acp_permissions', 'acp', 17, 187, 188, 'ACP_GROUPS_PERMISSIONS', 'setting_group_global', 'acl_a_authgroups && (acl_a_aauth || acl_a_mauth || acl_a_uauth)'),
(100, 1, 1, 'acp_permissions', 'acp', 14, 165, 166, 'ACP_GROUPS_PERMISSIONS', 'setting_group_global', 'acl_a_authgroups && (acl_a_aauth || acl_a_mauth || acl_a_uauth)'),
(101, 1, 1, 'acp_permissions', 'acp', 18, 203, 204, 'ACP_GROUPS_FORUM_PERMISSIONS', 'setting_group_local', 'acl_a_authgroups && (acl_a_mauth || acl_a_fauth)'),
(102, 1, 1, 'acp_permissions', 'acp', 14, 167, 168, 'ACP_GROUPS_FORUM_PERMISSIONS', 'setting_group_local', 'acl_a_authgroups && (acl_a_mauth || acl_a_fauth)'),
(103, 1, 1, 'acp_permissions', 'acp', 17, 189, 190, 'ACP_ADMINISTRATORS', 'setting_admin_global', 'acl_a_aauth && (acl_a_authusers || acl_a_authgroups)'),
(104, 1, 1, 'acp_permissions', 'acp', 17, 191, 192, 'ACP_GLOBAL_MODERATORS', 'setting_mod_global', 'acl_a_mauth && (acl_a_authusers || acl_a_authgroups)'),
(105, 1, 1, 'acp_permissions', 'acp', 20, 219, 220, 'ACP_VIEW_ADMIN_PERMISSIONS', 'view_admin_global', 'acl_a_viewauth'),
(106, 1, 1, 'acp_permissions', 'acp', 20, 221, 222, 'ACP_VIEW_USER_PERMISSIONS', 'view_user_global', 'acl_a_viewauth'),
(107, 1, 1, 'acp_permissions', 'acp', 20, 223, 224, 'ACP_VIEW_GLOBAL_MOD_PERMISSIONS', 'view_mod_global', 'acl_a_viewauth'),
(108, 1, 1, 'acp_permissions', 'acp', 20, 225, 226, 'ACP_VIEW_FORUM_MOD_PERMISSIONS', 'view_mod_local', 'acl_a_viewauth'),
(109, 1, 1, 'acp_permissions', 'acp', 20, 227, 228, 'ACP_VIEW_FORUM_PERMISSIONS', 'view_forum_local', 'acl_a_viewauth'),
(110, 1, 1, 'acp_php_info', 'acp', 30, 277, 278, 'ACP_PHP_INFO', 'info', 'acl_a_phpinfo'),
(111, 1, 1, 'acp_profile', 'acp', 13, 133, 134, 'ACP_CUSTOM_PROFILE_FIELDS', 'profile', 'acl_a_profile'),
(112, 1, 1, 'acp_prune', 'acp', 7, 73, 74, 'ACP_PRUNE_FORUMS', 'forums', 'acl_a_prune'),
(113, 1, 1, 'acp_prune', 'acp', 13, 135, 136, 'ACP_PRUNE_USERS', 'users', 'acl_a_userdel'),
(114, 1, 1, 'acp_ranks', 'acp', 13, 137, 138, 'ACP_MANAGE_RANKS', 'ranks', 'acl_a_ranks'),
(115, 1, 1, 'acp_reasons', 'acp', 30, 279, 280, 'ACP_MANAGE_REASONS', 'main', 'acl_a_reasons'),
(116, 1, 1, 'acp_search', 'acp', 5, 65, 66, 'ACP_SEARCH_SETTINGS', 'settings', 'acl_a_search'),
(117, 1, 1, 'acp_search', 'acp', 27, 263, 264, 'ACP_SEARCH_INDEX', 'index', 'acl_a_search'),
(118, 1, 1, 'acp_styles', 'acp', 22, 237, 238, 'ACP_STYLES', 'style', 'acl_a_styles'),
(119, 1, 1, 'acp_styles', 'acp', 22, 239, 240, 'ACP_STYLES_INSTALL', 'install', 'acl_a_styles'),
(120, 1, 1, 'acp_update', 'acp', 29, 269, 270, 'ACP_VERSION_CHECK', 'version_check', 'acl_a_board'),
(121, 1, 1, 'acp_users', 'acp', 13, 127, 128, 'ACP_MANAGE_USERS', 'overview', 'acl_a_user'),
(122, 1, 0, 'acp_users', 'acp', 13, 139, 140, 'ACP_USER_FEEDBACK', 'feedback', 'acl_a_user'),
(123, 1, 0, 'acp_users', 'acp', 13, 141, 142, 'ACP_USER_WARNINGS', 'warnings', 'acl_a_user'),
(124, 1, 0, 'acp_users', 'acp', 13, 143, 144, 'ACP_USER_PROFILE', 'profile', 'acl_a_user'),
(125, 1, 0, 'acp_users', 'acp', 13, 145, 146, 'ACP_USER_PREFS', 'prefs', 'acl_a_user'),
(126, 1, 0, 'acp_users', 'acp', 13, 147, 148, 'ACP_USER_AVATAR', 'avatar', 'acl_a_user'),
(127, 1, 0, 'acp_users', 'acp', 13, 149, 150, 'ACP_USER_RANK', 'rank', 'acl_a_user'),
(128, 1, 0, 'acp_users', 'acp', 13, 151, 152, 'ACP_USER_SIG', 'sig', 'acl_a_user'),
(129, 1, 0, 'acp_users', 'acp', 13, 153, 154, 'ACP_USER_GROUPS', 'groups', 'acl_a_user && acl_a_group'),
(130, 1, 0, 'acp_users', 'acp', 13, 155, 156, 'ACP_USER_PERM', 'perm', 'acl_a_user && acl_a_viewauth'),
(131, 1, 0, 'acp_users', 'acp', 13, 157, 158, 'ACP_USER_ATTACH', 'attach', 'acl_a_user'),
(132, 1, 1, 'acp_words', 'acp', 10, 101, 102, 'ACP_WORDS', 'words', 'acl_a_words'),
(133, 1, 1, 'acp_users', 'acp', 2, 5, 6, 'ACP_MANAGE_USERS', 'overview', 'acl_a_user'),
(134, 1, 1, 'acp_groups', 'acp', 2, 7, 8, 'ACP_GROUPS_MANAGE', 'manage', 'acl_a_group'),
(135, 1, 1, 'acp_forums', 'acp', 2, 9, 10, 'ACP_MANAGE_FORUMS', 'manage', 'acl_a_forum'),
(136, 1, 1, 'acp_logs', 'acp', 2, 11, 12, 'ACP_MOD_LOGS', 'mod', 'acl_a_viewlogs'),
(137, 1, 1, 'acp_bots', 'acp', 2, 13, 14, 'ACP_BOTS', 'bots', 'acl_a_bots'),
(138, 1, 1, 'acp_php_info', 'acp', 2, 15, 16, 'ACP_PHP_INFO', 'info', 'acl_a_phpinfo'),
(139, 1, 1, 'acp_permissions', 'acp', 8, 77, 78, 'ACP_FORUM_PERMISSIONS', 'setting_forum_local', 'acl_a_fauth && (acl_a_authusers || acl_a_authgroups)'),
(140, 1, 1, 'acp_permissions', 'acp', 8, 79, 80, 'ACP_FORUM_PERMISSIONS_COPY', 'setting_forum_copy', 'acl_a_fauth && acl_a_authusers && acl_a_authgroups && acl_a_mauth'),
(141, 1, 1, 'acp_permissions', 'acp', 8, 81, 82, 'ACP_FORUM_MODERATORS', 'setting_mod_local', 'acl_a_mauth && (acl_a_authusers || acl_a_authgroups)'),
(142, 1, 1, 'acp_permissions', 'acp', 8, 83, 84, 'ACP_USERS_FORUM_PERMISSIONS', 'setting_user_local', 'acl_a_authusers && (acl_a_mauth || acl_a_fauth)'),
(143, 1, 1, 'acp_permissions', 'acp', 8, 85, 86, 'ACP_GROUPS_FORUM_PERMISSIONS', 'setting_group_local', 'acl_a_authgroups && (acl_a_mauth || acl_a_fauth)'),
(144, 1, 1, '', 'mcp', 0, 1, 10, 'MCP_MAIN', '', ''),
(145, 1, 1, '', 'mcp', 0, 11, 22, 'MCP_QUEUE', '', ''),
(146, 1, 1, '', 'mcp', 0, 23, 36, 'MCP_REPORTS', '', ''),
(147, 1, 1, '', 'mcp', 0, 37, 42, 'MCP_NOTES', '', ''),
(148, 1, 1, '', 'mcp', 0, 43, 52, 'MCP_WARN', '', ''),
(149, 1, 1, '', 'mcp', 0, 53, 60, 'MCP_LOGS', '', ''),
(150, 1, 1, '', 'mcp', 0, 61, 68, 'MCP_BAN', '', ''),
(151, 1, 1, 'mcp_ban', 'mcp', 150, 62, 63, 'MCP_BAN_USERNAMES', 'user', 'acl_m_ban'),
(152, 1, 1, 'mcp_ban', 'mcp', 150, 64, 65, 'MCP_BAN_IPS', 'ip', 'acl_m_ban'),
(153, 1, 1, 'mcp_ban', 'mcp', 150, 66, 67, 'MCP_BAN_EMAILS', 'email', 'acl_m_ban'),
(154, 1, 1, 'mcp_logs', 'mcp', 149, 54, 55, 'MCP_LOGS_FRONT', 'front', 'acl_m_ || aclf_m_'),
(155, 1, 1, 'mcp_logs', 'mcp', 149, 56, 57, 'MCP_LOGS_FORUM_VIEW', 'forum_logs', 'acl_m_,$id'),
(156, 1, 1, 'mcp_logs', 'mcp', 149, 58, 59, 'MCP_LOGS_TOPIC_VIEW', 'topic_logs', 'acl_m_,$id'),
(157, 1, 1, 'mcp_main', 'mcp', 144, 2, 3, 'MCP_MAIN_FRONT', 'front', ''),
(158, 1, 1, 'mcp_main', 'mcp', 144, 4, 5, 'MCP_MAIN_FORUM_VIEW', 'forum_view', 'acl_m_,$id'),
(159, 1, 1, 'mcp_main', 'mcp', 144, 6, 7, 'MCP_MAIN_TOPIC_VIEW', 'topic_view', 'acl_m_,$id'),
(160, 1, 1, 'mcp_main', 'mcp', 144, 8, 9, 'MCP_MAIN_POST_DETAILS', 'post_details', 'acl_m_,$id || (!$id && aclf_m_)'),
(161, 1, 1, 'mcp_notes', 'mcp', 147, 38, 39, 'MCP_NOTES_FRONT', 'front', ''),
(162, 1, 1, 'mcp_notes', 'mcp', 147, 40, 41, 'MCP_NOTES_USER', 'user_notes', ''),
(163, 1, 1, 'mcp_pm_reports', 'mcp', 146, 30, 31, 'MCP_PM_REPORTS_OPEN', 'pm_reports', 'acl_m_pm_report'),
(164, 1, 1, 'mcp_pm_reports', 'mcp', 146, 32, 33, 'MCP_PM_REPORTS_CLOSED', 'pm_reports_closed', 'acl_m_pm_report'),
(165, 1, 1, 'mcp_pm_reports', 'mcp', 146, 34, 35, 'MCP_PM_REPORT_DETAILS', 'pm_report_details', 'acl_m_pm_report'),
(166, 1, 1, 'mcp_queue', 'mcp', 145, 12, 13, 'MCP_QUEUE_UNAPPROVED_TOPICS', 'unapproved_topics', 'aclf_m_approve'),
(167, 1, 1, 'mcp_queue', 'mcp', 145, 14, 15, 'MCP_QUEUE_UNAPPROVED_POSTS', 'unapproved_posts', 'aclf_m_approve'),
(168, 1, 1, 'mcp_queue', 'mcp', 145, 16, 17, 'MCP_QUEUE_DELETED_TOPICS', 'deleted_topics', 'aclf_m_approve'),
(169, 1, 1, 'mcp_queue', 'mcp', 145, 18, 19, 'MCP_QUEUE_DELETED_POSTS', 'deleted_posts', 'aclf_m_approve'),
(170, 1, 1, 'mcp_queue', 'mcp', 145, 20, 21, 'MCP_QUEUE_APPROVE_DETAILS', 'approve_details', 'acl_m_approve,$id || (!$id && aclf_m_approve)'),
(171, 1, 1, 'mcp_reports', 'mcp', 146, 24, 25, 'MCP_REPORTS_OPEN', 'reports', 'aclf_m_report'),
(172, 1, 1, 'mcp_reports', 'mcp', 146, 26, 27, 'MCP_REPORTS_CLOSED', 'reports_closed', 'aclf_m_report'),
(173, 1, 1, 'mcp_reports', 'mcp', 146, 28, 29, 'MCP_REPORT_DETAILS', 'report_details', 'acl_m_report,$id || (!$id && aclf_m_report)'),
(174, 1, 1, 'mcp_warn', 'mcp', 148, 44, 45, 'MCP_WARN_FRONT', 'front', 'aclf_m_warn'),
(175, 1, 1, 'mcp_warn', 'mcp', 148, 46, 47, 'MCP_WARN_LIST', 'list', 'aclf_m_warn'),
(176, 1, 1, 'mcp_warn', 'mcp', 148, 48, 49, 'MCP_WARN_USER', 'warn_user', 'aclf_m_warn'),
(177, 1, 1, 'mcp_warn', 'mcp', 148, 50, 51, 'MCP_WARN_POST', 'warn_post', 'acl_m_warn && acl_f_read,$id'),
(178, 1, 1, '', 'ucp', 0, 1, 14, 'UCP_MAIN', '', ''),
(179, 1, 1, '', 'ucp', 0, 15, 28, 'UCP_PROFILE', '', ''),
(180, 1, 1, '', 'ucp', 0, 29, 38, 'UCP_PREFS', '', ''),
(181, 1, 1, 'ucp_pm', 'ucp', 0, 39, 48, 'UCP_PM', '', ''),
(182, 1, 1, '', 'ucp', 0, 49, 54, 'UCP_USERGROUPS', '', ''),
(183, 1, 1, '', 'ucp', 0, 55, 60, 'UCP_ZEBRA', '', ''),
(184, 1, 1, 'ucp_attachments', 'ucp', 178, 10, 11, 'UCP_MAIN_ATTACHMENTS', 'attachments', 'acl_u_attach'),
(185, 1, 1, 'ucp_auth_link', 'ucp', 179, 26, 27, 'UCP_AUTH_LINK_MANAGE', 'auth_link', 'authmethod_oauth'),
(186, 1, 1, 'ucp_groups', 'ucp', 182, 50, 51, 'UCP_USERGROUPS_MEMBER', 'membership', ''),
(187, 1, 1, 'ucp_groups', 'ucp', 182, 52, 53, 'UCP_USERGROUPS_MANAGE', 'manage', ''),
(188, 1, 1, 'ucp_main', 'ucp', 178, 2, 3, 'UCP_MAIN_FRONT', 'front', ''),
(189, 1, 1, 'ucp_main', 'ucp', 178, 4, 5, 'UCP_MAIN_SUBSCRIBED', 'subscribed', ''),
(190, 1, 1, 'ucp_main', 'ucp', 178, 6, 7, 'UCP_MAIN_BOOKMARKS', 'bookmarks', 'cfg_allow_bookmarks'),
(191, 1, 1, 'ucp_main', 'ucp', 178, 8, 9, 'UCP_MAIN_DRAFTS', 'drafts', ''),
(192, 1, 1, 'ucp_notifications', 'ucp', 180, 36, 37, 'UCP_NOTIFICATION_OPTIONS', 'notification_options', ''),
(193, 1, 1, 'ucp_notifications', 'ucp', 178, 12, 13, 'UCP_NOTIFICATION_LIST', 'notification_list', 'cfg_allow_board_notifications'),
(194, 1, 0, 'ucp_pm', 'ucp', 181, 40, 41, 'UCP_PM_VIEW', 'view', 'cfg_allow_privmsg'),
(195, 1, 1, 'ucp_pm', 'ucp', 181, 42, 43, 'UCP_PM_COMPOSE', 'compose', 'cfg_allow_privmsg'),
(196, 1, 1, 'ucp_pm', 'ucp', 181, 44, 45, 'UCP_PM_DRAFTS', 'drafts', 'cfg_allow_privmsg'),
(197, 1, 1, 'ucp_pm', 'ucp', 181, 46, 47, 'UCP_PM_OPTIONS', 'options', 'cfg_allow_privmsg'),
(198, 1, 1, 'ucp_prefs', 'ucp', 180, 30, 31, 'UCP_PREFS_PERSONAL', 'personal', ''),
(199, 1, 1, 'ucp_prefs', 'ucp', 180, 32, 33, 'UCP_PREFS_POST', 'post', ''),
(200, 1, 1, 'ucp_prefs', 'ucp', 180, 34, 35, 'UCP_PREFS_VIEW', 'view', ''),
(201, 1, 1, 'ucp_profile', 'ucp', 179, 16, 17, 'UCP_PROFILE_PROFILE_INFO', 'profile_info', 'acl_u_chgprofileinfo'),
(202, 1, 1, 'ucp_profile', 'ucp', 179, 18, 19, 'UCP_PROFILE_SIGNATURE', 'signature', 'acl_u_sig'),
(203, 1, 1, 'ucp_profile', 'ucp', 179, 20, 21, 'UCP_PROFILE_AVATAR', 'avatar', 'cfg_allow_avatar'),
(204, 1, 1, 'ucp_profile', 'ucp', 179, 22, 23, 'UCP_PROFILE_REG_DETAILS', 'reg_details', ''),
(205, 1, 1, 'ucp_profile', 'ucp', 179, 24, 25, 'UCP_PROFILE_AUTOLOGIN_KEYS', 'autologin_keys', ''),
(206, 1, 1, 'ucp_zebra', 'ucp', 183, 56, 57, 'UCP_ZEBRA_FRIENDS', 'friends', ''),
(207, 1, 1, 'ucp_zebra', 'ucp', 183, 58, 59, 'UCP_ZEBRA_FOES', 'foes', ''),
(208, 1, 1, '', 'acp', 9, 104, 109, 'ACP_PHPBB_MEDIA_EMBED', '', ''),
(209, 1, 1, '\\phpbb\\mediaembed\\acp\\main_module', 'acp', 208, 105, 106, 'ACP_PHPBB_MEDIA_EMBED_SETTINGS', 'settings', 'ext_phpbb/mediaembed && acl_a_bbcode'),
(210, 1, 1, '\\phpbb\\mediaembed\\acp\\main_module', 'acp', 208, 107, 108, 'ACP_PHPBB_MEDIA_EMBED_MANAGE', 'manage', 'ext_phpbb/mediaembed && acl_a_bbcode'),
(211, 1, 1, '', 'acp', 32, 292, 295, 'ACP_BH_TITLE', '', ''),
(212, 1, 1, '\\phpbbmodders\\banhammer\\acp\\banhammer_module', 'acp', 211, 293, 294, 'ACP_BH_SETTINGS', 'settings', 'ext_phpbbmodders/banhammer && acl_a_user'),
(213, 1, 1, '', 'acp', 32, 296, 301, 'ADM_LINKED_ACCOUNTS', '', ''),
(214, 1, 1, '\\flerex\\linkedaccounts\\acp\\main_module', 'acp', 213, 297, 298, 'ADM_LINKED_ACCOUNTS_OVERVIEW', 'overview', 'ext_flerex/linkedaccounts && acl_a_link_accounts'),
(215, 1, 1, '\\flerex\\linkedaccounts\\acp\\main_module', 'acp', 213, 299, 300, 'ADM_LINKED_ACCOUNTS_MANAGEMENT', 'management', 'ext_flerex/linkedaccounts && acl_a_link_accounts'),
(216, 1, 1, '', 'ucp', 0, 61, 66, 'LINKED_ACCOUNTS', '', ''),
(217, 1, 1, '\\flerex\\linkedaccounts\\ucp\\main_module', 'ucp', 216, 62, 63, 'LINKED_ACCOUNTS_MANAGEMENT', 'management', 'ext_flerex/linkedaccounts && acl_u_link_accounts'),
(218, 1, 1, '\\flerex\\linkedaccounts\\ucp\\main_module', 'ucp', 216, 64, 65, 'LINKING_ACCOUNT', 'link', 'ext_flerex/linkedaccounts && acl_u_link_accounts'),
(219, 1, 1, '', 'acp', 32, 302, 305, 'ACP_USER_RECENT_ACTIVITY_TITLE', '', ''),
(220, 1, 1, '\\senky\\userrecentactivity\\acp\\main_module', 'acp', 219, 303, 304, 'ACP_SETTINGS', 'settings', 'ext_senky/userrecentactivity && acl_a_board');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_notifications`
--

DROP TABLE IF EXISTS `phpbb_notifications`;
CREATE TABLE IF NOT EXISTS `phpbb_notifications` (
  `notification_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `notification_type_id` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `item_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `item_parent_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `notification_read` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `notification_time` int(11) UNSIGNED NOT NULL DEFAULT 1,
  `notification_data` text COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`notification_id`),
  KEY `item_ident` (`notification_type_id`,`item_id`),
  KEY `user` (`user_id`,`notification_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_notification_types`
--

DROP TABLE IF EXISTS `phpbb_notification_types`;
CREATE TABLE IF NOT EXISTS `phpbb_notification_types` (
  `notification_type_id` smallint(4) UNSIGNED NOT NULL AUTO_INCREMENT,
  `notification_type_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `notification_type_enabled` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (`notification_type_id`),
  UNIQUE KEY `type` (`notification_type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_notification_types`
--

INSERT INTO `phpbb_notification_types` (`notification_type_id`, `notification_type_name`, `notification_type_enabled`) VALUES
(1, 'notification.type.topic', 1),
(2, 'notification.type.approve_topic', 1),
(3, 'notification.type.quote', 1),
(4, 'notification.type.bookmark', 1),
(5, 'notification.type.post', 1),
(6, 'notification.type.approve_post', 1),
(7, 'notification.type.admin_activate_user', 1),
(8, 'notification.type.group_request', 1),
(9, 'notification.type.topic_in_queue', 1),
(10, 'notification.type.post_in_queue', 1),
(11, 'notification.type.report_post', 1);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_oauth_accounts`
--

DROP TABLE IF EXISTS `phpbb_oauth_accounts`;
CREATE TABLE IF NOT EXISTS `phpbb_oauth_accounts` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `provider` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `oauth_provider_id` text COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`user_id`,`provider`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_oauth_states`
--

DROP TABLE IF EXISTS `phpbb_oauth_states`;
CREATE TABLE IF NOT EXISTS `phpbb_oauth_states` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `session_id` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `provider` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `oauth_state` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  KEY `user_id` (`user_id`),
  KEY `provider` (`provider`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_oauth_tokens`
--

DROP TABLE IF EXISTS `phpbb_oauth_tokens`;
CREATE TABLE IF NOT EXISTS `phpbb_oauth_tokens` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `session_id` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `provider` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `oauth_token` mediumtext COLLATE utf8_bin NOT NULL,
  KEY `user_id` (`user_id`),
  KEY `provider` (`provider`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_poll_options`
--

DROP TABLE IF EXISTS `phpbb_poll_options`;
CREATE TABLE IF NOT EXISTS `phpbb_poll_options` (
  `poll_option_id` tinyint(4) NOT NULL DEFAULT 0,
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `poll_option_text` text COLLATE utf8_bin NOT NULL,
  `poll_option_total` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  KEY `poll_opt_id` (`poll_option_id`),
  KEY `topic_id` (`topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_poll_votes`
--

DROP TABLE IF EXISTS `phpbb_poll_votes`;
CREATE TABLE IF NOT EXISTS `phpbb_poll_votes` (
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `poll_option_id` tinyint(4) NOT NULL DEFAULT 0,
  `vote_user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `vote_user_ip` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  KEY `topic_id` (`topic_id`),
  KEY `vote_user_id` (`vote_user_id`),
  KEY `vote_user_ip` (`vote_user_ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_posts`
--

DROP TABLE IF EXISTS `phpbb_posts`;
CREATE TABLE IF NOT EXISTS `phpbb_posts` (
  `post_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `poster_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `icon_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `poster_ip` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `post_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `post_reported` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `enable_bbcode` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `enable_smilies` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `enable_magic_url` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `enable_sig` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `post_username` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `post_subject` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `post_text` mediumtext CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `post_checksum` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `post_attachment` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `bbcode_bitfield` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `bbcode_uid` varchar(8) COLLATE utf8_bin NOT NULL DEFAULT '',
  `post_postcount` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `post_edit_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `post_edit_reason` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `post_edit_user` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `post_edit_count` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `post_edit_locked` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `post_visibility` tinyint(3) NOT NULL DEFAULT 0,
  `post_delete_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `post_delete_reason` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `post_delete_user` int(10) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`post_id`),
  KEY `forum_id` (`forum_id`),
  KEY `topic_id` (`topic_id`),
  KEY `poster_ip` (`poster_ip`),
  KEY `poster_id` (`poster_id`),
  KEY `tid_post_time` (`topic_id`,`post_time`),
  KEY `post_username` (`post_username`),
  KEY `post_visibility` (`post_visibility`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_privmsgs`
--

DROP TABLE IF EXISTS `phpbb_privmsgs`;
CREATE TABLE IF NOT EXISTS `phpbb_privmsgs` (
  `msg_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `root_level` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `author_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `icon_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `author_ip` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `message_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `enable_bbcode` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `enable_smilies` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `enable_magic_url` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `enable_sig` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `message_subject` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `message_text` mediumtext COLLATE utf8_bin NOT NULL,
  `message_edit_reason` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `message_edit_user` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `message_attachment` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `bbcode_bitfield` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `bbcode_uid` varchar(8) COLLATE utf8_bin NOT NULL DEFAULT '',
  `message_edit_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `message_edit_count` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `to_address` text COLLATE utf8_bin NOT NULL,
  `bcc_address` text COLLATE utf8_bin NOT NULL,
  `message_reported` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`msg_id`),
  KEY `author_ip` (`author_ip`),
  KEY `message_time` (`message_time`),
  KEY `author_id` (`author_id`),
  KEY `root_level` (`root_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_privmsgs_folder`
--

DROP TABLE IF EXISTS `phpbb_privmsgs_folder`;
CREATE TABLE IF NOT EXISTS `phpbb_privmsgs_folder` (
  `folder_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `folder_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `pm_count` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`folder_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_privmsgs_rules`
--

DROP TABLE IF EXISTS `phpbb_privmsgs_rules`;
CREATE TABLE IF NOT EXISTS `phpbb_privmsgs_rules` (
  `rule_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `rule_check` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `rule_connection` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `rule_string` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `rule_user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `rule_group_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `rule_action` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `rule_folder_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`rule_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_privmsgs_to`
--

DROP TABLE IF EXISTS `phpbb_privmsgs_to`;
CREATE TABLE IF NOT EXISTS `phpbb_privmsgs_to` (
  `msg_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `author_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `pm_deleted` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `pm_new` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `pm_unread` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `pm_replied` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `pm_marked` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `pm_forwarded` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `folder_id` int(11) NOT NULL DEFAULT 0,
  KEY `msg_id` (`msg_id`),
  KEY `author_id` (`author_id`),
  KEY `usr_flder_id` (`user_id`,`folder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_profile_fields`
--

DROP TABLE IF EXISTS `phpbb_profile_fields`;
CREATE TABLE IF NOT EXISTS `phpbb_profile_fields` (
  `field_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `field_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `field_type` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '',
  `field_ident` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `field_length` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `field_minlen` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `field_maxlen` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `field_novalue` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `field_default_value` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `field_validation` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `field_required` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `field_show_on_reg` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `field_hide` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `field_no_view` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `field_active` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `field_order` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `field_show_profile` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `field_show_on_vt` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `field_show_novalue` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `field_show_on_pm` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `field_show_on_ml` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `field_is_contact` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `field_contact_desc` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `field_contact_url` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`field_id`),
  KEY `fld_type` (`field_type`),
  KEY `fld_ordr` (`field_order`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_profile_fields`
--

INSERT INTO `phpbb_profile_fields` (`field_id`, `field_name`, `field_type`, `field_ident`, `field_length`, `field_minlen`, `field_maxlen`, `field_novalue`, `field_default_value`, `field_validation`, `field_required`, `field_show_on_reg`, `field_hide`, `field_no_view`, `field_active`, `field_order`, `field_show_profile`, `field_show_on_vt`, `field_show_novalue`, `field_show_on_pm`, `field_show_on_ml`, `field_is_contact`, `field_contact_desc`, `field_contact_url`) VALUES
(1, 'phpbb_location', 'profilefields.type.string', 'phpbb_location', '20', '2', '100', '', '', '.*', 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, '', ''),
(2, 'phpbb_website', 'profilefields.type.url', 'phpbb_website', '40', '12', '255', '', '', '', 0, 0, 0, 0, 1, 2, 1, 1, 0, 1, 1, 1, 'VISIT_WEBSITE', '%s'),
(10, 'phpbb_skype', 'profilefields.type.string', 'phpbb_skype', '20', '6', '32', '', '', '[a-zA-Z][\\w\\.,\\-_]+', 0, 0, 0, 0, 1, 3, 1, 1, 0, 1, 1, 1, 'VIEW_SKYPE_PROFILE', 'skype:%s?userinfo');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_profile_fields_data`
--

DROP TABLE IF EXISTS `phpbb_profile_fields_data`;
CREATE TABLE IF NOT EXISTS `phpbb_profile_fields_data` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `pf_phpbb_location` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `pf_phpbb_skype` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `pf_phpbb_website` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_profile_fields_data`
--

INSERT INTO `phpbb_profile_fields_data` (`user_id`, `pf_phpbb_location`, `pf_phpbb_skype`, `pf_phpbb_website`) VALUES
(2, '', '', '');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_profile_fields_lang`
--

DROP TABLE IF EXISTS `phpbb_profile_fields_lang`;
CREATE TABLE IF NOT EXISTS `phpbb_profile_fields_lang` (
  `field_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `lang_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `option_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `field_type` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '',
  `lang_value` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`field_id`,`lang_id`,`option_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_profile_lang`
--

DROP TABLE IF EXISTS `phpbb_profile_lang`;
CREATE TABLE IF NOT EXISTS `phpbb_profile_lang` (
  `field_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `lang_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `lang_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `lang_explain` text COLLATE utf8_bin NOT NULL,
  `lang_default_value` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`field_id`,`lang_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_profile_lang`
--

INSERT INTO `phpbb_profile_lang` (`field_id`, `lang_id`, `lang_name`, `lang_explain`, `lang_default_value`) VALUES
(1, 1, 'LOCATION', '', ''),
(2, 1, 'WEBSITE', '', ''),
(10, 1, 'SKYPE', '', '');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_qa_confirm`
--

DROP TABLE IF EXISTS `phpbb_qa_confirm`;
CREATE TABLE IF NOT EXISTS `phpbb_qa_confirm` (
  `session_id` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `confirm_id` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `lang_iso` varchar(30) COLLATE utf8_bin NOT NULL DEFAULT '',
  `question_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `attempts` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `confirm_type` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`confirm_id`),
  KEY `session_id` (`session_id`),
  KEY `lookup` (`confirm_id`,`session_id`,`lang_iso`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_ranks`
--

DROP TABLE IF EXISTS `phpbb_ranks`;
CREATE TABLE IF NOT EXISTS `phpbb_ranks` (
  `rank_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `rank_title` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `rank_min` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `rank_special` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `rank_image` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`rank_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_ranks`
--

INSERT INTO `phpbb_ranks` (`rank_id`, `rank_title`, `rank_min`, `rank_special`, `rank_image`) VALUES
(1, 'Site Admin', 0, 1, '');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_reports`
--

DROP TABLE IF EXISTS `phpbb_reports`;
CREATE TABLE IF NOT EXISTS `phpbb_reports` (
  `report_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `reason_id` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `post_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `user_notify` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `report_closed` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `report_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `report_text` mediumtext COLLATE utf8_bin NOT NULL,
  `pm_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `reported_post_enable_bbcode` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `reported_post_enable_smilies` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `reported_post_enable_magic_url` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `reported_post_text` mediumtext COLLATE utf8_bin NOT NULL,
  `reported_post_uid` varchar(8) COLLATE utf8_bin NOT NULL DEFAULT '',
  `reported_post_bitfield` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`report_id`),
  KEY `post_id` (`post_id`),
  KEY `pm_id` (`pm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_reports_reasons`
--

DROP TABLE IF EXISTS `phpbb_reports_reasons`;
CREATE TABLE IF NOT EXISTS `phpbb_reports_reasons` (
  `reason_id` smallint(4) UNSIGNED NOT NULL AUTO_INCREMENT,
  `reason_title` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `reason_description` mediumtext COLLATE utf8_bin NOT NULL,
  `reason_order` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`reason_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_reports_reasons`
--

INSERT INTO `phpbb_reports_reasons` (`reason_id`, `reason_title`, `reason_description`, `reason_order`) VALUES
(2, 'spam', 'The reported post has the only purpose to advertise for a website or another product.', 1),
(3, 'off_topic', 'The reported post is off topic.', 2),
(4, 'other', 'The reported post does not fit into any other category, please use the further information field.', 3);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_search_results`
--

DROP TABLE IF EXISTS `phpbb_search_results`;
CREATE TABLE IF NOT EXISTS `phpbb_search_results` (
  `search_key` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `search_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `search_keywords` mediumtext COLLATE utf8_bin NOT NULL,
  `search_authors` mediumtext COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`search_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_search_wordlist`
--

DROP TABLE IF EXISTS `phpbb_search_wordlist`;
CREATE TABLE IF NOT EXISTS `phpbb_search_wordlist` (
  `word_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `word_text` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `word_common` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `word_count` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`word_id`),
  UNIQUE KEY `wrd_txt` (`word_text`),
  KEY `wrd_cnt` (`word_count`)
) ENGINE=InnoDB AUTO_INCREMENT=553 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_search_wordlist`
--

INSERT INTO `phpbb_search_wordlist` (`word_id`, `word_text`, `word_common`, `word_count`) VALUES
(1, 'this', 0, 0),
(2, 'is', 0, 0),
(3, 'an', 0, 0),
(4, 'example', 0, 0),
(5, 'post', 0, 0),
(6, 'in', 0, 0),
(7, 'your', 0, 0),
(8, 'phpbb3', 0, 0),
(9, 'installation', 0, 0),
(10, 'everything', 0, 0),
(11, 'seems', 0, 0),
(12, 'to', 0, 0),
(13, 'be', 0, 0),
(14, 'working', 0, 0),
(15, 'you', 0, 0),
(16, 'may', 0, 0),
(17, 'delete', 0, 0),
(18, 'if', 0, 0),
(19, 'like', 0, 0),
(20, 'and', 0, 0),
(21, 'continue', 0, 0),
(22, 'set', 0, 0),
(23, 'up', 0, 0),
(24, 'board', 0, 0),
(25, 'during', 0, 0),
(26, 'the', 0, 0),
(27, 'process', 0, 0),
(28, 'first', 0, 0),
(29, 'category', 0, 0),
(30, 'forum', 0, 0),
(31, 'are', 0, 0),
(32, 'assigned', 0, 0),
(33, 'appropriate', 0, 0),
(34, 'of', 0, 0),
(35, 'permissions', 0, 0),
(36, 'for', 0, 0),
(37, 'predefined', 0, 0),
(38, 'usergroups', 0, 0),
(39, 'administrators', 0, 0),
(40, 'bots', 0, 0),
(41, 'global', 0, 0),
(42, 'moderators', 0, 0),
(43, 'guests', 0, 0),
(44, 'registered', 0, 0),
(45, 'users', 0, 0),
(46, 'coppa', 0, 0),
(47, 'also', 0, 0),
(48, 'choose', 0, 0),
(49, 'do', 0, 0),
(50, 'not', 0, 0),
(51, 'forget', 0, 0),
(52, 'assign', 0, 0),
(53, 'all', 0, 0),
(54, 'these', 0, 0),
(55, 'new', 0, 0),
(56, 'categories', 0, 0),
(57, 'forums', 0, 0),
(58, 'create', 0, 0),
(59, 'it', 0, 0),
(60, 'recommended', 0, 0),
(61, 'rename', 0, 0),
(62, 'copy', 0, 0),
(63, 'from', 0, 0),
(64, 'while', 0, 0),
(65, 'creating', 0, 0),
(66, 'have', 0, 0),
(67, 'fun', 0, 0),
(68, 'welcome', 0, 0),
(69, 'export', 0, 0),
(70, 'database', 0, 0),
(71, 'folder', 0, 0),
(72, 'current', 0, 0),
(73, 'changes', 0, 0),
(74, 'world', 0, 0),
(75, 'definitions', 0, 0),
(76, 'that', 0, 0),
(77, 'decide', 0, 0),
(78, 'f2p', 0, 0),
(79, 'p2p', 0, 0),
(80, 'need', 0, 0),
(81, 'implement', 0, 0),
(82, 'disabling', 0, 0),
(83, 'element', 0, 0),
(84, 'later', 0, 0),
(85, 'additional', 0, 0),
(86, 'types', 0, 0),
(87, 'different', 0, 0),
(88, 'rules', 0, 0),
(89, 'deathmatch', 0, 0),
(90, 'anywhere', 0, 0),
(91, 'before', 0, 0),
(92, 'wildy', 0, 0),
(93, 'fix', 0, 0),
(94, 'sound', 0, 0),
(95, 'client', 0, 0),
(96, 'jre9', 0, 0),
(97, 'doesn', 0, 0),
(98, 'include', 0, 0),
(99, 'audio', 0, 0),
(100, 'player', 0, 0),
(101, 'might', 0, 0),
(102, 'package', 0, 0),
(103, 'library', 0, 0),
(104, 'with', 0, 0),
(105, 'update', 0, 0),
(106, 'landscape', 0, 0),
(107, 'latest', 0, 0),
(108, 'reimplement', 0, 0),
(109, 'rsce', 0, 0),
(110, 'jail', 0, 0),
(111, 'dialog', 0, 0),
(112, 'requests', 0, 0),
(113, 'casting', 0, 0),
(114, 'god', 0, 0),
(115, 'spell', 0, 0),
(116, 'completion', 0, 0),
(117, 'mage', 0, 0),
(118, 'arena', 0, 0),
(119, 'outside', 0, 0),
(120, 'message', 0, 0),
(121, 'using', 0, 0),
(122, 'disk', 0, 0),
(123, 'returning', 0, 0),
(124, 'quest', 0, 0),
(125, 'replay', 0, 0),
(126, 'gnome', 0, 0),
(127, 'ball', 0, 0),
(128, 'fishing', 0, 0),
(129, 'trawler', 0, 0),
(130, 'gertrude', 0, 0),
(131, 'cat', 0, 0),
(132, 'murder', 0, 0),
(133, 'mystery', 0, 0),
(134, 'digsite', 0, 0),
(135, 'grand', 0, 0),
(136, 'tree', 0, 0),
(137, 'watchtower', 0, 0),
(138, 'shilo', 0, 0),
(139, 'village', 0, 0),
(140, 'underground', 0, 0),
(141, 'pass', 0, 0),
(142, 'observatory', 0, 0),
(143, 'sea', 0, 0),
(144, 'slug', 0, 0),
(145, 'waterfall', 0, 0),
(146, 'temple', 0, 0),
(147, 'ikov', 0, 0),
(148, 'tribal', 0, 0),
(149, 'totem', 0, 0),
(150, 'family', 0, 0),
(151, 'crest', 0, 0),
(152, 'hazel', 0, 0),
(153, 'cult', 0, 0),
(154, 'holy', 0, 0),
(155, 'grail', 0, 0),
(156, 'scorpion', 0, 0),
(157, 'catcher', 0, 0),
(158, 'dwarf', 0, 0),
(159, 'canon', 0, 0),
(160, 'tourist', 0, 0),
(161, 'trap', 0, 0),
(162, 'fight', 0, 0),
(163, 'witch', 0, 0),
(164, 'house', 0, 0),
(165, 'misc', 0, 0),
(166, 'when', 0, 0),
(167, 'exactly', 0, 0),
(168, 'charge', 0, 0),
(169, 'acquired', 0, 0),
(170, 'miniquest', 0, 0),
(171, 'game', 0, 0),
(172, 'remove', 0, 0),
(173, 'event', 0, 0),
(174, 'staff', 0, 0),
(175, 'type', 0, 0),
(176, 'evaluate', 0, 0),
(177, 'existing', 0, 0),
(178, 'events', 0, 0),
(179, 'wilderness', 0, 0),
(180, 'always', 0, 0),
(181, 'configure', 0, 0),
(182, 'cycle', 0, 0),
(183, 'command', 0, 0),
(184, 'box', 0, 0),
(185, 'has', 0, 0),
(186, 'incorrectly', 0, 0),
(187, 'rendered', 0, 0),
(188, 'isn', 0, 0),
(189, 'rendering', 0, 0),
(190, 'newline', 0, 0),
(191, 'teleport', 0, 0),
(192, 'pools', 0, 0),
(193, 'ensure', 0, 0),
(194, 'thrander', 0, 0),
(195, 'messages', 0, 0),
(196, 'right', 0, 0),
(197, 'thesallia', 0, 0),
(198, 'regives', 0, 0),
(199, 'scythe', 0, 0),
(200, 'bunny', 0, 0),
(201, 'ears', 0, 0),
(202, 'option', 0, 0),
(203, 'thessalia', 0, 0),
(204, 'lost', 0, 0),
(205, 'can', 0, 0),
(206, 'get', 0, 0),
(207, 'some', 0, 0),
(208, 'more', 0, 0),
(209, 'please', 0, 0),
(210, 'another', 0, 0),
(211, 'what', 0, 0),
(212, 'got', 0, 0),
(213, 'thank', 0, 0),
(214, 'regaining', 0, 0),
(215, 'ohh', 0, 0),
(216, 'poor', 0, 0),
(217, 'dear', 0, 0),
(218, 'here', 0, 0),
(219, 'white', 0, 0),
(220, 'gives', 0, 0),
(221, 'dlong', 0, 0),
(222, 'wear', 0, 0),
(223, 'earned', 0, 0),
(224, 'yet', 0, 0),
(225, 'complete', 0, 0),
(226, 'city', 0, 0),
(227, 'zenaris', 0, 0),
(228, 'attack', 0, 0),
(229, 'level', 0, 0),
(230, 'daxe', 0, 0),
(231, 'hero', 0, 0),
(232, 'guild', 0, 0),
(233, 'entry', 0, 0),
(234, 'dsq', 0, 0),
(235, 'legend', 0, 0),
(236, 'defense', 0, 0),
(237, 'picking', 0, 0),
(238, 'second', 0, 0),
(239, 'pair', 0, 0),
(240, 'don', 0, 0),
(241, 'only', 0, 0),
(242, 'one', 0, 0),
(243, 'head', 0, 0),
(244, 'already', 0, 0),
(245, 'staking', 0, 0),
(246, 'trading', 0, 0),
(247, 'non', 0, 0),
(248, 'tradeables', 0, 0),
(249, 'object', 0, 0),
(250, 'cannot', 0, 0),
(251, 'traded', 0, 0),
(252, 'other', 0, 0),
(253, 'players', 0, 0),
(254, 'added', 0, 0),
(255, 'duel', 0, 0),
(256, 'offer', 0, 0),
(257, 'telekenetic', 0, 0),
(258, 'grabbable', 0, 0),
(259, 'items', 0, 0),
(260, 'use', 0, 0),
(261, 'telekeneic', 0, 0),
(262, 'grab', 0, 0),
(263, 'selling', 0, 0),
(264, 'shop', 0, 0),
(265, 'sold', 0, 0),
(266, 'shops', 0, 0),
(267, 'tradeable', 0, 0),
(268, 'sell', 0, 0),
(269, 'show', 0, 0),
(270, 'trade', 0, 0),
(271, 'stake', 0, 0),
(272, 'should', 0, 0),
(273, 'able', 0, 0),
(274, 'drop', 0, 0),
(275, 'but', 0, 0),
(276, 'see', 0, 0),
(277, 'drophandler', 0, 0),
(278, 'java', 0, 0),
(279, 'txt', 0, 0),
(280, 'item', 0, 0),
(281, 'list', 0, 0),
(282, 'dropping', 0, 0),
(283, 'visible', 0, 0),
(284, 'who', 0, 0),
(285, 'dropped', 0, 0),
(286, 'seconds', 0, 0),
(287, 'field', 0, 0),
(288, 'def', 0, 0),
(289, 'table', 0, 0),
(290, 'load', 0, 0),
(291, 'into', 0, 0),
(292, 'dat', 0, 0),
(293, 'through', 0, 0),
(294, 'itemexporter', 0, 0),
(295, 'tradehandler', 0, 0),
(296, 'duelhandler', 0, 0),
(297, 'auctionhouse', 0, 0),
(298, 'drinking', 0, 0),
(299, 'half', 0, 0),
(300, 'wine', 0, 0),
(301, 'same', 0, 0),
(302, 'normal', 0, 0),
(303, 'needs', 0, 0),
(304, 'implemented', 0, 0),
(305, 'eating', 0, 0),
(306, 'pumpkin', 0, 0),
(307, 'easter', 0, 0),
(308, 'egg', 0, 0),
(309, 'christmas', 0, 0),
(310, 'cracker', 0, 0),
(311, 'pull', 0, 0),
(312, 'above', 0, 0),
(313, 'puller', 0, 0),
(314, 'prize', 0, 0),
(315, 'party', 0, 0),
(316, 'hat', 0, 0),
(317, 'silver', 0, 0),
(318, 'ore', 0, 0),
(319, 'side', 0, 0),
(320, 'gets', 0, 0),
(321, 'random', 0, 0),
(322, 'person', 0, 0),
(323, 'unimplemented', 0, 0),
(324, 'quests', 0, 0),
(325, 'miniquests', 0, 0),
(326, 'minigames', 0, 0),
(327, 'portal', 0, 0),
(328, 'exact', 0, 0),
(329, 'goto', 0, 0),
(330, 'location', 0, 0),
(331, 'objectaction', 0, 0),
(332, 'handlewalkthrough', 0, 0),
(333, 'add', 0, 0),
(334, 'cast', 0, 0),
(335, 'spells', 0, 0),
(336, 'without', 0, 0),
(337, 'cape', 0, 0),
(338, 'special', 0, 0),
(339, 'setting', 0, 0),
(340, 'near', 0, 0),
(341, 'engineer', 0, 0),
(342, '78x95', 0, 0),
(343, 'tiles', 0, 0),
(344, 'permitted', 0, 0),
(345, 'cannon', 0, 0),
(346, 'close', 0, 0),
(347, 'black', 0, 0),
(348, 'guard', 0, 0),
(349, 'references', 0, 0),
(350, 'rscu', 0, 0),
(351, 'rscunity', 0, 0),
(352, 'rsc', 0, 0),
(353, 'unity', 0, 0),
(354, 'commandhandler', 0, 0),
(355, 'customize', 0, 0),
(356, 'commands', 0, 0),
(357, 'mudclient', 0, 0),
(358, 'drawcommandswindow', 0, 0),
(359, 'syntax', 0, 0),
(360, 'enough', 0, 0),
(361, 'arguments', 0, 0),
(362, 'supplied', 0, 0),
(363, 'instead', 0, 0),
(364, 'silent', 0, 0),
(365, 'fail', 0, 0),
(366, 'character', 0, 0),
(367, 'edit', 0, 0),
(368, 'design', 0, 0),
(369, 'menu', 0, 0),
(370, 'change', 0, 0),
(371, 'appearance', 0, 0),
(372, 'examine', 0, 0),
(373, 'they', 0, 0),
(374, 'work', 0, 0),
(375, 'how', 0, 0),
(376, 'want', 0, 0),
(377, 'modify', 0, 0),
(378, 'needed', 0, 0),
(379, 'stuck', 0, 0),
(380, 'tutorial', 0, 0),
(381, 'island', 0, 0),
(382, 'skip', 0, 0),
(383, 'teleports', 0, 0),
(384, 'lumbridge', 0, 0),
(385, 'free', 0, 0),
(386, 'refreshwoodcut', 0, 0),
(387, 'fixed', 0, 0),
(388, 'invulnerable', 0, 0),
(389, 'accepts', 0, 0),
(390, 'user', 0, 0),
(391, 'then', 0, 0),
(392, 'mod', 0, 0),
(393, 'refresh', 0, 0),
(394, 'defs', 0, 0),
(395, 'relaod', 0, 0),
(396, 'whole', 0, 0),
(397, 'low', 0, 0),
(398, 'pri', 0, 0),
(399, 'render', 0, 0),
(400, 'differently', 0, 0),
(401, 'targets', 0, 0),
(402, 'names', 0, 0),
(403, 'password', 0, 0),
(404, 'does', 0, 0),
(405, 'replace', 0, 0),
(406, 'dataoperations', 0, 0),
(407, 'function', 0, 0),
(408, 'addcharacters', 0, 0),
(409, 'completely', 0, 0),
(410, 'runecrafting', 0, 0),
(411, 'altar', 0, 0),
(412, 'code', 0, 0),
(413, 'associated', 0, 0),
(414, 'thereof', 0, 0),
(415, 'subs', 0, 0),
(416, 'higher', 0, 0),
(417, 'exp', 0, 0),
(418, 'rate', 0, 0),
(419, 'config', 0, 0),
(420, 'xml', 0, 0),
(421, 'increasexp', 0, 0),
(422, 'subscribers', 0, 0),
(423, 'sleep', 0, 0),
(424, 'faster', 0, 0),
(425, 'temporaryfatiguethrottle', 0, 0),
(426, 'fatigue', 0, 0),
(427, 'slower', 0, 0),
(428, 'pool', 0, 0),
(429, 'portals', 0, 0),
(430, 'often', 0, 0),
(431, 'canwarp', 0, 0),
(432, 'auction', 0, 0),
(433, 'slots', 0, 0),
(434, 'auctionhousehandler', 0, 0),
(435, 'double', 0, 0),
(436, 'drops', 0, 0),
(437, 'mods', 0, 0),
(438, 'npc', 0, 0),
(439, 'killedby', 0, 0),
(440, 'craft', 0, 0),
(441, 'runes', 0, 0),
(442, 'craftrunes', 0, 0),
(443, 'mining', 0, 0),
(444, 'handlemining', 0, 0),
(445, 'woodcutting', 0, 0),
(446, 'handlewoodcutting', 0, 0),
(447, 'handlefishing', 0, 0),
(448, 'removed', 0, 0),
(449, 'resend', 0, 0),
(450, 'history', 0, 0),
(451, 'resize', 0, 0),
(452, 'window', 0, 0),
(453, 'jframedelegate', 0, 0),
(454, 'onlogin', 0, 0),
(455, 'report', 0, 0),
(456, 'reportabusehandler', 0, 0),
(457, 'handlepacket', 0, 0),
(458, 'glider', 0, 0),
(459, 'hey', 0, 0),
(460, 'marwolf', 0, 0),
(461, 'began', 0, 0),
(462, 'playing', 0, 0),
(463, 'spring', 0, 0),
(464, '2002', 0, 0),
(465, 'believe', 0, 0),
(466, 'its', 0, 0),
(467, 'almost', 0, 0),
(468, 'been', 0, 0),
(469, 'two', 0, 0),
(470, 'decades', 0, 0),
(471, 'since', 0, 0),
(472, 'joined', 0, 0),
(473, 'fledgling', 0, 0),
(474, 'clan', 0, 0),
(475, 'named', 0, 0),
(476, 'dragon', 0, 0),
(477, 'annihilators', 0, 0),
(478, 'our', 0, 0),
(479, 'leader', 0, 0),
(480, 'kinoobing', 0, 0),
(481, 'went', 0, 0),
(482, 'missing', 0, 0),
(483, 'weeks', 0, 0),
(484, 'kept', 0, 0),
(485, 'recruiting', 0, 0),
(486, 'members', 0, 0),
(487, 'despite', 0, 0),
(488, 'gained', 0, 0),
(489, 'their', 0, 0),
(490, 'support', 0, 0),
(491, 'held', 0, 0),
(492, 'election', 0, 0),
(493, 'was', 0, 0),
(494, 'voted', 0, 0),
(495, 'avidgamers', 0, 0),
(496, 'com', 0, 0),
(497, 'site', 0, 0),
(498, 'introduce', 0, 0),
(499, 'yourself', 0, 0),
(500, 'made', 0, 0),
(501, 'simple', 0, 0),
(502, 'website', 0, 0),
(503, 'angelfire', 0, 0),
(504, 'typing', 0, 0),
(505, 'comes', 0, 0),
(506, 'old', 0, 0),
(507, 'noob', 0, 0),
(508, 'shame', 0, 0),
(509, 'put', 0, 0),
(510, 'together', 0, 0),
(511, 'disbanded', 0, 0),
(512, '2006', 0, 0),
(513, 'img', 0, 0),
(514, 'https', 0, 0),
(515, 'imgur', 0, 0),
(516, '2dg676a', 0, 0),
(517, 'jpg', 0, 0),
(518, 'z7wbevz', 0, 0),
(519, 'kayuycb', 0, 0),
(520, 'nunhma1', 0, 0),
(521, 'q18rina', 0, 0),
(522, 'pck7iwq', 0, 0),
(523, '8ljjzxb', 0, 0),
(524, 'footer', 0, 0),
(525, 'iframe', 0, 0),
(526, 'per', 0, 0),
(527, 'instance', 0, 0),
(528, 'chat', 0, 0),
(529, 'autorefresh', 0, 0),
(530, 'scroll', 0, 0),
(531, 'down', 0, 0),
(532, 'disable', 0, 0),
(533, 'enabled', 0, 0),
(534, 'kenix3', 0, 0),
(535, 'registration', 0, 0),
(536, 'shadow', 0, 0),
(537, 'muting', 0, 0),
(538, 'autoexpire', 0, 0),
(539, 'after', 0, 0),
(540, 'mins', 0, 0),
(541, 'logging', 0, 0),
(542, 'review', 0, 0),
(543, 'admin', 0, 0),
(544, 'panel', 0, 0),
(545, 'display', 0, 0),
(546, 'mute', 0, 0),
(547, 'logs', 0, 0),
(548, 'last', 0, 0),
(549, 'lines', 0, 0),
(550, 'muted', 0, 0),
(551, 'expand', 0, 0),
(552, 'minutes', 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_search_wordmatch`
--

DROP TABLE IF EXISTS `phpbb_search_wordmatch`;
CREATE TABLE IF NOT EXISTS `phpbb_search_wordmatch` (
  `post_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `word_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `title_match` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  UNIQUE KEY `un_mtch` (`word_id`,`post_id`,`title_match`),
  KEY `word_id` (`word_id`),
  KEY `post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_sessions`
--

DROP TABLE IF EXISTS `phpbb_sessions`;
CREATE TABLE IF NOT EXISTS `phpbb_sessions` (
  `session_id` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `session_user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `session_last_visit` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `session_start` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `session_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `session_ip` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `session_browser` varchar(150) COLLATE utf8_bin NOT NULL DEFAULT '',
  `session_forwarded_for` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `session_page` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `session_viewonline` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `session_autologin` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `session_admin` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `session_forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`session_id`),
  KEY `session_time` (`session_time`),
  KEY `session_user_id` (`session_user_id`),
  KEY `session_fid` (`session_forum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_sessions`
--

INSERT INTO `phpbb_sessions` (`session_id`, `session_user_id`, `session_last_visit`, `session_start`, `session_time`, `session_ip`, `session_browser`, `session_forwarded_for`, `session_page`, `session_viewonline`, `session_autologin`, `session_admin`, `session_forum_id`) VALUES
('e3bb54f5bae8f37cc70f6ce30628b90a', 2, 1529772106, 1536194670, 1536198093, '192.168.240.1', 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:61.0) Gecko/20100101 Firefox/61.0', '', 'index.php', 1, 1, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_sessions_keys`
--

DROP TABLE IF EXISTS `phpbb_sessions_keys`;
CREATE TABLE IF NOT EXISTS `phpbb_sessions_keys` (
  `key_id` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `last_ip` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `last_login` int(11) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`key_id`,`user_id`),
  KEY `last_login` (`last_login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_sitelist`
--

DROP TABLE IF EXISTS `phpbb_sitelist`;
CREATE TABLE IF NOT EXISTS `phpbb_sitelist` (
  `site_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_ip` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `site_hostname` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `ip_exclude` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`site_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_sitelist`
--

INSERT INTO `phpbb_sitelist` (`site_id`, `site_ip`, `site_hostname`, `ip_exclude`) VALUES
(1, '', 'openrsc.com', 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_smilies`
--

DROP TABLE IF EXISTS `phpbb_smilies`;
CREATE TABLE IF NOT EXISTS `phpbb_smilies` (
  `smiley_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `emotion` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `smiley_url` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `smiley_width` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `smiley_height` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `smiley_order` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `display_on_posting` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (`smiley_id`),
  KEY `display_on_post` (`display_on_posting`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_smilies`
--

INSERT INTO `phpbb_smilies` (`smiley_id`, `code`, `emotion`, `smiley_url`, `smiley_width`, `smiley_height`, `smiley_order`, `display_on_posting`) VALUES
(1, ':D', 'Very Happy', 'icon_e_biggrin.gif', 15, 17, 1, 1),
(2, ':-D', 'Very Happy', 'icon_e_biggrin.gif', 15, 17, 2, 1),
(3, ':grin:', 'Very Happy', 'icon_e_biggrin.gif', 15, 17, 3, 1),
(4, ':)', 'Smile', 'icon_e_smile.gif', 15, 17, 4, 1),
(5, ':-)', 'Smile', 'icon_e_smile.gif', 15, 17, 5, 1),
(6, ':smile:', 'Smile', 'icon_e_smile.gif', 15, 17, 6, 1),
(7, ';)', 'Wink', 'icon_e_wink.gif', 15, 17, 7, 1),
(8, ';-)', 'Wink', 'icon_e_wink.gif', 15, 17, 8, 1),
(9, ':wink:', 'Wink', 'icon_e_wink.gif', 15, 17, 9, 1),
(10, ':(', 'Sad', 'icon_e_sad.gif', 15, 17, 10, 1),
(11, ':-(', 'Sad', 'icon_e_sad.gif', 15, 17, 11, 1),
(12, ':sad:', 'Sad', 'icon_e_sad.gif', 15, 17, 12, 1),
(13, ':o', 'Surprised', 'icon_e_surprised.gif', 15, 17, 13, 1),
(14, ':-o', 'Surprised', 'icon_e_surprised.gif', 15, 17, 14, 1),
(15, ':eek:', 'Surprised', 'icon_e_surprised.gif', 15, 17, 15, 1),
(16, ':shock:', 'Shocked', 'icon_eek.gif', 15, 17, 16, 1),
(17, ':?', 'Confused', 'icon_e_confused.gif', 15, 17, 17, 1),
(18, ':-?', 'Confused', 'icon_e_confused.gif', 15, 17, 18, 1),
(19, ':???:', 'Confused', 'icon_e_confused.gif', 15, 17, 19, 1),
(20, '8-)', 'Cool', 'icon_cool.gif', 15, 17, 20, 1),
(21, ':cool:', 'Cool', 'icon_cool.gif', 15, 17, 21, 1),
(22, ':lol:', 'Laughing', 'icon_lol.gif', 15, 17, 22, 1),
(23, ':x', 'Mad', 'icon_mad.gif', 15, 17, 23, 1),
(24, ':-x', 'Mad', 'icon_mad.gif', 15, 17, 24, 1),
(25, ':mad:', 'Mad', 'icon_mad.gif', 15, 17, 25, 1),
(26, ':P', 'Razz', 'icon_razz.gif', 15, 17, 26, 1),
(27, ':-P', 'Razz', 'icon_razz.gif', 15, 17, 27, 1),
(28, ':razz:', 'Razz', 'icon_razz.gif', 15, 17, 28, 1),
(29, ':oops:', 'Embarrassed', 'icon_redface.gif', 15, 17, 29, 1),
(30, ':cry:', 'Crying or Very Sad', 'icon_cry.gif', 15, 17, 30, 1),
(31, ':evil:', 'Evil or Very Mad', 'icon_evil.gif', 15, 17, 31, 1),
(32, ':twisted:', 'Twisted Evil', 'icon_twisted.gif', 15, 17, 32, 1),
(33, ':roll:', 'Rolling Eyes', 'icon_rolleyes.gif', 15, 17, 33, 1),
(34, ':!:', 'Exclamation', 'icon_exclaim.gif', 15, 17, 34, 1),
(35, ':?:', 'Question', 'icon_question.gif', 15, 17, 35, 1),
(36, ':idea:', 'Idea', 'icon_idea.gif', 15, 17, 36, 1),
(37, ':arrow:', 'Arrow', 'icon_arrow.gif', 15, 17, 37, 1),
(38, ':|', 'Neutral', 'icon_neutral.gif', 15, 17, 38, 1),
(39, ':-|', 'Neutral', 'icon_neutral.gif', 15, 17, 39, 1),
(40, ':mrgreen:', 'Mr. Green', 'icon_mrgreen.gif', 15, 17, 40, 1),
(41, ':geek:', 'Geek', 'icon_e_geek.gif', 17, 17, 41, 1),
(42, ':ugeek:', 'Uber Geek', 'icon_e_ugeek.gif', 17, 18, 42, 1);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_sphinx`
--

DROP TABLE IF EXISTS `phpbb_sphinx`;
CREATE TABLE IF NOT EXISTS `phpbb_sphinx` (
  `counter_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `max_doc_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`counter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_sphinx`
--

INSERT INTO `phpbb_sphinx` (`counter_id`, `max_doc_id`) VALUES
(1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_styles`
--

DROP TABLE IF EXISTS `phpbb_styles`;
CREATE TABLE IF NOT EXISTS `phpbb_styles` (
  `style_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `style_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `style_copyright` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `style_active` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `style_path` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '',
  `bbcode_bitfield` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT 'kNg=',
  `style_parent_id` int(4) UNSIGNED NOT NULL DEFAULT 0,
  `style_parent_tree` text COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`style_id`),
  UNIQUE KEY `style_name` (`style_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_styles`
--

INSERT INTO `phpbb_styles` (`style_id`, `style_name`, `style_copyright`, `style_active`, `style_path`, `bbcode_bitfield`, `style_parent_id`, `style_parent_tree`) VALUES
(1, 'prosilver', '&copy; phpBB Limited', 1, 'prosilver', '//g=', 0, ''),
(2, 'prosilver Special Edition', '© phpBB Limited, 2008', 1, 'prosilver_se', '//g=', 1, 'prosilver'),
(3, 'PBWoW3', '© PayBas 2015, @Sajaki 2017', 1, 'pbwow3', '//g=', 1, 'prosilver');

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_teampage`
--

DROP TABLE IF EXISTS `phpbb_teampage`;
CREATE TABLE IF NOT EXISTS `phpbb_teampage` (
  `teampage_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `teampage_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `teampage_position` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `teampage_parent` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`teampage_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_teampage`
--

INSERT INTO `phpbb_teampage` (`teampage_id`, `group_id`, `teampage_name`, `teampage_position`, `teampage_parent`) VALUES
(1, 5, '', 1, 0),
(2, 4, '', 2, 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_topics`
--

DROP TABLE IF EXISTS `phpbb_topics`;
CREATE TABLE IF NOT EXISTS `phpbb_topics` (
  `topic_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `icon_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `topic_attachment` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `topic_reported` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `topic_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `topic_poster` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `topic_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `topic_time_limit` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `topic_views` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `topic_status` tinyint(3) NOT NULL DEFAULT 0,
  `topic_type` tinyint(3) NOT NULL DEFAULT 0,
  `topic_first_post_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `topic_first_poster_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `topic_first_poster_colour` varchar(6) COLLATE utf8_bin NOT NULL DEFAULT '',
  `topic_last_post_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `topic_last_poster_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `topic_last_poster_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `topic_last_poster_colour` varchar(6) COLLATE utf8_bin NOT NULL DEFAULT '',
  `topic_last_post_subject` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `topic_last_post_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `topic_last_view_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `topic_moved_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `topic_bumped` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `topic_bumper` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `poll_title` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `poll_start` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `poll_length` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `poll_max_options` tinyint(4) NOT NULL DEFAULT 1,
  `poll_last_vote` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `poll_vote_change` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `topic_visibility` tinyint(3) NOT NULL DEFAULT 0,
  `topic_delete_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `topic_delete_reason` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `topic_delete_user` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `topic_posts_approved` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `topic_posts_unapproved` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `topic_posts_softdeleted` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`topic_id`),
  KEY `forum_id` (`forum_id`),
  KEY `forum_id_type` (`forum_id`,`topic_type`),
  KEY `last_post_time` (`topic_last_post_time`),
  KEY `fid_time_moved` (`forum_id`,`topic_last_post_time`,`topic_moved_id`),
  KEY `topic_visibility` (`topic_visibility`),
  KEY `forum_vis_last` (`forum_id`,`topic_visibility`,`topic_last_post_id`),
  KEY `latest_topics` (`forum_id`,`topic_last_post_time`,`topic_last_post_id`,`topic_moved_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_topics_posted`
--

DROP TABLE IF EXISTS `phpbb_topics_posted`;
CREATE TABLE IF NOT EXISTS `phpbb_topics_posted` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `topic_posted` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`,`topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_topics_track`
--

DROP TABLE IF EXISTS `phpbb_topics_track`;
CREATE TABLE IF NOT EXISTS `phpbb_topics_track` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `forum_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `mark_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`,`topic_id`),
  KEY `forum_id` (`forum_id`),
  KEY `topic_id` (`topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_topics_watch`
--

DROP TABLE IF EXISTS `phpbb_topics_watch`;
CREATE TABLE IF NOT EXISTS `phpbb_topics_watch` (
  `topic_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `notify_status` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  KEY `topic_id` (`topic_id`),
  KEY `user_id` (`user_id`),
  KEY `notify_stat` (`notify_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_users`
--

DROP TABLE IF EXISTS `phpbb_users`;
CREATE TABLE IF NOT EXISTS `phpbb_users` (
  `user_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_type` tinyint(2) NOT NULL DEFAULT 0,
  `group_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 3,
  `user_permissions` mediumtext COLLATE utf8_bin NOT NULL,
  `user_perm_from` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `user_ip` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_regdate` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `username` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `username_clean` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_password` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_passchg` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `user_email` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_email_hash` bigint(20) NOT NULL DEFAULT 0,
  `user_birthday` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_lastvisit` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `user_lastmark` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `user_lastpost_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `user_lastpage` varchar(200) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_last_confirm_key` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_last_search` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `user_warnings` tinyint(4) NOT NULL DEFAULT 0,
  `user_last_warning` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `user_login_attempts` tinyint(4) NOT NULL DEFAULT 0,
  `user_inactive_reason` tinyint(2) NOT NULL DEFAULT 0,
  `user_inactive_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `user_posts` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `user_lang` varchar(30) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_timezone` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_dateformat` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT 'd M Y H:i',
  `user_style` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `user_rank` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `user_colour` varchar(6) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_new_privmsg` int(4) NOT NULL DEFAULT 0,
  `user_unread_privmsg` int(4) NOT NULL DEFAULT 0,
  `user_last_privmsg` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `user_message_rules` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `user_full_folder` int(11) NOT NULL DEFAULT -3,
  `user_emailtime` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `user_topic_show_days` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `user_topic_sortby_type` varchar(1) COLLATE utf8_bin NOT NULL DEFAULT 't',
  `user_topic_sortby_dir` varchar(1) COLLATE utf8_bin NOT NULL DEFAULT 'd',
  `user_post_show_days` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `user_post_sortby_type` varchar(1) COLLATE utf8_bin NOT NULL DEFAULT 't',
  `user_post_sortby_dir` varchar(1) COLLATE utf8_bin NOT NULL DEFAULT 'a',
  `user_notify` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `user_notify_pm` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `user_notify_type` tinyint(4) NOT NULL DEFAULT 0,
  `user_allow_pm` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `user_allow_viewonline` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `user_allow_viewemail` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `user_allow_massemail` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `user_options` int(11) UNSIGNED NOT NULL DEFAULT 230271,
  `user_avatar` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_avatar_type` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_avatar_width` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `user_avatar_height` smallint(4) UNSIGNED NOT NULL DEFAULT 0,
  `user_sig` mediumtext COLLATE utf8_bin NOT NULL,
  `user_sig_bbcode_uid` varchar(8) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_sig_bbcode_bitfield` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_jabber` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_actkey` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_newpasswd` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_form_salt` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_new` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `user_reminded` tinyint(4) NOT NULL DEFAULT 0,
  `user_reminded_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username_clean` (`username_clean`),
  KEY `user_birthday` (`user_birthday`),
  KEY `user_email_hash` (`user_email_hash`),
  KEY `user_type` (`user_type`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_users`
--

INSERT INTO `phpbb_users` (`user_id`, `user_type`, `group_id`, `user_permissions`, `user_perm_from`, `user_ip`, `user_regdate`, `username`, `username_clean`, `user_password`, `user_passchg`, `user_email`, `user_email_hash`, `user_birthday`, `user_lastvisit`, `user_lastmark`, `user_lastpost_time`, `user_lastpage`, `user_last_confirm_key`, `user_last_search`, `user_warnings`, `user_last_warning`, `user_login_attempts`, `user_inactive_reason`, `user_inactive_time`, `user_posts`, `user_lang`, `user_timezone`, `user_dateformat`, `user_style`, `user_rank`, `user_colour`, `user_new_privmsg`, `user_unread_privmsg`, `user_last_privmsg`, `user_message_rules`, `user_full_folder`, `user_emailtime`, `user_topic_show_days`, `user_topic_sortby_type`, `user_topic_sortby_dir`, `user_post_show_days`, `user_post_sortby_type`, `user_post_sortby_dir`, `user_notify`, `user_notify_pm`, `user_notify_type`, `user_allow_pm`, `user_allow_viewonline`, `user_allow_viewemail`, `user_allow_massemail`, `user_options`, `user_avatar`, `user_avatar_type`, `user_avatar_width`, `user_avatar_height`, `user_sig`, `user_sig_bbcode_uid`, `user_sig_bbcode_bitfield`, `user_jabber`, `user_actkey`, `user_newpasswd`, `user_form_salt`, `user_new`, `user_reminded`, `user_reminded_time`) VALUES
(1, 2, 1, '', 0, '', 1529675815, 'Anonymous', 'anonymous', '', 0, '', 0, '', 0, 0, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', '', 'd M Y H:i', 3, 0, '', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 1, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '8c84dd198838df98', 1, 0, 0),
(2, 3, 5, 'zik0zjzik0zjzik0zjhra0hs\n\n\n\nzik0zjqmyfb4\n\n\n\nzik0zjziimf4\nzik0zjqmyfb4\nhwby9w000000\n\n\nhwby9w000000\n\n\n\nhwby9w000000', 0, '108.162.237.186', 1529675815, 'Administrator', 'administrator', '$2y$10$GSlXO2CzM4y2Mi1puUdD7.xi16waq1/dcaangExACy2N1Mc7WcHhe', 1536194745, 'openrsc.mailer@gmail.com', 255669274424, ' 0- 0-   0', 1536194711, 0, 1529716441, 'adm/index.php?i=acp_users&mode=overview&u=2', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'America/Anguilla', 'D M d, Y g:i a', 3, 1, 'AA0000', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 1, 1, 1, 1, 230271, '', '', 0, 0, '', '', '', '', '', '', '0ba215ee95f1cfde', 0, 0, 0),
(3, 2, 6, '', 0, '', 1529675815, 'AdsBot [Google]', 'adsbot [google]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '78b19ecb290d083b', 0, 0, 0),
(4, 2, 6, '', 0, '', 1529675815, 'Alexa [Bot]', 'alexa [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '67f695fa9783b0b7', 0, 0, 0),
(5, 2, 6, '', 0, '', 1529675815, 'Alta Vista [Bot]', 'alta vista [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'e66b20807371b4ad', 0, 0, 0),
(6, 2, 6, '', 0, '', 1529675815, 'Ask Jeeves [Bot]', 'ask jeeves [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '4c5073b77ed17bc3', 0, 0, 0),
(7, 2, 6, '', 0, '', 1529675815, 'Baidu [Spider]', 'baidu [spider]', '', 1529675815, '', 0, '', 1529712636, 1529675815, 0, 'index.php', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '8d9d0fd6e1d1c892', 0, 0, 0),
(8, 2, 6, '', 0, '', 1529675815, 'Bing [Bot]', 'bing [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '06ce95ac51ee459f', 0, 0, 0),
(9, 2, 6, '', 0, '', 1529675815, 'Exabot [Bot]', 'exabot [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '5d8eb506fa4b0108', 0, 0, 0),
(10, 2, 6, '', 0, '', 1529675815, 'FAST Enterprise [Crawler]', 'fast enterprise [crawler]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '3e7a2051ad1ffd97', 0, 0, 0),
(11, 2, 6, '', 0, '', 1529675815, 'FAST WebCrawler [Crawler]', 'fast webcrawler [crawler]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'f1204964e2492e6e', 0, 0, 0),
(12, 2, 6, '', 0, '', 1529675815, 'Francis [Bot]', 'francis [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'a3359575da7a348e', 0, 0, 0),
(13, 2, 6, '', 0, '', 1529675815, 'Gigabot [Bot]', 'gigabot [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '0bb6e80bda63ba31', 0, 0, 0),
(14, 2, 6, '', 0, '', 1529675815, 'Google Adsense [Bot]', 'google adsense [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'acc3c384d32a7feb', 0, 0, 0),
(15, 2, 6, '', 0, '', 1529675815, 'Google Desktop', 'google desktop', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '7dfef1395c81662b', 0, 0, 0),
(16, 2, 6, '', 0, '', 1529675815, 'Google Feedfetcher', 'google feedfetcher', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '245e937e2f0de53e', 0, 0, 0),
(17, 2, 6, '', 0, '', 1529675815, 'Google [Bot]', 'google [bot]', '', 1529675815, '', 0, '', 1529722928, 1529675815, 0, 'index.php', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '3d5d5e3f9f0b1b8b', 0, 0, 0),
(18, 2, 6, '', 0, '', 1529675815, 'Heise IT-Markt [Crawler]', 'heise it-markt [crawler]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'f00dce6cc7bf4286', 0, 0, 0),
(19, 2, 6, '', 0, '', 1529675815, 'Heritrix [Crawler]', 'heritrix [crawler]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'b0b51a880c792094', 0, 0, 0),
(20, 2, 6, '', 0, '', 1529675815, 'IBM Research [Bot]', 'ibm research [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '1276f83353506c1b', 0, 0, 0),
(21, 2, 6, '', 0, '', 1529675815, 'ICCrawler - ICjobs', 'iccrawler - icjobs', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'ee1fd87ac906ac84', 0, 0, 0),
(22, 2, 6, '', 0, '', 1529675815, 'ichiro [Crawler]', 'ichiro [crawler]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '87823fd01955fc69', 0, 0, 0),
(23, 2, 6, '', 0, '', 1529675815, 'Majestic-12 [Bot]', 'majestic-12 [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '55c2ec975b0c043e', 0, 0, 0),
(24, 2, 6, '', 0, '', 1529675815, 'Metager [Bot]', 'metager [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '0fba164f090c636f', 0, 0, 0),
(25, 2, 6, '', 0, '', 1529675815, 'MSN NewsBlogs', 'msn newsblogs', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '555391741f87a615', 0, 0, 0),
(26, 2, 6, '', 0, '', 1529675815, 'MSN [Bot]', 'msn [bot]', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'd8eab6ba336dbaa6', 0, 0, 0),
(27, 2, 6, '', 0, '', 1529675815, 'MSNbot Media', 'msnbot media', '', 1529675815, '', 0, '', 0, 1529675815, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '2b3af8045b41ab29', 0, 0, 0),
(28, 2, 6, '', 0, '', 1529675816, 'Nutch [Bot]', 'nutch [bot]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'e30f2a766c3eb1f1', 0, 0, 0),
(29, 2, 6, '', 0, '', 1529675816, 'Online link [Validator]', 'online link [validator]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '7cb4f7ddab39ec96', 0, 0, 0),
(30, 2, 6, '', 0, '', 1529675816, 'psbot [Picsearch]', 'psbot [picsearch]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'dbc7e95f3a26cb5a', 0, 0, 0),
(31, 2, 6, '', 0, '', 1529675816, 'Sensis [Crawler]', 'sensis [crawler]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '2aedc9a9a4591ea2', 0, 0, 0),
(32, 2, 6, '', 0, '', 1529675816, 'SEO Crawler', 'seo crawler', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '02dd0d06b2f644af', 0, 0, 0),
(33, 2, 6, '', 0, '', 1529675816, 'Seoma [Crawler]', 'seoma [crawler]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '08c0d43d357ff4fb', 0, 0, 0),
(34, 2, 6, '', 0, '', 1529675816, 'SEOSearch [Crawler]', 'seosearch [crawler]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'ed46850081502ebd', 0, 0, 0),
(35, 2, 6, '', 0, '', 1529675816, 'Snappy [Bot]', 'snappy [bot]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'f651eed0ea2931f3', 0, 0, 0),
(36, 2, 6, '', 0, '', 1529675816, 'Steeler [Crawler]', 'steeler [crawler]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'e62f36057fe21e87', 0, 0, 0),
(37, 2, 6, '', 0, '', 1529675816, 'Telekom [Bot]', 'telekom [bot]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'cd8fdf9a43f06479', 0, 0, 0),
(38, 2, 6, '', 0, '', 1529675816, 'TurnitinBot [Bot]', 'turnitinbot [bot]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '3433d42c31108769', 0, 0, 0),
(39, 2, 6, '', 0, '', 1529675816, 'Voyager [Bot]', 'voyager [bot]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'fb63dbcf3d78e181', 0, 0, 0),
(40, 2, 6, '', 0, '', 1529675816, 'W3 [Sitesearch]', 'w3 [sitesearch]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '5f0cefb70ff1e9ab', 0, 0, 0),
(41, 2, 6, '', 0, '', 1529675816, 'W3C [Linkcheck]', 'w3c [linkcheck]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '6d70ba59aed7f3ea', 0, 0, 0),
(42, 2, 6, '', 0, '', 1529675816, 'W3C [Validator]', 'w3c [validator]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '529c1a356d1e3418', 0, 0, 0),
(43, 2, 6, '', 0, '', 1529675816, 'YaCy [Bot]', 'yacy [bot]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '9c00f0b553011599', 0, 0, 0),
(44, 2, 6, '', 0, '', 1529675816, 'Yahoo MMCrawler [Bot]', 'yahoo mmcrawler [bot]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'ac77a2357021e66a', 0, 0, 0),
(45, 2, 6, '', 0, '', 1529675816, 'Yahoo Slurp [Bot]', 'yahoo slurp [bot]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', '5078bee9835e375c', 0, 0, 0),
(46, 2, 6, '', 0, '', 1529675816, 'Yahoo [Bot]', 'yahoo [bot]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'c10a5c08df22c1fc', 0, 0, 0),
(47, 2, 6, '', 0, '', 1529675816, 'YahooSeeker [Bot]', 'yahooseeker [bot]', '', 1529675816, '', 0, '', 0, 1529675816, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 'en', 'UTC', 'D M d, Y g:i a', 3, 0, '9E8DA7', 0, 0, 0, 0, -3, 0, 0, 't', 'd', 0, 't', 'a', 0, 1, 0, 0, 1, 1, 0, 230271, '', '', 0, 0, '', '', '', '', '', '', 'd14b4ccbae99383e', 0, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_user_group`
--

DROP TABLE IF EXISTS `phpbb_user_group`;
CREATE TABLE IF NOT EXISTS `phpbb_user_group` (
  `group_id` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `group_leader` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `user_pending` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  KEY `group_id` (`group_id`),
  KEY `user_id` (`user_id`),
  KEY `group_leader` (`group_leader`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_user_group`
--

INSERT INTO `phpbb_user_group` (`group_id`, `user_id`, `group_leader`, `user_pending`) VALUES
(1, 1, 0, 0),
(2, 2, 0, 0),
(4, 2, 0, 0),
(5, 2, 1, 0),
(6, 3, 0, 0),
(6, 4, 0, 0),
(6, 5, 0, 0),
(6, 6, 0, 0),
(6, 7, 0, 0),
(6, 8, 0, 0),
(6, 9, 0, 0),
(6, 10, 0, 0),
(6, 11, 0, 0),
(6, 12, 0, 0),
(6, 13, 0, 0),
(6, 14, 0, 0),
(6, 15, 0, 0),
(6, 16, 0, 0),
(6, 17, 0, 0),
(6, 18, 0, 0),
(6, 19, 0, 0),
(6, 20, 0, 0),
(6, 21, 0, 0),
(6, 22, 0, 0),
(6, 23, 0, 0),
(6, 24, 0, 0),
(6, 25, 0, 0),
(6, 26, 0, 0),
(6, 27, 0, 0),
(6, 28, 0, 0),
(6, 29, 0, 0),
(6, 30, 0, 0),
(6, 31, 0, 0),
(6, 32, 0, 0),
(6, 33, 0, 0),
(6, 34, 0, 0),
(6, 35, 0, 0),
(6, 36, 0, 0),
(6, 37, 0, 0),
(6, 38, 0, 0),
(6, 39, 0, 0),
(6, 40, 0, 0),
(6, 41, 0, 0),
(6, 42, 0, 0),
(6, 43, 0, 0),
(6, 44, 0, 0),
(6, 45, 0, 0),
(6, 46, 0, 0),
(6, 47, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_user_notifications`
--

DROP TABLE IF EXISTS `phpbb_user_notifications`;
CREATE TABLE IF NOT EXISTS `phpbb_user_notifications` (
  `item_type` varchar(165) COLLATE utf8_bin NOT NULL DEFAULT '',
  `item_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `method` varchar(165) COLLATE utf8_bin NOT NULL DEFAULT '',
  `notify` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  UNIQUE KEY `itm_usr_mthd` (`item_type`,`item_id`,`user_id`,`method`),
  KEY `user_id` (`user_id`),
  KEY `uid_itm_id` (`user_id`,`item_id`),
  KEY `usr_itm_tpe` (`user_id`,`item_type`,`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `phpbb_user_notifications`
--

INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES
('notification.type.post', 0, 2, 'notification.method.board', 1),
('notification.type.post', 0, 2, 'notification.method.email', 1),
('notification.type.post', 0, 3, 'notification.method.email', 1),
('notification.type.post', 0, 4, 'notification.method.email', 1),
('notification.type.post', 0, 5, 'notification.method.email', 1),
('notification.type.post', 0, 6, 'notification.method.email', 1),
('notification.type.post', 0, 7, 'notification.method.email', 1),
('notification.type.post', 0, 8, 'notification.method.email', 1),
('notification.type.post', 0, 9, 'notification.method.email', 1),
('notification.type.post', 0, 10, 'notification.method.email', 1),
('notification.type.post', 0, 11, 'notification.method.email', 1),
('notification.type.post', 0, 12, 'notification.method.email', 1),
('notification.type.post', 0, 13, 'notification.method.email', 1),
('notification.type.post', 0, 14, 'notification.method.email', 1),
('notification.type.post', 0, 15, 'notification.method.email', 1),
('notification.type.post', 0, 16, 'notification.method.email', 1),
('notification.type.post', 0, 17, 'notification.method.email', 1),
('notification.type.post', 0, 18, 'notification.method.email', 1),
('notification.type.post', 0, 19, 'notification.method.email', 1),
('notification.type.post', 0, 20, 'notification.method.email', 1),
('notification.type.post', 0, 21, 'notification.method.email', 1),
('notification.type.post', 0, 22, 'notification.method.email', 1),
('notification.type.post', 0, 23, 'notification.method.email', 1),
('notification.type.post', 0, 24, 'notification.method.email', 1),
('notification.type.post', 0, 25, 'notification.method.email', 1),
('notification.type.post', 0, 26, 'notification.method.email', 1),
('notification.type.post', 0, 27, 'notification.method.email', 1),
('notification.type.post', 0, 28, 'notification.method.email', 1),
('notification.type.post', 0, 29, 'notification.method.email', 1),
('notification.type.post', 0, 30, 'notification.method.email', 1),
('notification.type.post', 0, 31, 'notification.method.email', 1),
('notification.type.post', 0, 32, 'notification.method.email', 1),
('notification.type.post', 0, 33, 'notification.method.email', 1),
('notification.type.post', 0, 34, 'notification.method.email', 1),
('notification.type.post', 0, 35, 'notification.method.email', 1),
('notification.type.post', 0, 36, 'notification.method.email', 1),
('notification.type.post', 0, 37, 'notification.method.email', 1),
('notification.type.post', 0, 38, 'notification.method.email', 1),
('notification.type.post', 0, 39, 'notification.method.email', 1),
('notification.type.post', 0, 40, 'notification.method.email', 1),
('notification.type.post', 0, 41, 'notification.method.email', 1),
('notification.type.post', 0, 42, 'notification.method.email', 1),
('notification.type.post', 0, 43, 'notification.method.email', 1),
('notification.type.post', 0, 44, 'notification.method.email', 1),
('notification.type.post', 0, 45, 'notification.method.email', 1),
('notification.type.post', 0, 46, 'notification.method.email', 1),
('notification.type.post', 0, 47, 'notification.method.email', 1),
('notification.type.post', 0, 48, 'notification.method.email', 1),
('notification.type.post', 0, 49, 'notification.method.email', 1),
('notification.type.post', 0, 50, 'notification.method.email', 1),
('notification.type.topic', 0, 2, 'notification.method.board', 1),
('notification.type.topic', 0, 2, 'notification.method.email', 1),
('notification.type.topic', 0, 3, 'notification.method.email', 1),
('notification.type.topic', 0, 4, 'notification.method.email', 1),
('notification.type.topic', 0, 5, 'notification.method.email', 1),
('notification.type.topic', 0, 6, 'notification.method.email', 1),
('notification.type.topic', 0, 7, 'notification.method.email', 1),
('notification.type.topic', 0, 8, 'notification.method.email', 1),
('notification.type.topic', 0, 9, 'notification.method.email', 1),
('notification.type.topic', 0, 10, 'notification.method.email', 1),
('notification.type.topic', 0, 11, 'notification.method.email', 1),
('notification.type.topic', 0, 12, 'notification.method.email', 1),
('notification.type.topic', 0, 13, 'notification.method.email', 1),
('notification.type.topic', 0, 14, 'notification.method.email', 1),
('notification.type.topic', 0, 15, 'notification.method.email', 1),
('notification.type.topic', 0, 16, 'notification.method.email', 1),
('notification.type.topic', 0, 17, 'notification.method.email', 1),
('notification.type.topic', 0, 18, 'notification.method.email', 1),
('notification.type.topic', 0, 19, 'notification.method.email', 1),
('notification.type.topic', 0, 20, 'notification.method.email', 1),
('notification.type.topic', 0, 21, 'notification.method.email', 1),
('notification.type.topic', 0, 22, 'notification.method.email', 1),
('notification.type.topic', 0, 23, 'notification.method.email', 1),
('notification.type.topic', 0, 24, 'notification.method.email', 1),
('notification.type.topic', 0, 25, 'notification.method.email', 1),
('notification.type.topic', 0, 26, 'notification.method.email', 1),
('notification.type.topic', 0, 27, 'notification.method.email', 1),
('notification.type.topic', 0, 28, 'notification.method.email', 1),
('notification.type.topic', 0, 29, 'notification.method.email', 1),
('notification.type.topic', 0, 30, 'notification.method.email', 1),
('notification.type.topic', 0, 31, 'notification.method.email', 1),
('notification.type.topic', 0, 32, 'notification.method.email', 1),
('notification.type.topic', 0, 33, 'notification.method.email', 1),
('notification.type.topic', 0, 34, 'notification.method.email', 1),
('notification.type.topic', 0, 35, 'notification.method.email', 1),
('notification.type.topic', 0, 36, 'notification.method.email', 1),
('notification.type.topic', 0, 37, 'notification.method.email', 1),
('notification.type.topic', 0, 38, 'notification.method.email', 1),
('notification.type.topic', 0, 39, 'notification.method.email', 1),
('notification.type.topic', 0, 40, 'notification.method.email', 1),
('notification.type.topic', 0, 41, 'notification.method.email', 1),
('notification.type.topic', 0, 42, 'notification.method.email', 1),
('notification.type.topic', 0, 43, 'notification.method.email', 1),
('notification.type.topic', 0, 44, 'notification.method.email', 1),
('notification.type.topic', 0, 45, 'notification.method.email', 1),
('notification.type.topic', 0, 46, 'notification.method.email', 1),
('notification.type.topic', 0, 47, 'notification.method.email', 1),
('notification.type.topic', 0, 48, 'notification.method.email', 1),
('notification.type.topic', 0, 49, 'notification.method.email', 1),
('notification.type.topic', 0, 50, 'notification.method.email', 1);

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_warnings`
--

DROP TABLE IF EXISTS `phpbb_warnings`;
CREATE TABLE IF NOT EXISTS `phpbb_warnings` (
  `warning_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `post_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `log_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `warning_time` int(11) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`warning_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_words`
--

DROP TABLE IF EXISTS `phpbb_words`;
CREATE TABLE IF NOT EXISTS `phpbb_words` (
  `word_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `word` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `replacement` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`word_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `phpbb_zebra`
--

DROP TABLE IF EXISTS `phpbb_zebra`;
CREATE TABLE IF NOT EXISTS `phpbb_zebra` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `zebra_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `friend` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `foe` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`,`zebra_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `phpbb_posts`
--
ALTER TABLE `phpbb_posts` ADD FULLTEXT KEY `post_subject` (`post_subject`);
ALTER TABLE `phpbb_posts` ADD FULLTEXT KEY `post_content` (`post_text`,`post_subject`);
ALTER TABLE `phpbb_posts` ADD FULLTEXT KEY `post_text` (`post_text`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
