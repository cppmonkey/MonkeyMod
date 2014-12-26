CREATE DEFINER=`CppMonkey`@`cppmonkey.net` PROCEDURE `GrantPlayerPermission`(IN `serverId` INT, IN `playerId` INT, IN `grantedById` INT, IN `permission` VARCHAR(16), IN `value` VARCHAR(16))
	LANGUAGE SQL
	NOT DETERMINISTIC
	CONTAINS SQL
	SQL SECURITY INVOKER
	COMMENT ''
BEGIN
	INSERT INTO `mc_permissions` (`player_id`, `server_id`, `granted_by_id`, `permission`, `value`)
	VALUES (`playerId`, `serverId`, `grantedById`, `permission`, `value`);
END

CREATE DEFINER=`CppMonkey`@`cppmonkey.net` PROCEDURE `LogPlayerAction`(IN `serverId` INT, IN `playerId` INT, IN `action` INT, IN `time` DATETIME)
	LANGUAGE SQL
	NOT DETERMINISTIC
	CONTAINS SQL
	SQL SECURITY INVOKER
	COMMENT ''
BEGIN
  INSERT INTO `mc_transition` (`server_id`, `player_id`, `action`, `last_action`)
    VALUES(`serverId`, `playerId`, `action`, `time`);
END

CREATE DEFINER=`CppMonkey`@`cppmonkey.net` PROCEDURE `LogPlayerChat`(IN `sid` INT, IN `pid` INT, IN `msg` TEXT, IN `time` DATETIME)
	LANGUAGE SQL
	NOT DETERMINISTIC
	CONTAINS SQL
	SQL SECURITY INVOKER
	COMMENT ''
BEGIN
  INSERT INTO `mc_chat` (`server_id`, `player_id`, `chat_message`, `chat_date`)
    VALUES ( 'sid', 'pid', 'msg', `time`);
END

CREATE DEFINER=`CppMonkey`@`cppmonkey.net` PROCEDURE `NewPlayer`(IN `name` CHAR(32))
	LANGUAGE SQL
	NOT DETERMINISTIC
	CONTAINS SQL
	SQL SECURITY INVOKER
	COMMENT ''
BEGIN
  INSERT INTO `mc_players` (`player_name`)
    VALUES ( `name` );
END