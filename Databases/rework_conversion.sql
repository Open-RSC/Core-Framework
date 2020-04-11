-- Use this file to place all items to bank and add new inventory layout.

DROP TABLE IF EXISTS `openrsc_itemstatuses`;
CREATE TABLE IF NOT EXISTS `openrsc_itemstatuses` (
    `itemID` int(10) UNSIGNED    NOT NULL AUTO_INCREMENT,
    `catalogID`       int(10) UNSIGNED    NOT NULL,
    `amount`   int(10) UNSIGNED NOT NULL DEFAULT 1,
    `noted`  tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
    `durability`     int(5) UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (`itemID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

SELECT '1. openrsc_itemstatuses created.' AS '';

INSERT INTO `openrsc_bank` (`playerID`, `id`, `amount`)
SELECT `playerID`, `id`, `amount`
FROM `openrsc_invitems`;

SELECT '2. Items moved from openrsc_invitems to openrsc_bank.' AS '';

INSERT INTO `openrsc_itemstatuses` (`catalogID`, `amount`)
SELECT `id`, `amount`
FROM `openrsc_bank`;

SELECT '3. Items updated in openrsc_itemstatuses.' AS '';

DELETE FROM `openrsc_invitems`;

ALTER TABLE `openrsc_invitems`
DROP COLUMN `dbid`;

ALTER TABLE `openrsc_invitems`
DROP COLUMN `amount`;

ALTER TABLE `openrsc_invitems`
DROP COLUMN `id`;

ALTER TABLE `openrsc_invitems`
ADD COLUMN `itemID` int(10) UNSIGNED    NOT NULL;

-- CABBAGE -- UNCOMMENT FOR RUNNING ON CABBAGE!!!!!!!!!!!!
ALTER TABLE `openrsc_equipped` DROP COLUMN `id`;
ALTER TABLE `openrsc_equipped` DROP COLUMN `amount`;
ALTER TABLE `openrsc_equipped` DROP COLUMN `dbid`;
ALTER TABLE `openrsc_equipped` ADD COLUMN `itemID` int(10) UNSIGNED NOT NULL;
