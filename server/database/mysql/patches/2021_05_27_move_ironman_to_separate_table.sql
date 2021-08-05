CREATE TABLE IF NOT EXISTS `_PREFIX_ironman`
(
    `playerID`             int(10) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `iron_man`             tinyint(2)       NOT NULL DEFAULT 0,
    `iron_man_restriction` tinyint(2)       NOT NULL DEFAULT 1,
    `hc_ironman_death`     tinyint(2)       NOT NULL DEFAULT 0
);

DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN END;
    INSERT INTO `_PREFIX_ironman` (`playerID`, `iron_man`, `iron_man_restriction`, `hc_ironman_death`)
    SELECT `id`, `iron_man`, `iron_man_restriction`, `hc_ironman_death` FROM `_PREFIX_players`;
    ALTER TABLE `players`
        DROP COLUMN `iron_man`,
        DROP COLUMN `iron_man_restriction`,
        DROP COLUMN `hc_ironman_death`;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;