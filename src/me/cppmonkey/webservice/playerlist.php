<?php
error_reporting(E_ALL | E_STRICT);

include "config.php";

include "minecraft.class.php";

if (!isset($dblink))
{
	$dblink = new mysqli( "mysql.cppmonkey.net", $dbuser, $dbpass, "killerabbit" );
}

if (isset($_GET['serverip']))
{
	$minecraft = new Minecraft();

	$minecraft->BuildServerList();

	$server = $minecraft->ServerFromIp($_GET['serverip']);
	
	if ($server)
	
	{
		$query = sprintf( "SELECT
			DISTINCT `mc_transition`.`player`, `mc_players`.`player_id`
		FROM
			`mc_transition` 
		INNER JOIN
			`mc_players`
		ON
			`mc_transition`.`player` = `mc_players`.`player_name`

		WHERE `server_id` = '%d';", $dblink->real_escape_string( $server->GetId() ) );
	}
	else
	{
		echo "ERROR";
	}
}
else
{
	$query = "SELECT
		DISTINCT `mc_transition`.`player` AS 'name', `mc_players`.`player_id` AS 'id'
	FROM
		`mc_transition` 
	INNER JOIN
		`mc_players`
	ON
		`mc_transition`.`player` = `mc_players`.`player_name`;";
}

if( !mysqli_connect_errno() )
{
	if( $results = $dblink->query( $query ) )
	{
		echo "[";
		// echo "Servers found ".$results->num_rows."<br />\n";
		
		$first = true;
		if( $results->num_rows > 0 ){
			while ( $record = $results->fetch_assoc() )
			{
				if( $first )
				{
					echo json_encode($record);
					$first = false;
				}
				else
				{
					echo ",".json_encode($record);
				}
			}
		} else {
			echo '{"name":"none","id":"-1"}';
		}
		echo "]";
	}
}else{
	echo "invalid database link<br>";
	echo "Connect Error: " . mysqli_connect_error();
}

?>