SELECT
	DISTINCT `player`,
	COUNT(`timestamp`) AS Connections,
	MIN(`mc_transition`.`timestamp`) AS FirstConnection,
	MAX(`mc_transition`.`timestamp`) AS LastConnection,
	`killerabbit`.`mc_servers`.`title`
FROM
	`mc_transition`
INNER JOIN
	`killerabbit`.`mc_servers`
ON
	`killerabbit`.`mc_servers`.`id` = `mc_transition`.`server_id`
WHERE
	`action` = 'CONNECT'
GROUP BY
	`mc_transition`.`player`,
	`mc_transition`.`server_id`

ORDER BY
	`mc_transition`.`server_id` ASC,
	`LastConnection` DESC
LIMIT
	0, 60