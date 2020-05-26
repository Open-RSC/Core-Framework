ALTER TABLE `players`
    ADD IF NOT EXISTS `iron_man` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
    ADD IF NOT EXISTS `iron_man_restriction` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
    ADD IF NOT EXISTS `hc_ironman_death` tinyint(1) UNSIGNED NOT NULL DEFAULT 0;