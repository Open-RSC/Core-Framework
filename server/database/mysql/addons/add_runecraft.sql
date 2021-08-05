ALTER TABLE `curstats`
    ADD `runecraft` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `experience`
    ADD`runecraft` int(9) NOT NULL DEFAULT 0;
ALTER TABLE `maxstats`
    ADD `runecraft` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `capped_experience`
    ADD `runecraft` int(10) UNSIGNED;
