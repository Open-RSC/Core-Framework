DROP TABLE IF EXISTS `clan`;
CREATE TABLE IF NOT EXISTS `clan`
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

DROP TABLE IF EXISTS `clan_players`;
CREATE TABLE IF NOT EXISTS `clan_players`
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