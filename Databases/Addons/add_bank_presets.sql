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