ALTER TABLE `curstats`
    ADD `harvesting` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `experience`
    ADD `harvesting` int(9) NOT NULL DEFAULT 0;
ALTER TABLE `maxstats`
    ADD `harvesting` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `capped_experience`
    ADD `harvesting` int(10) UNSIGNED;
