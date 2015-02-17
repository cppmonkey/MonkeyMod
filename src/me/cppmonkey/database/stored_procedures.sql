DELIMITER $$
DROP PROCEDURE IF EXISTS `GrantPlayerPermission` $$
CREATE PROCEDURE `GrantPlayerPermission`(IN `serverId` INT, IN `playerId` INT, IN `grantedById` INT, IN `permission` VARCHAR(16), IN `value` VARCHAR(16))
	LANGUAGE SQL
	NOT DETERMINISTIC
	CONTAINS SQL
	SQL SECURITY INVOKER
	COMMENT ''
BEGIN
	INSERT INTO `mc_permissions` (`player_id`, `server_id`, `granted_by_id`, `permission`, `value`)
	VALUES (`playerId`, `serverId`, `grantedById`, `permission`, `value`);
END;

DELIMITER $$
DROP PROCEDURE IF EXISTS `LogPlayerAction` $$
CREATE PROCEDURE `LogPlayerAction`(IN `serverId` INT, IN `playerId` INT, IN `action` INT, IN `time` DATETIME)
	LANGUAGE SQL
	NOT DETERMINISTIC
	CONTAINS SQL
	SQL SECURITY INVOKER
	COMMENT ''
BEGIN
  INSERT INTO `mc_transition` (`server_id`, `player_id`, `action`, `last_action`)
    VALUES(`serverId`, `playerId`, `action`, `time`);
END;

DELIMITER $$
DROP PROCEDURE IF EXISTS `LogPlayerChat` $$
CREATE PROCEDURE `LogPlayerChat`(IN `sid` INT, IN `pid` INT, IN `msg` TEXT, IN `time` DATETIME)
	LANGUAGE SQL
	NOT DETERMINISTIC
	CONTAINS SQL
	SQL SECURITY INVOKER
	COMMENT ''
BEGIN
  INSERT INTO `mc_chat` (`server_id`, `player_id`, `chat_message`, `chat_date`)
    VALUES ( 'sid', 'pid', 'msg', `time`);
END;

DELIMITER $$
DROP PROCEDURE IF EXISTS NewPlayer $$
CREATE PROCEDURE `NewPlayer`(IN `name` CHAR(32))
	LANGUAGE SQL
	NOT DETERMINISTIC
	CONTAINS SQL
	SQL SECURITY INVOKER
	COMMENT ''
BEGIN
  INSERT INTO `mc_players` (`player_name`)
    VALUES ( `name` );
END;

DELIMITER $$
DROP PROCEDURE IF EXISTS `GetServerHistory` $$
CREATE PROCEDURE `GetServerHistory`(IN `serverId` INT(11) UNSIGNED, IN _START INTEGER, IN _LIMIT INTEGER )
BEGIN
PREPARE STMT FROM "
  SELECT  `mc_players`.`name` AS  'name',  `mc_chat`.`message` AS  'msg',  `chat_date` AS  'timestamp'
    FROM  `mc_chat`
    LEFT JOIN  `mc_players` ON  `mc_chat`.`player_id` =  `mc_players`.`id`
    WHERE  `server_id` =  ?
  UNION
    SELECT  `mc_players`.`name` AS  'name',  `mc_transition`.`action` AS  'msg',  `mc_transition`.`timestamp` AS  'timestamp'
    FROM  `mc_transition`
    LEFT JOIN  `mc_players` ON  `mc_transition`.`player_id` =  `mc_players`.`id`
    WHERE  `server_id` =  ?
  ORDER BY `timestamp` DESC
  LIMIT ?, ?";
SET @SERVERID = `serverId`;
SET @START = _START;
SET @LIMIT = _LIMIT;

EXECUTE STMT USING @SERVERID, @SERVERID, @START, @LIMIT;
DEALLOCATE PREPARE STMT;
END;
