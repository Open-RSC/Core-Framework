ALTER TABLE `openrsc_curstats`
    ADD IF NOT EXISTS `cur_harvesting` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `openrsc_experience`
    ADD IF NOT EXISTS `exp_harvesting` int(9) UNSIGNED NOT NULL DEFAULT 0;