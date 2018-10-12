USE openrsc_game;

START TRANSACTION;
    DELETE FROM openrsc_bank WHERE id = 317;
    DELETE FROM openrsc_bank WHERE id = 1562;
    DELETE FROM openrsc_invitems WHERE id = 317;
    DELETE FROM openrsc_invitems WHERE id = 1562;
COMMIT;