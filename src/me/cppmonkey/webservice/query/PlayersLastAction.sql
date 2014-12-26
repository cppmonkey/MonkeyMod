SELECT DISTINCT `all`.`player_id`, MAX(`all`.`timestamp`) FROM
((SELECT DISTINCT `player_id`, MAX(`timestamp`) AS `timestamp` FROM `mc_transition` WHERE 1
GROUP BY `player_id`
)
UNION DISTINCT
(SELECT DISTINCT `player_id`, MAX(`chat_date`) AS `timestamp` FROM `mc_chat` WHERE 1
GROUP BY `player_id`
)) AS `all`
WHERE 1
GROUP BY `player_id`
ORDER BY `player_id`