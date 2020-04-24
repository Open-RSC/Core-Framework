-- CABBAGE -- UNCOMMENT FOR RUNNING ON CABBAGE!!!!!!!!!!!!
ALTER TABLE `openrsc_equipped` CHANGE `id` `itemID` int(10) UNSIGNED NOT NULL;
ALTER TABLE `openrsc_equipped` DROP COLUMN `amount`;
ALTER TABLE `openrsc_equipped` DROP COLUMN `dbid`;
ALTER TABLE `openrsc_curstats` ADD COLUMN IF NOT EXISTS `cur_harvesting` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `openrsc_experience` ADD COLUMN IF NOT EXISTS `exp_harvesting` int(9) UNSIGNED  NOT NULL DEFAULT 0;
