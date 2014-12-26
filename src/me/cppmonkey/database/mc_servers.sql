CREATE TABLE IF NOT EXISTS mc_servers (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(32) NOT NULL,
  `address` varchar(32) NOT NULL,
  `ipv4` varchar(15) NOT NULL,
  `ipv6` varchar(39) NOT NULL,
  `game_port` smallint(5) unsigned NOT NULL,
  `owner_id` int(11) NULL DEFAULT NULL COMMENT 'ID of the owner, if known',
  `last_action` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY owner_id (owner_id)
) ENGINE=InnoDB

ALTER TABLE `mc_servers`
  ADD FOREIGN KEY (`owner_id`) REFERENCES `mc_players` (`id`) ON DELETE CASCADE;