<?php

include "config.php";

include "minecraft.class.php";

if (!$dblink)
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
		DISTINCT `mc_transition`.`player`, `mc_players`.`player_id`
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
		
		while ( $record = $results->fetch_assoc() )
		{
			if( $first )
			{
				echo sprintf( "{\"name\":\"%s\",\"id\":\"%s\"}",
					$record['player'],
					$record['player_id']
				);
				$first = false;
			}
			else
			{
				echo sprintf( ",{\"name\":\"%s\",\"id\":\"%s\"}",
					$record['player'],
					$record['player_id']
				);
			}
		}
		echo "]";
	}
}else{
	echo "invalid database link<br>";
	echo "Connect Error: " . mysqli_connect_error();
}

?>