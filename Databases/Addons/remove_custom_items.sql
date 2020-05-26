DELETE FROM `itemdef` WHERE `itemdef`.`id` > 1289;


-- Make arrows no longer wieldable
UPDATE `itemdef` SET `isWearable`=0, `wearableID`=0, `wearSlot`=-1, `requiredLevel`=0, `requiredSkillID`=-1 WHERE `id` IN (
    11, 190, 574, 592, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 723, 786
);

-- Make pickaxes no longer wieldable.
UPDATE `itemdef` SET `isWearable`=0, `appearanceID`=0, `wearableID`=0, `wearSlot`=-1, `requiredLevel`=0, `requiredSkillID`=-1, `weaponAimBonus`=0, `weaponPowerBonus`=0 WHERE `id` IN (
	156, 1258, 1259, 1260, 1261, 1262
);

-- Custom appearance sprites removal
UPDATE `itemdef` SET `appearanceID`=(case
    -- Axes
    when id=87 then 109
    when id=12 then 110
    when id=88 then 111
    when id=203 then 112
    when id=204 then 113
    when id=405 then 114
    when id=428 then 115
    -- Kite Shields
    when id=128 then 98
    when id=2 then 99
    when id=129 then 100
    when id=130 then 101
    when id=131 then 102
    when id=404 then 103
    when id=433 then 104
    when id=1278 then 225
    -- Dragon Medium Helmet
    when id=795 then 179
    -- Plated Skirts
    when id=214 then 246
    when id=215 then 93
    when id=225 then 94
    when id=226 then 95
    when id=227 then 96
    when id=406 then 97
    when id=434 then 159
    -- Bows
    when id=188 then 108
    when id=189 then 108
    when id=648 then 108
    when id=649 then 108
    when id=650 then 108
    when id=651 then 108
    when id=652 then 108
    when id=653 then 108
    when id=654 then 108
    when id=655 then 108
    when id=656 then 108
    when id=657 then 108
    -- Short Swords
    when id=66 then 48
    when id=1 then 49
    when id=67 then 50
    when id=68 then 51
    when id=69 then 52
    when id=397 then 53
    when id=424 then 54
    -- Daggers
    when id=62 then 48
    when id=28 then 49
    when id=63 then 50
    when id=64 then 51
    when id=65 then 52
    when id=396 then 53
    when id=423 then 54
    -- Poisoned Daggers
    when id=560 then 48
    when id=559 then 49
    when id=561 then 50
    when id=562 then 51
    when id=563 then 53
    when id=564 then 52
    when id=565 then 54
    -- 2h Swords
    when id=76 then 48
    when id=77 then 49
    when id=78 then 50
    when id=79 then 51
    when id=80 then 52
    when id=81 then 285
    when id=426 then 54
    -- Spears
    when id=1135 then 181
    when id=1136 then 181
    when id=1137 then 181
    when id=1138 then 181
    when id=1139 then 181
    when id=1140 then 220
    -- Necklaces
    when id=289 then 81
    when id=302 then 81
    when id=290 then 81
    when id=303 then 81
    when id=291 then 81
    when id=304 then 81
    when id=292 then 81
    when id=305 then 81
    when id=544 then 81
    when id=597 then 81
    when id=1194 then 80
    when id=852 then 80
    when id=721 then 172
    when id=726 then 80
    when id=782 then 80
    when id=385 then 80
    when id=1028 then 80
	-- Staves
	when id=101 then 123
	when id=102 then 123
	when id=103 then 123
	when id=197 then 123
    -- Battlestaves
    when id=617 then 123
    when id=616 then 123
    when id=618 then 123
    when id=615 then 123
	-- Enchanted Battlestaves
	when id=684 then 123
	when id=683 then 123
	when id=685 then 123
	when id=682 then 123
end) WHERE `id` IN (
    -- Axes
    87, 12, 88, 203, 204, 405, 428,
    -- Kite Shields
    128, 2, 129, 130, 131, 404, 433, 1278,
    -- Dragon Medium Helmet
    795,
    -- Plated Skirts
    214, 215, 225, 226, 227, 406, 434,
    -- Bows
    188, 189, 648, 649, 650, 651, 652, 653, 654, 655, 656, 657,
    -- Short Swords
    66, 1, 67, 68, 69, 397, 424,
    -- Daggers
    62, 28, 63, 64, 65, 396, 423,
    -- Poisoned Daggers
    560, 559, 561, 562, 563, 564, 565,
    -- 2h Swords
    76, 77, 78, 79, 80, 81, 426,
    -- Spears
    1135, 1136, 1137, 1138, 1139, 1140,
    -- Necklaces
    289, 302, 290, 303, 291, 304, 292, 305, 544, 597, 1194, 852, 721, 726, 782, 385, 1028,
	-- Staves
	101, 102, 103, 197,
    -- Battlestaves
    617, 616, 618, 615,
	-- Enchanted Battlestaves
	684, 683, 685, 682
);