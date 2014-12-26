UPDATE `mc_servers`,
       (SELECT DISTINCT `all`.`server_id`,
                        Max(`all`.`timestamp`) AS `timestamp`
        FROM   ((SELECT DISTINCT `server_id`,
                                 Max(`timestamp`) AS `timestamp`
                 FROM   `mc_transition`
                 WHERE  1
                 GROUP  BY `server_id`)
                UNION DISTINCT
                (SELECT DISTINCT `server_id`,
                                 Max(`chat_date`) AS `timestamp`
                 FROM   `mc_chat`
                 WHERE  1
                 GROUP  BY `server_id`)) AS `all`
        WHERE  1
        GROUP  BY `server_id`) AS `au`
SET    `mc_servers`.`last_action` = `au`.`timestamp`
WHERE  `mc_servers`.`id` = `au`.`server_id`;