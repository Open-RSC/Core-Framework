/*
 * New player tables section
 * The queries performed in the section will add new tables and drop any that already exist
 */

DROP TABLE IF EXISTS `bank`;
CREATE TABLE IF NOT EXISTS `bank`
(
    `playerID` int(10) UNSIGNED NOT NULL,
    `itemID`   int(10) UNSIGNED NOT NULL,
    `slot`     int(5) UNSIGNED  NOT NULL DEFAULT 0,
    KEY (`playerID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `chat_logs`;
CREATE TABLE IF NOT EXISTS `chat_logs`
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

DROP TABLE IF EXISTS `curstats`;
CREATE TABLE IF NOT EXISTS `curstats`
(
    `playerID`       int(10) UNSIGNED    NOT NULL,
    `attack`     tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `defense`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `strength`   tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `hits`       tinyint(3) UNSIGNED NOT NULL DEFAULT 10,
    `ranged`     tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `prayer`     tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `magic`      tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `cooking`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `woodcut`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `fletching`  tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `fishing`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `firemaking` tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `crafting`   tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `smithing`   tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `mining`     tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `herblaw`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `agility`    tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `thieving`   tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    PRIMARY KEY (`playerID`),
    KEY `playerID` (`playerID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `droplogs`;
CREATE TABLE IF NOT EXISTS `droplogs`
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

DROP TABLE IF EXISTS `experience`;
CREATE TABLE IF NOT EXISTS `experience`
(
    `playerID`       int(10) UNSIGNED NOT NULL,
    `attack`     int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `defense`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `strength`   int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `hits`       int(9) UNSIGNED  NOT NULL DEFAULT 4616,
    `ranged`     int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `prayer`     int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `magic`      int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `cooking`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `woodcut`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `fletching`  int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `fishing`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `firemaking` int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `crafting`   int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `smithing`   int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `mining`     int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `herblaw`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `agility`    int(9) UNSIGNED  NOT NULL DEFAULT 0,
    `thieving`   int(9) UNSIGNED  NOT NULL DEFAULT 0,
    PRIMARY KEY (`playerID`),
    KEY `playerID` (`playerID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `friends`;
CREATE TABLE IF NOT EXISTS `friends`
(
    `playerID`   int(10) UNSIGNED    NOT NULL,
    `friend`     bigint(19) UNSIGNED NOT NULL,
    `friendName` varchar(12)         NOT NULL,
    `dbid`       int(10)             NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`),
    KEY `friend` (`friend`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `game_reports`;
CREATE TABLE IF NOT EXISTS `game_reports`
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

DROP TABLE IF EXISTS `generic_logs`;
CREATE TABLE IF NOT EXISTS `generic_logs`
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

DROP TABLE IF EXISTS `ignores`;
CREATE TABLE IF NOT EXISTS `ignores`
(
    `playerID` int(10) UNSIGNED    NOT NULL,
    `ignore`   bigint(19) UNSIGNED NOT NULL,
    `dbid`     int(10)             NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`),
    KEY `ignore` (`ignore`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `invitems`;
CREATE TABLE IF NOT EXISTS `invitems`
(
    `playerID` int(10) UNSIGNED    NOT NULL,
    `itemID`   int(10) UNSIGNED    NOT NULL,
    `slot`     int(5) UNSIGNED     NOT NULL,
    KEY (`playerID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `live_feeds`;
CREATE TABLE IF NOT EXISTS `live_feeds`
(
    `id`       int(10)          NOT NULL AUTO_INCREMENT,
    `username` varchar(12)      NOT NULL,
    `message`  varchar(165)     NOT NULL,
    `time`     int(10) UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `logins`;
CREATE TABLE IF NOT EXISTS `logins`
(
    `playerID` int(10) UNSIGNED NOT NULL,
    `time`     int(5) UNSIGNED  NOT NULL,
    `ip`       varchar(255)     NOT NULL DEFAULT '0.0.0.0',
    `dbid`     int(10)          NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`),
    KEY `ip` (`ip`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `npckills`;
CREATE TABLE IF NOT EXISTS `npckills`
(
    `ID`        int(10) NOT NULL AUTO_INCREMENT,
    `npcID`     int(10) DEFAULT NULL,
    `playerID`  int(10) DEFAULT NULL,
    `killCount` int(10) DEFAULT 0,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

DROP TABLE IF EXISTS `players`;
CREATE TABLE IF NOT EXISTS `players`
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
    `cameraauto`        tinyint(1) UNSIGNED          DEFAULT 1,
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
    `npc_kills`         INT(10)          NOT NULL    DEFAULT 0,
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
  
ALTER TABLE `players`
ALTER `cameraauto` SET DEFAULT 1;

DROP TABLE IF EXISTS `player_cache`;
CREATE TABLE IF NOT EXISTS `player_cache`
(
    `playerID` int(10) UNSIGNED NOT NULL,
    `type`     tinyint(1)       NOT NULL,
    `key`      varchar(32)      NOT NULL,
    `value`    varchar(150)     NOT NULL,
    `dbid`     int(10)          NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `player_change_recovery`;
CREATE TABLE IF NOT EXISTS `player_change_recovery`
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

DROP TABLE IF EXISTS `player_contact_details`;
CREATE TABLE IF NOT EXISTS `player_contact_details`
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

DROP TABLE IF EXISTS `player_recovery`;
CREATE TABLE IF NOT EXISTS `player_recovery`
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

DROP TABLE IF EXISTS `player_security_changes`;
CREATE TABLE IF NOT EXISTS `player_security_changes`
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

DROP TABLE IF EXISTS `private_message_logs`;
CREATE TABLE IF NOT EXISTS `private_message_logs`
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

DROP TABLE IF EXISTS `quests`;
CREATE TABLE IF NOT EXISTS `quests`
(
    `dbid`     int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `playerID` int(10) UNSIGNED NOT NULL,
    `id`       int(10) DEFAULT NULL,
    `stage`    int(10) DEFAULT NULL,
    PRIMARY KEY (`dbid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `recovery_questions`;
CREATE TABLE IF NOT EXISTS `recovery_questions`
(
    `questionID` int(10) UNSIGNED NOT NULL,
    `question`   varchar(256)     NOT NULL DEFAULT '',
    PRIMARY KEY (`questionID`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

INSERT INTO `recovery_questions` (`questionID`, `question`)
VALUES (1, 'Where were you born?'),
       (2, 'What was your first teacher\'s name?'),
       (3, 'What is your father\'s middle name?'),
       (4, 'Who was your first best friend?'),
       (5, 'What is your favourite vacation spot?'),
       (6, 'What is your mother\'s middle name?'),
       (7, 'What was your first pet\'s name?'),
       (8, 'What was the name of your first school?'),
       (9, 'What is your mother\'s maiden name?'),
       (10, 'Who was your first boyfriend/girlfriend?'),
       (11, 'What was the first computer game you purchased?'),
       (12, 'Who is your favourite actor/actress?'),
       (13, 'Who is your favourite author?'),
       (14, 'Who is your favourite musician?'),
       (15, 'Who is your favourite cartoon character?'),
       (16, 'What is your favourite book?'),
       (17, 'What is your favourite food?'),
       (18, 'What is your favourite movie?');

DROP TABLE IF EXISTS `recovery_attempts`;
CREATE TABLE IF NOT EXISTS `recovery_attempts`
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

DROP TABLE IF EXISTS `staff_logs`;
CREATE TABLE IF NOT EXISTS `staff_logs`
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

DROP TABLE IF EXISTS `trade_logs`;
CREATE TABLE IF NOT EXISTS `trade_logs`
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


/*
 * New server (non-player) tables section
 * The queries performed in the section will add new tables and drop any that already exist
 */

DROP TABLE IF EXISTS `objects`;
CREATE TABLE IF NOT EXISTS `objects`
(
    `x`         int(10) NOT NULL,
    `y`         int(10) NOT NULL,
    `id`        int(10) NOT NULL,
    `direction` int(10) NOT NULL,
    `type`      int(10) NOT NULL,
    `d_id`      int(11) NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`d_id`),
    UNIQUE KEY `d_id` (`d_id`)
) ENGINE = MyISAM
  AUTO_INCREMENT = 28954
  DEFAULT CHARSET = latin1;

DROP TABLE IF EXISTS `npclocs`;
CREATE TABLE IF NOT EXISTS `npclocs`
(
    `id`     int(10) DEFAULT NULL,
    `startX` int(10) DEFAULT NULL,
    `minX`   int(10) DEFAULT NULL,
    `maxX`   int(10) DEFAULT NULL,
    `startY` int(10) DEFAULT NULL,
    `minY`   int(10) DEFAULT NULL,
    `maxY`   int(10) DEFAULT NULL,
    `dbid`   int(11) NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`dbid`),
    UNIQUE KEY `fsdf` (`dbid`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 7551
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `grounditems`;
CREATE TABLE IF NOT EXISTS `grounditems`
(
    `id`      int(10) DEFAULT NULL,
    `x`       int(10) DEFAULT NULL,
    `y`       int(10) DEFAULT NULL,
    `amount`  int(10) DEFAULT NULL,
    `respawn` int(10) DEFAULT NULL,
    `idx`     int(10) NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`idx`),
    KEY `idx` (`idx`)
) ENGINE = MyISAM
  AUTO_INCREMENT = 1112
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `itemstatuses`;
CREATE TABLE IF NOT EXISTS `itemstatuses`
(
    `itemID`     int(10) UNSIGNED    NOT NULL,
    `catalogID`  int(10) UNSIGNED    NOT NULL,
    `amount`     int(10) UNSIGNED    NOT NULL DEFAULT 1,
    `noted`      tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
    `wielded`    tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
    `durability` int(5) UNSIGNED     NOT NULL DEFAULT 0,
    PRIMARY KEY (`itemID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
  