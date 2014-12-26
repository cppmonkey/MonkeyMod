UPDATE `mc_players`,
       (SELECT DISTINCT `all`.`player_id`,
                        Max(`all`.`timestamp`) AS `timestamp`
        FROM   ((SELECT DISTINCT `player_id`,
                                 Max(`timestamp`) AS `timestamp`
                 FROM   `mc_transition`
                 WHERE  1
                 GROUP  BY `player_id`)
                UNION DISTINCT
                (SELECT DISTINCT `player_id`,
                                 Max(`chat_date`) AS `timestamp`
                 FROM   `mc_chat`
                 WHERE  1
                 GROUP  BY `player_id`)) AS `all`
        WHERE  1
        GROUP  BY `player_id`) AS `au`
SET    `mc_players`.`last_action` = `au`.`timestamp`
WHERE  `mc_players`.player_id = `au`.`player_id`;