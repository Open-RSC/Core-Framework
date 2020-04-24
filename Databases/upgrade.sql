/*
 * Clean up section
 * The queries performed in the section will remove depreciated tables and columns
 */

DROP TABLE IF EXISTS `openrsc_giveaway`;
DROP TABLE IF EXISTS `openrsc_name_changes`;
DROP TABLE IF EXISTS `openrsc_orders`;
DROP TABLE IF EXISTS `openrsc_achievement_task`;
DROP TABLE IF EXISTS `openrsc_achievement_status`;
DROP TABLE IF EXISTS `openrsc_achievement_reward`;
DROP TABLE IF EXISTS `openrsc_achievements`;
ALTER TABLE `openrsc_players` DELETE IF EXISTS `highscoreopt` tinyint(1) UNSIGNED NOT NULL DEFAULT 0;


/*
 * Upgrade section
 * The queries performed in the section will upgrade previous versions of the database tables
 */

