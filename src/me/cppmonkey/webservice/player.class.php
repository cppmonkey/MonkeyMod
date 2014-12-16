<?php

class Player{

	var $m_Name;
	var $m_Group;
	var $m_Admin = false;
	var $m_id;

	function Player( $name )
	{
		$this->m_Name = $name;
		if( isset($_GET['vip']) )
		{
			if( $_GET['vip'] == "true" || $_GET['vip'] == "on" )
			$this->m_Group = "vip";
		}

		if( isset( $_GET['admin'] ))
		{
			if( $_GET['admin'] == "true" || $_GET['vip'] == "on" )
			{
				$this->m_Admin = true;
			}
		}
		
		$this->GetPlayerId();
		
		if( $this->m_id == -1 )
		{
			// Create new Player
			Global $dblink;
		
			$query = sprintf( "
					INSERT INTO `killerabbit`.`mc_players` (
						`player_name` ,
						`player_email` ,
						`player_seen`
						)
						VALUES (
						'%s', '', '0'
						);
					
					", $dblink->real_escape_string( $this->m_Name )
			);

			if( $results = $dblink->query( $query ) )
			{
				$this->m_id = $dblink->insert_id;
			}
		}
		
		
	}
	
	function GetPlayerId()
	{
		Global $dblink;
		
		$query = sprintf( "
				SELECT `player_id`
				FROM `killerabbit`.`mc_players`
				WHERE `player_name` = '%s'
				", $dblink->real_escape_string( $this->m_Name )
		);

		if( $results = $dblink->query( $query ) )
		{
			if( $results->num_rows > 0 )
			{
				while( $row = $results->fetch_assoc() )
				{
					$this->m_id = $row['player_id'];
				}
			}
			else
			{
				// Player is unknown
				echo "unable to find player\n";
				$this->m_id = -1;
			}
		}
	}

	function HasActiveSubscription()
	{
		Global $dblink;

		$query = sprintf( "
				SELECT *
				FROM `killerabbit`.`mc_subscription`
				WHERE `player` = '%s' AND `expired` = 0
				", $dblink->real_escape_string( $this->m_Name )
		);

		if( $results = $dblink->query( $query ) )
		{
			if( $results->num_rows > 0 )
			{
				while( $row = $results->fetch_assoc() )
				{
					$expirey = strtotime( "+".$row['duration']." month", mysql2timestamp( $row['startdate'] ) );
						
					if( time() < $expirey )
					{
						return true;
					}
					else
					{
						mail(
							"paul@cppmonkey.net",
							"Expired subscription",
							sprintf("%s subscription has expired", $this->m_Name)
						);
					}
				}
			}
		}

		return false;
	}

	function GetGroup()
	{
		return $this->m_Group;
	}

	function SetGroup( $group )
	{
		$this->m_Group = $group;
	}

	function GetName()
	{
		return $this->m_Name;
	}
	
	function GetPermissions( $server_id )
	{
		Global $dblink;
		
		$output = "\n";
		$result = array();

		$query = sprintf( "
				SELECT `permission`, `value`
				FROM `killerabbit`.`mc_permissions`
				WHERE `player_id` = '%d' AND `server_id` = '%d'
				", $this->m_id, $server_id );
		
		if( $results = $dblink->query( $query ) )
		{
			if( $results->num_rows > 0 )
			{
				while( $row = $results->fetch_assoc() )
				{
					$result[$row['permission']] = $row['value'];
					$output .= $row['permission'].":".$row['value']."\n";
				}
			}
		}
		return $result;
	
	}

	function IsAdmin()
	{
		return $this->m_Admin;
	}
	
	function IsVip()
	{
		if ($this->m_Group == "vip")
		{
			return true;
		}
		return false;
	}
	
	function LogTransition( $server )
	{
		Global $dblink;
		$query = sprintf(
			    "INSERT INTO `mc_transition` ( `player`, `server_id`, `action`, `isVip`, `isAdmin`, `timestamp` )
				VALUES
				( '%s', '%d', '%d', '%s', '%s', UTC_TIMESTAMP() )",
				$dblink->real_escape_string( GetName() ),
				$server->GetId(),
				Action( $_GET["action"] ),
				IsVip(),
				IsAdmin()
			);
	}
}


function mysql2timestamp($datetime){
	$val = explode(" ",$datetime);
	$date = explode("-",$val[0]);
	$time = explode(":",$val[1]);
	return mktime($time[0],$time[1],$time[2],$date[1],$date[2],$date[0]);
}

?>