# Remaining days on subscription

SELECT
	`player`,
	`startdate` + INTERVAL `duration` MONTH AS `Expiration date`,
	DATEDIFF(`startdate` + INTERVAL `duration` MONTH, NOW()) AS `days remaining`
FROM
	`mc_subscription`
WHERE
	`expired` = 0