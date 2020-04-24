/*
 * New tables section
 * The queries performed in the section will add new tables and drop any that already exist
 */

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;


DROP TABLE IF EXISTS `openrsc_bank`;
CREATE TABLE IF NOT EXISTS `openrsc_bank`
(
    `playerID` int(10) UNSIGNED NOT NULL,
    `id`       int(10) UNSIGNED NOT NULL,
    `amount`   int(10) UNSIGNED NOT NULL DEFAULT 1,
    `slot`     int(5) UNSIGNED  NOT NULL DEFAULT 0,
    `dbid`     int(10)          NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_chat_logs`;
CREATE TABLE IF NOT EXISTS `openrsc_chat_logs`
(
    `id`      int(10)          NOT NULL AUTO_INCREMENT,
    `sender`  varchar(12)      NOT NULL,
    `message` varchar(255)     NOT NULL,
    `time`    int(10) UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `time` (`time`),
    KEY `sender` (`sender`),
    KEY `message` (`message`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_curstats`;
CREATE TABLE IF NOT EXISTS `openrsc_curstats`
(
    `id`             int(10) UNSIGNED    NOT NULL AUTO_INCREMENT,
    `playerID`       int(10) UNSIGNED    NOT NULL,
    `cur_attack`     tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_defense`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_strength`   tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_hits`       tinyint(3) UNSIGNED NOT NULL DEFAULT 10,
    `cur_ranged`     tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_prayer`     tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_magic`      tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_cooking`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_woodcut`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_fletching`  tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_fishing`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_firemaking` tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_crafting`   tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_smithing`   tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_mining`     tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_herblaw`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_agility`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cur_thieving`   tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    KEY `playerID` (`playerID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_droplogs`;
CREATE TABLE IF NOT EXISTS `openrsc_droplogs`
(
    `ID`         int(11)   NOT NULL AUTO_INCREMENT,
    `itemID`     int(10)            DEFAULT NULL,
    `playerID`   int(10)            DEFAULT NULL,
    `dropAmount` int(10)            DEFAULT NULL,
    `npcId`      int(10)            DEFAULT NULL,
    `ts`         timestamp NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

DROP TABLE IF EXISTS `openrsc_experience`;
CREATE TABLE IF NOT EXISTS `openrsc_experience`
(
    `id`             int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `playerID`       int(10) UNSIGNED NOT NULL,
    `exp_attack`     int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_defense`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_strength`   int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_hits`       int(9) UNSIGNED  NOT NULL DEFAULT 4616,
    `exp_ranged`     int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_prayer`     int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_magic`      int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_cooking`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_woodcut`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_fletching`  int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_fishing`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_firemaking` int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_crafting`   int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_smithing`   int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_mining`     int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_herblaw`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_agility`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `exp_thieving`   int(9) UNSIGNED  NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `playerID` (`playerID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_friends`;
CREATE TABLE IF NOT EXISTS `openrsc_friends`
(
    `playerID`   int(10) UNSIGNED    NOT NULL,
    `friend`     bigint(19) UNSIGNED NOT NULL,
    `friendName` varchar(12)         NOT NULL,
    `dbid`       int(10)             NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`),
    KEY `friend` (`friend`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_game_reports`;
CREATE TABLE IF NOT EXISTS `openrsc_game_reports`
(
    `id`                 int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `reporter`           varchar(12)      NOT NULL,
    `reported`           varchar(12)      NOT NULL,
    `time`               int(10) UNSIGNED NOT NULL,
    `reason`             int(5) UNSIGNED  NOT NULL,
    `chatlog`            text                      DEFAULT NULL,
    `reporter_x`         int(5)                    DEFAULT NULL,
    `reporter_y`         int(5)                    DEFAULT NULL,
    `reported_x`         int(5)           NOT NULL DEFAULT 0,
    `reported_y`         int(5)                    DEFAULT 0,
    `suggests_or_mutes`  tinyint(1)                DEFAULT NULL,
    `tried_apply_action` tinyint(1)                DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_generic_logs`;
CREATE TABLE IF NOT EXISTS `openrsc_generic_logs`
(
    `id`      int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `message` text             NOT NULL,
    `time`    int(10) UNSIGNED NOT NULL,
    PRIMARY KEY (`id`),
    KEY `id` (`id`),
    KEY `time` (`time`),
    KEY `message` (`message`(333))
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_ignores`;
CREATE TABLE IF NOT EXISTS `openrsc_ignores`
(
    `playerID` int(10) UNSIGNED    NOT NULL,
    `ignore`   bigint(19) UNSIGNED NOT NULL,
    `dbid`     int(10)             NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`),
    KEY `ignore` (`ignore`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_invitems`;
CREATE TABLE IF NOT EXISTS `openrsc_invitems`
(
    `playerID` int(10) UNSIGNED    NOT NULL,
    `id`       int(10) UNSIGNED    NOT NULL,
    `amount`   int(10) UNSIGNED    NOT NULL DEFAULT 1,
    `wielded`  tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
    `slot`     int(5) UNSIGNED     NOT NULL,
    `dbid`     int(10)             NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_live_feeds`;
CREATE TABLE IF NOT EXISTS `openrsc_live_feeds`
(
    `id`       int(10)          NOT NULL AUTO_INCREMENT,
    `username` varchar(12)      NOT NULL,
    `message`  varchar(165)     NOT NULL,
    `time`     int(10) UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_logins`;
CREATE TABLE IF NOT EXISTS `openrsc_logins`
(
    `playerID` int(10) UNSIGNED NOT NULL,
    `time`     int(5) UNSIGNED  NOT NULL,
    `ip`       varchar(255)     NOT NULL DEFAULT '0.0.0.0',
    `dbid`     int(10)          NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`),
    KEY `ip` (`ip`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_npckills`;
CREATE TABLE IF NOT EXISTS `openrsc_npckills`
(
    `ID`        int(10) NOT NULL AUTO_INCREMENT,
    `npcID`     int(10) DEFAULT NULL,
    `playerID`  int(10) DEFAULT NULL,
    `killCount` int(10) DEFAULT 0,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

DROP TABLE IF EXISTS `openrsc_players`;
CREATE TABLE IF NOT EXISTS `openrsc_players`
(
    `id`                int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `username`          varchar(12)      NOT NULL    DEFAULT '',
    `group_id`          int(10)                      DEFAULT 10,
    `email`             varchar(255)                 DEFAULT NULL,
    `pass`              varchar(512)     NOT NULL,
    `salt`              varchar(250)     NOT NULL    DEFAULT '',
    `combat`            int(10)                      DEFAULT 3,
    `skill_total`       int(10)                      DEFAULT 27,
    `x`                 int(5) UNSIGNED              DEFAULT 216,
    `y`                 int(5) UNSIGNED              DEFAULT 451,
    `fatigue`           int(10)                      DEFAULT 0,
    `combatstyle`       tinyint(1)                   DEFAULT 0,
    `block_chat`        tinyint(1) UNSIGNED          DEFAULT 0,
    `block_private`     tinyint(1) UNSIGNED          DEFAULT 0,
    `block_trade`       tinyint(1) UNSIGNED          DEFAULT 0,
    `block_duel`        tinyint(1) UNSIGNED          DEFAULT 0,
    `cameraauto`        tinyint(1) UNSIGNED          DEFAULT 0,
    `onemouse`          tinyint(1) UNSIGNED          DEFAULT 0,
    `soundoff`          tinyint(1) UNSIGNED          DEFAULT 0,
    `haircolour`        int(5) UNSIGNED              DEFAULT 2,
    `topcolour`         int(5) UNSIGNED              DEFAULT 8,
    `trousercolour`     int(5) UNSIGNED              DEFAULT 14,
    `skincolour`        int(5) UNSIGNED              DEFAULT 0,
    `headsprite`        int(5) UNSIGNED              DEFAULT 1,
    `bodysprite`        int(5) UNSIGNED              DEFAULT 2,
    `male`              tinyint(1) UNSIGNED          DEFAULT 1,
    `creation_date`     int(10) UNSIGNED NOT NULL    DEFAULT 0,
    `creation_ip`       varchar(255)     NOT NULL    DEFAULT '0.0.0.0',
    `login_date`        int(10) UNSIGNED             DEFAULT 0,
    `login_ip`          varchar(255)                 DEFAULT '0.0.0.0',
    `banned`            varchar(255)     NOT NULL    DEFAULT '0',
    `offences`          int(11)          NOT NULL    DEFAULT 0,
    `muted`             varchar(255)     NOT NULL    DEFAULT '0',
    `kills`             int(10)          NOT NULL    DEFAULT 0,
    `deaths`            int(10)                      DEFAULT 0,
    `online`            tinyint(1) UNSIGNED ZEROFILL DEFAULT 0,
    `quest_points`      int(5)                       DEFAULT NULL,
    `bank_size`         int(10) UNSIGNED NOT NULL    DEFAULT 192,
    `lastRecoveryTryId` int(10) UNSIGNED             DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `id` (`id`),
    KEY `skill_total` (`skill_total`),
    KEY `group_id` (`group_id`),
    KEY `banned` (`banned`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_player_cache`;
CREATE TABLE IF NOT EXISTS `openrsc_player_cache`
(
    `playerID` int(10) UNSIGNED NOT NULL,
    `type`     tinyint(1)       NOT NULL,
    `key`      varchar(32)      NOT NULL,
    `value`    varchar(150)     NOT NULL,
    `dbid`     int(10)          NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_player_change_recovery`;
CREATE TABLE IF NOT EXISTS `openrsc_player_change_recovery`
(
    `playerID`  int(10) UNSIGNED NOT NULL,
    `username`  varchar(12)      NOT NULL DEFAULT '',
    `question1` varchar(256)     NOT NULL DEFAULT '',
    `answer1`   varchar(512)     NOT NULL DEFAULT '',
    `question2` varchar(256)     NOT NULL DEFAULT '',
    `answer2`   varchar(512)     NOT NULL DEFAULT '',
    `question3` varchar(256)     NOT NULL DEFAULT '',
    `answer3`   varchar(512)     NOT NULL DEFAULT '',
    `question4` varchar(256)     NOT NULL DEFAULT '',
    `answer4`   varchar(512)     NOT NULL DEFAULT '',
    `question5` varchar(256)     NOT NULL DEFAULT '',
    `answer5`   varchar(512)     NOT NULL DEFAULT '',
    `date_set`  int(10) UNSIGNED NOT NULL DEFAULT 0,
    `ip_set`    varchar(255)              DEFAULT '0.0.0.0',
    PRIMARY KEY (`playerID`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_player_contact_details`;
CREATE TABLE IF NOT EXISTS `openrsc_player_contact_details`
(
    `playerID`      int(10) UNSIGNED NOT NULL,
    `username`      varchar(12)      NOT NULL DEFAULT '',
    `fullname`      varchar(100)              DEFAULT '',
    `zipCode`       varchar(10)               DEFAULT '',
    `country`       varchar(100)              DEFAULT '',
    `email`         varchar(255)              DEFAULT NULL,
    `date_modified` int(10) UNSIGNED NOT NULL DEFAULT 0,
    `ip`            varchar(255)              DEFAULT '0.0.0.0',
    PRIMARY KEY (`playerID`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_player_recovery`;
CREATE TABLE IF NOT EXISTS `openrsc_player_recovery`
(
    `playerID`      int(10) UNSIGNED NOT NULL,
    `username`      varchar(12)      NOT NULL DEFAULT '',
    `question1`     varchar(256)     NOT NULL DEFAULT '',
    `answer1`       varchar(512)     NOT NULL DEFAULT '',
    `question2`     varchar(256)     NOT NULL DEFAULT '',
    `answer2`       varchar(512)     NOT NULL DEFAULT '',
    `question3`     varchar(256)     NOT NULL DEFAULT '',
    `answer3`       varchar(512)     NOT NULL DEFAULT '',
    `question4`     varchar(256)     NOT NULL DEFAULT '',
    `answer4`       varchar(512)     NOT NULL DEFAULT '',
    `question5`     varchar(256)     NOT NULL DEFAULT '',
    `answer5`       varchar(512)     NOT NULL DEFAULT '',
    `date_set`      int(10) UNSIGNED NOT NULL DEFAULT 0,
    `ip_set`        varchar(255)              DEFAULT '0.0.0.0',
    `previous_pass` varchar(512)              DEFAULT NULL,
    `earlier_pass`  varchar(512)              DEFAULT NULL,
    PRIMARY KEY (`playerID`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_player_security_changes`;
CREATE TABLE IF NOT EXISTS `openrsc_player_security_changes`
(
    `id`         int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `playerID`   int(10) UNSIGNED NOT NULL,
    `eventAlias` varchar(20)      NOT NULL,
    `date`       int(10) UNSIGNED NOT NULL DEFAULT 0,
    `ip`         varchar(255)              DEFAULT '0.0.0.0',
    `message`    text                      DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_private_message_logs`;
CREATE TABLE IF NOT EXISTS `openrsc_private_message_logs`
(
    `id`       int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `sender`   varchar(12)      NOT NULL,
    `message`  varchar(255)     NOT NULL,
    `reciever` varchar(12)      NOT NULL,
    `time`     int(10) UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `reciever` (`reciever`),
    KEY `time` (`time`),
    KEY `sender` (`sender`),
    KEY `message` (`message`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_quests`;
CREATE TABLE IF NOT EXISTS `openrsc_quests`
(
    `dbid`     int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `playerID` int(10) UNSIGNED NOT NULL,
    `id`       int(10) DEFAULT NULL,
    `stage`    int(10) DEFAULT NULL,
    PRIMARY KEY (`dbid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_recovery_attempts`;
CREATE TABLE IF NOT EXISTS `openrsc_recovery_attempts`
(
    `playerID` int(10) UNSIGNED NOT NULL,
    `username` varchar(12)      NOT NULL DEFAULT '',
    `time`     int(5) UNSIGNED  NOT NULL,
    `ip`       varchar(255)     NOT NULL DEFAULT '0.0.0.0',
    `dbid`     int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`),
    KEY `ip` (`ip`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_staff_logs`;
CREATE TABLE IF NOT EXISTS `openrsc_staff_logs`
(
    `id`              int(11)          NOT NULL AUTO_INCREMENT,
    `staff_username`  varchar(12)               DEFAULT NULL,
    `action`          tinyint(2) UNSIGNED       DEFAULT NULL,
    `affected_player` varchar(12)               DEFAULT NULL,
    `time`            int(10) UNSIGNED NOT NULL,
    `staff_x`         int(5) UNSIGNED  NOT NULL DEFAULT 0,
    `staff_y`         int(5) UNSIGNED           DEFAULT 0,
    `affected_x`      int(5) UNSIGNED           DEFAULT 0,
    `affected_y`      int(5) UNSIGNED           DEFAULT 0,
    `staff_ip`        varchar(15)               DEFAULT '0.0.0.0',
    `affected_ip`     varchar(15)               DEFAULT '0.0.0.0',
    `extra`           varchar(255)              DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_trade_logs`;
CREATE TABLE IF NOT EXISTS `openrsc_trade_logs`
(
    `id`            int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `player1`       varchar(12) CHARACTER SET utf16 DEFAULT NULL,
    `player2`       varchar(12)                     DEFAULT NULL,
    `player1_items` varchar(255)                    DEFAULT NULL,
    `player2_items` varchar(255)                    DEFAULT NULL,
    `player1_ip`    varchar(39)      NOT NULL       DEFAULT '0.0.0.0',
    `player2_ip`    varchar(39)      NOT NULL       DEFAULT '0.0.0.0',
    `time`          int(10)                         DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `player1` (`player1`),
    KEY `player2` (`player2`),
    KEY `player1_ip` (`player1_ip`),
    KEY `player2_ip` (`player2_ip`),
    KEY `time` (`time`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
