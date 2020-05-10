DROP TABLE IF EXISTS `openrsc_equipped`;
CREATE TABLE IF NOT EXISTS `openrsc_equipped`
(
    `playerID` int(10) UNSIGNED NOT NULL,
    `itemID`   int(10) UNSIGNED NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;