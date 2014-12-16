SELECT
	DISTINCT `player`,
	COUNT(`timestamp`) AS Connections,
	MIN(`tbl_transition`.`timestamp`) AS FirstConnection,
	MAX(`tbl_transition`.`timestamp`) AS LastConnection,
	`killerabbit`.`mc_servers`.`title`
FROM
	`tbl_transition`
INNER JOIN
	`killerabbit`.`mc_servers`
ON
	`killerabbit`.`mc_servers`.`id` = `tbl_transition`.`server_id`
WHERE
	`action` = 'CONNECT'
GROUP BY
	`tbl_transition`.`player`,
	`tbl_transition`.`server_id`

ORDER BY
	`tbl_transition`.`server_id` ASC,
	`LastConnection` DESC
LIMIT
	0, 60