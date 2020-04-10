-- Use this file to place all items to bank and add new inventory layout.

CREATE TABLE IF NOT EXISTS `openrsc_itemstatuses` (
    `itemID` int(10) UNSIGNED    NOT NULL,
    `catalogID`       int(10) UNSIGNED    NOT NULL,
    `amount`   int(10) UNSIGNED NOT NULL DEFAULT 1,
    `noted`  tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
    `durability`     int(5) UNSIGNED     NOT NULL,
    PRIMARY KEY (`itemID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `openrsc_bank`
    (`playerID`, `id`, `amount`, `slot`)
SELECT (`playerID`, `id`, `amount`, 0)
FROM `openrsc_invitems`;

INSERT INTO `openrsc_itemstatuses`
    (`itemID`, `catalogID`, `amount`, `noted`, `durability`)
SELECT (row_number(), `id`, `amount`, 0, 0)

DELETE FROM `openrsc_invitems`;

ALTER TABLE `openrsc_invitems`
DROP COLUMN `dbid`;

ALTER TABLE `openrsc_invitems`
DROP COLUMN `amount`;

ALTER TABLE `openrsc_invitems`
DROP COLUMN `id`;

ALTER TABLE `openrsc_invitems`
ADD COLUMN `itemID` int(10) UNSIGNED    NOT NULL;

-- CABBAGE
ALTER TABLE `openrsc_equipped`
DROP COLUMN `id`;

ALTER TABLE `openrsc_equipped`
DROP COLUMN `amount`;

ALTER TABLE `openrsc_equipped`
DROP COLUMN `dbid`;

ALTER TABLE `openrsc_equipped`
ADD COLUMN `itemID` int(10) UNSIGNED    NOT NULL;
