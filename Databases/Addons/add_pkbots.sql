ALTER TABLE `openrsc_npcdef`
    ADD IF NOT EXISTS `pkBot` tinyint(1) DEFAULT 0;