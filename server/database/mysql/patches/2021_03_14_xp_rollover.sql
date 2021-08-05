ALTER TABLE `_PREFIX_experience`
    MODIFY COLUMN `attack` INT(9) NOT NULL DEFAULT 0;
ALTER TABLE `_PREFIX_experience`
    MODIFY COLUMN `defense` INT(9) NOT NULL DEFAULT 0;
ALTER TABLE `_PREFIX_experience`
    MODIFY COLUMN `strength` INT(9) NOT NULL DEFAULT 0;
ALTER TABLE `_PREFIX_experience`
    MODIFY COLUMN `hits` INT(9) NOT NULL DEFAULT 0;
ALTER TABLE `_PREFIX_experience`
    MODIFY COLUMN `cooking` INT(9) NOT NULL DEFAULT 0;
ALTER TABLE `_PREFIX_experience`
    MODIFY COLUMN `firemaking` INT(9) NOT NULL DEFAULT 0;
ALTER TABLE `_PREFIX_experience`
    MODIFY COLUMN `crafting` INT(9) NOT NULL DEFAULT 0;
ALTER TABLE `_PREFIX_experience`
    MODIFY COLUMN `smithing` INT(9) NOT NULL DEFAULT 0;
ALTER TABLE `_PREFIX_experience`
    MODIFY COLUMN `mining` INT(9) NOT NULL DEFAULT 0;
ALTER TABLE `_PREFIX_experience`
    MODIFY COLUMN `herblaw` INT(9) NOT NULL DEFAULT 0;
ALTER TABLE `_PREFIX_experience`
    MODIFY COLUMN `thieving` INT(9) NOT NULL DEFAULT 0;

DROP PROCEDURE IF EXISTS `modifyIfExists`;
DELIMITER //
CREATE PROCEDURE modifyIfExists()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    ALTER TABLE `_PREFIX_experience`
        MODIFY COLUMN `prayer` INT(9) NOT NULL DEFAULT 0;
    ALTER TABLE `_PREFIX_experience`
        MODIFY COLUMN `magic` INT(9) NOT NULL DEFAULT 0;
    ALTER TABLE `_PREFIX_experience`
        MODIFY COLUMN `woodcut` INT(9) NOT NULL DEFAULT 0;
    ALTER TABLE `_PREFIX_experience`
        MODIFY COLUMN `fishing` INT(9) NOT NULL DEFAULT 0;
    ALTER TABLE `_PREFIX_experience`
        MODIFY COLUMN `fletching` INT(9) NOT NULL DEFAULT 0;
    ALTER TABLE `_PREFIX_experience`
        MODIFY COLUMN `agility` INT(9) NOT NULL DEFAULT 0;
    ALTER TABLE `_PREFIX_experience`
        MODIFY COLUMN `runecraft` INT(9) NOT NULL DEFAULT 0;
    ALTER TABLE `_PREFIX_experience`
        MODIFY COLUMN `harvesting` INT(9) NOT NULL DEFAULT 0;
END //
DELIMITER ;
CALL `modifyIfExists`();
DROP PROCEDURE `modifyIfExists`;

CREATE TABLE IF NOT EXISTS `_PREFIX_maxstats`
(
    `playerID`   int(10) UNSIGNED    NOT NULL PRIMARY KEY,
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
    `runecraft`  tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
    `harvesting` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `_PREFIX_capped_experience`
(
    `playerID`   int(10) UNSIGNED    NOT NULL PRIMARY KEY,
    `attack`     int(10) UNSIGNED,
    `defense`    int(10) UNSIGNED,
    `strength`   int(10) UNSIGNED,
    `hits`       int(10) UNSIGNED,
    `ranged`     int(10) UNSIGNED,
    `prayer`     int(10) UNSIGNED,
    `magic`      int(10) UNSIGNED,
    `cooking`    int(10) UNSIGNED,
    `woodcut`    int(10) UNSIGNED,
    `fletching`  int(10) UNSIGNED,
    `fishing`    int(10) UNSIGNED,
    `firemaking` int(10) UNSIGNED,
    `crafting`   int(10) UNSIGNED,
    `smithing`   int(10) UNSIGNED,
    `mining`     int(10) UNSIGNED,
    `herblaw`    int(10) UNSIGNED,
    `agility`    int(10) UNSIGNED,
    `thieving`   int(10) UNSIGNED,
    `runecraft`  int(10) UNSIGNED,
    `harvesting` int(10) UNSIGNED
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

