SELECT
	DISTINCT `killerabbit`.`mc_servers`.`id`,
	COUNT(DISTINCT `mc_transition`.`player`) AS PlayerCount,
	`killerabbit`.`mc_servers`.`title`
FROM
	`mc_transition`
INNER JOIN
	`killerabbit`.`mc_servers`
ON
	`killerabbit`.`mc_servers`.`id` = `mc_transition`.`server_id`
GROUP BY
	`mc_transition`.`server_id`

ORDER BY
	`mc_transition`.`server_id` ASC
LIMIT
	0, 60