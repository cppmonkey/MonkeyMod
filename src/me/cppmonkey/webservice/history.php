<?php
error_reporting(E_ALL | E_STRICT);

include "config.php";

include "minecraft.class.php";

if (!isset($dblink))
{
	$dblink = new mysqli( "mysql.cppmonkey.net", $dbuser, $dbpass, "killerabbit" );
}

if( isset( $_GET["serverip"]) ){

	$minecraft = new Minecraft();

	$minecraft->BuildServerList();

	$server = $minecraft->ServerFromIp($_GET['serverip']);

	$query = sprintf("
			(SELECT `chat_name` AS 'name', `chat_message` AS 'msg', `chat_date` as 'timestamp' FROM `mc_chat` WHERE `server_id` = '%d')
UNION
(SELECT `player` AS 'name', `action` AS 'msg', `timestamp` AS 'timestamp' FROM `mc_transition` WHERE `server_id` = '%d')
ORDER BY `timestamp` DESC
LIMIT 0 , 30
		",
			$dblink->real_escape_string( $server->GetId() ),
			$dblink->real_escape_string( $server->GetId() )
		);
		
		
		if( !mysqli_connect_errno() )
		{
			if( $results = $dblink->query( $query ) )
			{
				echo "[";
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
					echo '{"name":"none","msg":"none","timestamp":"none"}';
				}
				echo "]";
			}
		} else {
			echo '[{"name":"error","msg":"'.$dblink->error.'"}]';
		}
		

} else {
	echo '[{"name":"error","msg":" serverip not specified"}]';
}
?>