SELECT 
	`chat_name`, 
	`chat_message`,
	COUNT(`chat_message`) AS Frequency,
	`server_id`
FROM `mc_chat`
GROUP BY
	`chat_name`,
	`chat_message`, 
	`server_id`
ORDER BY
	Frequency DESC