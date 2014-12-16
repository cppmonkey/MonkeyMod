SELECT
	DISTINCT `mc_transition`.`player`, `mc_players`.`player_id`
FROM
	`mc_transition` 
INNER JOIN
	`mc_players`
ON
	`mc_transition`.`player` = `mc_players`.`player_name`

WHERE `server_id` =89
LIMIT 0 , 30