
-- Make arrows wieldable.
UPDATE `itemdef` SET `isWearable`=1, `wearableID`=1000, `wearSlot`=12, `requiredLevel`=1, `requiredSkillID`=4 WHERE `id` IN (
    11, 574, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 723
);
-- Make bolts wieldable
UPDATE `itemdef` SET `isWearable`=1, `wearableID`=1001, `wearSlot`=12, `requiredLevel`=1, `requiredSkillID`=4 WHERE `id` IN (
    190,  592, 786
);

-- Make pickaxes wieldable.
UPDATE `itemdef` SET `isWearable`=1, `wearableID`=16, `wearSlot`=4, `requiredLevel`=1, `requiredSkillID`=0, `weaponAimBonus`=0, `weaponPowerBonus`=0 WHERE `id`=156; -- Bronze
UPDATE `itemdef` SET `isWearable`=1, `wearableID`=16, `wearSlot`=4, `requiredLevel`=1, `requiredSkillID`=0, `weaponAimBonus`=7, `weaponPowerBonus`=5 WHERE `id`=1258; -- Iron
UPDATE `itemdef` SET `isWearable`=1, `wearableID`=16, `wearSlot`=4, `requiredLevel`=5, `requiredSkillID`=0, `weaponAimBonus`=11, `weaponPowerBonus`=8 WHERE `id`=1259; -- Steel
UPDATE `itemdef` SET `isWearable`=1, `wearableID`=16, `wearSlot`=4, `requiredLevel`=20, `requiredSkillID`=0, `weaponAimBonus`=16, `weaponPowerBonus`=12 WHERE `id`=1260; -- Mithril
UPDATE `itemdef` SET `isWearable`=1, `wearableID`=16, `wearSlot`=4, `requiredLevel`=30, `requiredSkillID`=0, `weaponAimBonus`=23, `weaponPowerBonus`=17 WHERE `id`=1261; -- Adamant
UPDATE `itemdef` SET `isWearable`=1, `wearableID`=16, `wearSlot`=4, `requiredLevel`=40, `requiredSkillID`=0, `weaponAimBonus`=36, `weaponPowerBonus`=26 WHERE `id`=1262; -- Rune

-- Custom appearance sprites.
UPDATE `itemdef` SET `appearanceID`=(case
    -- Axes
    when id=87 then 230
    when id=12 then 231
    when id=88 then 232
    when id=203 then 233
    when id=204 then 234
    when id=405 then 235
    when id=428 then 236
    -- Kite Shields
    when id=128 then 237
    when id=2 then 238
    when id=129 then 239
    when id=130 then 240
    when id=131 then 241
    when id=404 then 242
    when id=433 then 243
    when id=1278 then 244
    -- Dragon Medium Helmet
    when id=795 then 245
    -- Plated Skirts
    when id=214 then 246
    when id=215 then 247
    when id=225 then 248
    when id=226 then 249
    when id=227 then 250
    when id=406 then 251
    when id=434 then 252
    -- Bows
    when id=188 then 253
    when id=189 then 253
    when id=648 then 254
    when id=649 then 254
    when id=650 then 255
    when id=651 then 255
    when id=652 then 256
    when id=653 then 256
    when id=654 then 257
    when id=655 then 257
    when id=656 then 258
    when id=657 then 258
    -- Short Swords
    when id=66 then 259
    when id=1 then 260
    when id=67 then 261
    when id=68 then 262
    when id=69 then 263
    when id=397 then 264
    when id=424 then 265
    -- Daggers
    when id=62 then 266
    when id=28 then 267
    when id=63 then 268
    when id=64 then 269
    when id=65 then 270
    when id=396 then 271
    when id=423 then 272
    -- Poisoned Daggers
    when id=560 then 273
    when id=559 then 274
    when id=561 then 275
    when id=562 then 276
    when id=563 then 277
    when id=564 then 278
    when id=565 then 279
    -- 2h Swords
    when id=76 then 280
    when id=77 then 281
    when id=78 then 282
    when id=79 then 283
    when id=80 then 284
    when id=81 then 285
    when id=426 then 286
    -- Spears
    when id=1135 then 388
    when id=1136 then 389
    when id=1137 then 390
    when id=1138 then 391
    when id=1139 then 392
    when id=1140 then 393
    -- Necklaces
    when id=289 then 405
    when id=302 then 406
    when id=290 then 407
    when id=303 then 408
    when id=291 then 409
    when id=304 then 410
    when id=292 then 411
    when id=305 then 412
    when id=544 then 413
    when id=597 then 414
    when id=1194 then 415
    when id=852 then 416
    when id=721 then 417
    when id=726 then 418
    when id=782 then 419
    when id=385 then 420
    when id=1028 then 421
	-- Staves
	when id=101 then 422
	when id=102 then 423
	when id=103 then 424
	when id=197 then 425
    -- Battlestaves
    when id=617 then 422
    when id=616 then 423
    when id=618 then 424
    when id=615 then 425
	-- Enchanted Battlestaves
	when id=684 then 422
	when id=683 then 423
	when id=685 then 424
	when id=682 then 425
	-- Pickaxes
	when id=156 then 434
	when id=1258 then 435
	when id=1259 then 436
	when id=1260 then 437
	when id=1261 then 438
	when id=1262 then 439
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
	684, 683, 685, 682,
	-- Pickaxes
	156, 1258, 1259, 1260, 1261, 1262
);
REPLACE INTO `grounditems` (`id`, `x`, `y`, `amount`, `respawn`, `idx`)
VALUES (1362, 309, 3429, 1, 95, 1117);
