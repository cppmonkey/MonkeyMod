CREATE TABLE IF NOT EXISTS `mc_chat` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `player_id` INT(11) NOT NULL,
  `server_id` INT(11) NOT NULL,
  `message` TEXT NOT NULL,
  `timestamp` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `server_id` (`server_id`),
  INDEX `player_id` (`player_id`)
)ENGINE=InnoDB;

ALTER TABLE `mc_chat`
  ADD FOREIGN KEY (`player_id`) REFERENCES `mc_players` (`id`) ON DELETE CASCADE,
  ADD FOREIGN KEY (`server_id`) REFERENCES `mc_servers` (`id`) ON DELETE CASCADE;
  
DROP TRIGGER IF EXISTS `ChatUpdateAction`;
DELIMITER //
CREATE TRIGGER `ChatUpdateAction` AFTER INSERT ON `mc_chat`
 FOR EACH ROW BEGIN
  UPDATE `mc_players` SET `last_action` = NEW.`timestamp` WHERE `mc_players`.`id` =NEW.`player_id`;
  UPDATE `mc_servers` SET `last_action` = NEW.`timestamp` WHERE `mc_servers`.`id` =NEW.`server_id`;
END
//
DELIMITER ;