UPDATE `mc_chat`
LEFT JOIN `mc_players`
ON `mc_chat`.`chat_name` = `mc_players`.`player_name`
SET `mc_chat`.`player_id` = `mc_players`.`player_id`;

UPDATE `mc_transition`
LEFT JOIN `mc_players`
ON `mc_transition`.`player` = `mc_players`.`player_name`
SET `mc_transition`.`player_id` = `mc_players`.`player_id`;