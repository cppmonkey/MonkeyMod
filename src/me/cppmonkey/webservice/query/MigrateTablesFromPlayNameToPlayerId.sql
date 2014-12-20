UPDATE `mc_chat`
LEFT JOIN `mc_players`
ON `mc_chat`.`chat_name` = `mc_players`.`player_name`
SET `mc_chat`.`player_id` = `mc_players`.`player_id`;

INSERT INTO `mc_players` (`player_name`)
	SELECT DISTINCT
		`chat_name`
	FROM `mc_chat`
	WHERE
		`player_id` = '0';

UPDATE `mc_transition`
LEFT JOIN `mc_players`
ON `mc_transition`.`player` = `mc_players`.`player_name`
SET `mc_transition`.`player_id` = `mc_players`.`player_id`;

INSERT INTO `mc_players` (`player_name`)
	SELECT DISTINCT
		`player`
	FROM `mc_transition`
	WHERE
		`player_id` = '0';

UPDATE `mc_chat`
LEFT JOIN `mc_players`
ON `mc_chat`.`chat_name` = `mc_players`.`player_name`
SET `mc_chat`.`player_id` = `mc_players`.`player_id`;

UPDATE `mc_transition`
LEFT JOIN `mc_players`
ON `mc_transition`.`player` = `mc_players`.`player_name`
SET `mc_transition`.`player_id` = `mc_players`.`player_id`;

ALTER TABLE `mc_transition` DROP COLUMN `player`;
ALTER TABLE `mc_chat` DROP COLUMN `chat_name`;
ALTER TABLE `mc_serverPackageStart` DROP COLUMN `update_forced`;