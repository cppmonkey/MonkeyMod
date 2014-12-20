<?php

class Player{

	var $m_Name;
    var $m_id = -1;

    function Player() {
        Global $dblink;

        if(isset($_GET['player'])){
            $this->m_Name = $_GET['player'];

            $query = sprintf( "
                    SELECT `player_id`
                    FROM `killerabbit`.`mc_players`
                    WHERE `player_name` = '%s'
                    ", $dblink->real_escape_string( $this->m_Name )
            );

            if($results = $dblink->query($query)) {
                if( $results->num_rows > 0 ) {
                    while($row = $results->fetch_assoc()) {
                        $this->m_id = $row['player_id'];
                    }
                } else {
                    $this->m_id = -1;
                }
            } else {
                ReportError("unable to insert<br>".$query."<br>".print_r($dblink->error_list,true));
			}
		}
		
        if(isset($_GET['player_id'])){
            $this->m_id = $_GET['player_id'];
        }
		
        if( $this->m_id == -1 ) {
			// Create new Player
			Global $dblink;
		
			$query = sprintf( "
					INSERT INTO `killerabbit`.`mc_players` (
						`player_name` ,
                    `player_email` 
						)
						VALUES (
                    '%s', ''
						);
					
					", $dblink->real_escape_string( $this->m_Name )
			);

            if( $results = $dblink->query( $query ) ){
				$this->m_id = $dblink->insert_id;
            } else {
                ReportError("unable to insert<br>".$query."<br>".print_r($dblink->error_list,true));
			}
		}
		
		
	}
	
    function GetId() {
        return $this->m_id;
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
        } else {
            ReportError("unable to insert<br>".$query."<br>".print_r($dblink->error_list,true));
		}
		return $result;
	
	}
}


function mysql2timestamp($datetime){
	$val = explode(" ",$datetime);
	$date = explode("-",$val[0]);
	$time = explode(":",$val[1]);
	return mktime($time[0],$time[1],$time[2],$date[1],$date[2],$date[0]);
}

?>