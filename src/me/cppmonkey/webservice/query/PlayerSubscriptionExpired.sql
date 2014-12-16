# Expired subscription?

UPDATE
	`mc_subscription`
SET
	`expired` = 1
WHERE
	`startdate` + INTERVAL `duration` MONTH < NOW()