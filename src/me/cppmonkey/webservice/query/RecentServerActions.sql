(SELECT `chat_name` AS 'name', `chat_message` AS 'msg', `chat_date` as 'timestamp' FROM `mc_chat` WHERE `server_id` = '%d')
UNION
(SELECT `player` AS 'name', `action` AS 'msg', `timestamp` AS 'timestamp' FROM `mc_transition` WHERE `server_id` = '%d')
ORDER BY `timestamp` DESC
LIMIT 0 , 30