player=nibbles
docker exec -i mysql mysql -uroot -proot -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '1' WHERE username = '$player';"
