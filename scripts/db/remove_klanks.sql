USE openrsc_game;

START TRANSACTION;

  DELETE FROM openrsc_bank
  WHERE id = 1006 AND playerID IN (
	  SELECT p.id FROM openrsc_players AS p
		  INNER JOIN openrsc_quests AS q ON p.id = q.playerID
		  WHERE Q.id = 41 AND q.stage <> -1
  );

  DELETE FROM openrsc_invitems
  WHERE id = 1006 AND playerID IN (
	  SELECT p.id FROM openrsc_players AS p
		  INNER JOIN openrsc_quests AS q ON p.id = q.playerID
		  WHERE Q.id = 41 AND q.stage <> -1
  );

COMMIT;