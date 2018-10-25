START TRANSACTION;

--Arcenia roots
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(1284, 11, 3327, 1, 30);
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(1284, 9, 3325, 1, 30);
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(1284, 5, 3329, 1, 30);

--Stone Tablet
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(1174, 11, 3348, 1, 30);

--Skulls
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(27, 8, 3353, 1, 30);
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(27, 8, 3354, 1, 30);
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(27, 6, 3355, 1, 30);
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(27, 17, 3353, 1, 30);
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(27, 17, 3355, 1, 30);

--Bronze Pickaxes
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(156, 25, 3347, 1, 60);
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(156, 28, 3347, 1, 60);
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(156, 30, 3345, 1, 60);
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(156, 30, 3346, 1, 60);

--Buckets
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(21, 22, 3343, 1, 60);
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(21, 23, 3342, 1, 60);
INSERT INTO openrsc_grounditems(id, x, y, amount, respawn)
VALUES(21, 24, 3341, 1, 60);

COMMIT;