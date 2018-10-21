USE openrsc_game;

START TRANSACTION;
    DELETE FROM openrsc_bank WHERE id = 593;
    DELETE FROM openrsc_bank WHERE id = 1789;
    DELETE FROM openrsc_invitems WHERE id = 593;
    DELETE FROM openrsc_invitems WHERE id = 1789;
COMMIT;