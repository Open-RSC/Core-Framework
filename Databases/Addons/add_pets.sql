ALTER TABLE `openrsc_players`
    ADD IF NOT EXISTS `petfatigue` INT(10) NULL DEFAULT '0' AFTER `fatigue`;
    ADD IF NOT EXISTS `pets` INT(10) NOT NULL DEFAULT '0' AFTER `npc_kills`;