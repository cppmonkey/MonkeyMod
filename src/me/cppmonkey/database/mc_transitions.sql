CREATE TABLE IF NOT EXISTS `mc_transitions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` int(11) NOT NULL,
  `server_id` int(11) NOT NULL,
  `action` enum('CONNECT','DISCONNECT','IGNITE','CHEST','TOWER','MODIFY','UPDATE','BUILD') NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `server_id` (`server_id`),
  KEY `player_id` (`player_id`)
) ENGINE=InnoDB;


ALTER TABLE `mc_transitions`
  ADD FOREIGN KEY (`server_id`) REFERENCES `mc_servers` (`id`) ON DELETE CASCADE,
  ADD FOREIGN KEY (`player_id`) REFERENCES `mc_players` (`player_id`) ON DELETE CASCADE;

DROP TRIGGER IF EXISTS `TransitionUpdateAction`;
DELIMITER //
CREATE TRIGGER `TransitionUpdateAction` AFTER INSERT ON `mc_transitions`
 FOR EACH ROW BEGIN
  UPDATE `mc_players` SET `last_action` = NEW.`timestamp` WHERE `mc_players`.`id` =NEW.`player_id`;
  UPDATE `mc_servers` SET `last_action` = NEW.`timestamp` WHERE `mc_servers`.`id` =NEW.`server_id`;
END
//
DELIMITER ;