ALTER TABLE `curstats`
    ADD IF NOT EXISTS `harvesting` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `experience`
    ADD IF NOT EXISTS `harvesting` int(9) NOT NULL DEFAULT 0;
ALTER TABLE `maxstats`
    ADD IF NOT EXISTS `harvesting` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `capped_experience`
    ADD IF NOT EXISTS `harvesting` int(10) UNSIGNED;
