ALTER TABLE `curstats` CHANGE IF EXISTS `cur_runecraft` `runecraft` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `curstats` CHANGE IF EXISTS `cur_harvesting` `harvesting` tinyint(3) UNSIGNED NOT NULL DEFAULT 1;
ALTER TABLE `experience` CHANGE IF EXISTS `exp_runecraft` `runecraft` int(9) UNSIGNED  NOT NULL DEFAULT 0;
ALTER TABLE `experience` CHANGE IF EXISTS `exp_harvesting` `harvesting` int(9) UNSIGNED  NOT NULL DEFAULT 0;

RENAME TABLE `openrsc_expired_auctions` TO `expired_auctions`;
RENAME TABLE `openrsc_auctions` TO `auctions`;
RENAME TABLE `openrsc_bankpresets` TO `bankpresets`;
RENAME TABLE `openrsc_clan` TO `clan`;
RENAME TABLE `openrsc_clan_players` TO `clan_players`;
RENAME TABLE `openrsc_equipped` TO `equipped`;
RENAME TABLE `openrsc_npckills` TO `npckills`;