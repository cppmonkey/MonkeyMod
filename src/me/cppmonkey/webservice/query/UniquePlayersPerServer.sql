SELECT
	DISTINCT `killerabbit`.`mc_servers`.`id`,
	COUNT(DISTINCT `tbl_transition`.`player`) AS PlayerCount,
	`killerabbit`.`mc_servers`.`title`
FROM
	`tbl_transition`
INNER JOIN
	`killerabbit`.`mc_servers`
ON
	`killerabbit`.`mc_servers`.`id` = `tbl_transition`.`server_id`
GROUP BY
	`tbl_transition`.`server_id`

ORDER BY
	`tbl_transition`.`server_id` ASC
LIMIT
	0, 60