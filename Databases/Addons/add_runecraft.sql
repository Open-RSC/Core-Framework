ALTER TABLE `curstats`
    ADD IF NOT EXISTS `runecraft` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `experience`
    ADD IF NOT EXISTS `runecraft` int(9) NOT NULL DEFAULT 0;
ALTER TABLE `maxstats`
    ADD IF NOT EXISTS `runecraft` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `capped_experience`
    ADD IF NOT EXISTS `runecraft` int(10) UNSIGNED;
