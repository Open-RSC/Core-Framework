START TRANSACTION;
  DELETE FROM openrsc_invitems
  WHERE id = 1006;

  DELETE FROM openrsc_bank
  WHERE id = 1006;
COMMIT;