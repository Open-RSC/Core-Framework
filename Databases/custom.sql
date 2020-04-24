/*
 * New tables section
 * The queries performed in the section will add new tables and drop any that already exist
 */

DROP TABLE IF EXISTS `openrsc_expired_auctions`;
CREATE TABLE IF NOT EXISTS `openrsc_expired_auctions`
(
    `playerID`    int(10) UNSIGNED NOT NULL,
    `claim_id`    int(11)          NOT NULL AUTO_INCREMENT,
    `item_id`     int(11)          NOT NULL,
    `item_amount` int(11)          NOT NULL,
    `time`        varchar(255)     NOT NULL,
    `claim_time`  varchar(255)     NOT NULL DEFAULT '0',
    `claimed`     tinyint(1)       NOT NULL DEFAULT 0,
    `explanation` varchar(255)     NOT NULL DEFAULT ' ',
    PRIMARY KEY (`claim_id`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_clan`;
CREATE TABLE IF NOT EXISTS `openrsc_clan`
(
    `id`                int(10)               NOT NULL AUTO_INCREMENT,
    `name`              varchar(16)           NOT NULL,
    `tag`               varchar(5)            NOT NULL,
    `leader`            varchar(12)           NOT NULL,
    `kick_setting`      tinyint(1) UNSIGNED   NOT NULL DEFAULT 1,
    `invite_setting`    tinyint(1) UNSIGNED   NOT NULL DEFAULT 1,
    `allow_search_join` tinyint(1) UNSIGNED   NOT NULL DEFAULT 2,
    `matches_won`       mediumint(5) UNSIGNED NOT NULL DEFAULT 0,
    `matches_lost`      mediumint(5) UNSIGNED NOT NULL DEFAULT 0,
    `clan_points`       int(10) UNSIGNED      NOT NULL DEFAULT 0,
    `bank_size`         mediumint(5) UNSIGNED NOT NULL DEFAULT 10,
    PRIMARY KEY (`id`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_clan_players`;
CREATE TABLE IF NOT EXISTS `openrsc_clan_players`
(
    `id`       int(10)               NOT NULL AUTO_INCREMENT,
    `clan_id`  int(10) UNSIGNED      NOT NULL,
    `username` varchar(12)           NOT NULL,
    `rank`     tinyint(1) UNSIGNED   NOT NULL,
    `kills`    mediumint(5) UNSIGNED NOT NULL DEFAULT 0,
    `deaths`   mediumint(5) UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_auctions`;
CREATE TABLE IF NOT EXISTS `openrsc_auctions`
(
    `auctionID`       bigint(20)       NOT NULL AUTO_INCREMENT,
    `itemID`          int(11)          NOT NULL,
    `amount`          int(11)          NOT NULL,
    `amount_left`     int(11)          NOT NULL,
    `price`           int(11)          NOT NULL,
    `seller`          int(10) UNSIGNED NOT NULL,
    `seller_username` varchar(12)      NOT NULL,
    `buyer_info`      text             NOT NULL,
    `sold-out`        tinyint(4)       NOT NULL DEFAULT 0,
    `time`            varchar(255)     NOT NULL DEFAULT '0',
    `was_cancel`      tinyint(1)       NOT NULL DEFAULT 0,
    PRIMARY KEY (`auctionID`),
    KEY `auctionID` (`auctionID`),
    KEY `itemID` (`itemID`),
    KEY `seller_username_2` (`seller_username`),
    KEY `time` (`time`),
    KEY `seller_username` (`seller_username`),
    KEY `buyer_info` (`buyer_info`)
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

DROP TABLE IF EXISTS `openrsc_bankpresets`;
CREATE TABLE IF NOT EXISTS `openrsc_bankpresets`
(
    `id`        int(10)          NOT NULL AUTO_INCREMENT,
    `playerID`  int(10) unsigned NOT NULL,
    `slot`      int(10) unsigned NOT NULL,
    `inventory` blob DEFAULT NULL,
    `equipment` blob DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `openrsc_equipped`;
CREATE TABLE IF NOT EXISTS `openrsc_equipped`
(
    `playerID` int(10) UNSIGNED NOT NULL,
    `id`       int(10) UNSIGNED NOT NULL,
    `amount`   int(10) UNSIGNED NOT NULL DEFAULT 1,
    `dbid`     int(10)          NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


/*
 * Player table conversion section
 * The queries performed in the section will modify existing player tables to convert the database into one compatible with custom features
 */

ALTER TABLE `openrsc_players` DROP COLUMN `amount`;
ALTER TABLE `openrsc_players` ADD `iron_man` tinyint(1) UNSIGNED NOT NULL DEFAULT 0;
ALTER TABLE `openrsc_players` ADD `iron_man` tinyint(1) UNSIGNED NOT NULL DEFAULT 0;
ALTER TABLE `openrsc_players` ADD `iron_man_restriction` tinyint(1) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `openrsc_players` ADD `hc_ironman_death` tinyint(1) UNSIGNED NOT NULL DEFAULT 0;

