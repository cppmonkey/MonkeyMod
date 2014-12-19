<?php
error_reporting(E_ALL | E_STRICT);
include "config.php";
include "minecraft.class.php";

$dblink = new mysqli( $dbserver, $dbuser, $dbpass, "killerabbit" );

function Action( $strAction )
{
	if ($strAction == "connect")
	return 1;
	if ($strAction == "disconnect")
	return 2;
	if ($strAction == "message")
	return 3;
	if ($strAction == "chest")
	return 4;
}

$minecraft = new Minecraft();
$minecraft->BuildServerList();

$server_ip = "";

if( isset( $_GET['serverip'] ))
$server_ip = $_GET['serverip'];
else
$server_ip = $_SERVER['REMOTE_ADDR'];

$server = $minecraft->ServerFromIp( $server_ip );

if( isset($_GET["action"]) && $server )
{
	$query = "";

	if( isset( $_GET["player"]) )
	{
		if ($_GET["action"] == "connect" || $_GET["action"] == "disconnect" )
		{
			$player = new Player($_GET['player']);
			
			$query = sprintf(
				    "INSERT INTO `mc_transition` ( `player`, `server_id`, `action`, `isVip`, `isAdmin`, `timestamp` )
					VALUES
					( '%s', '%d', '%d', '%s', '%s', UTC_TIMESTAMP() )",
					$dblink->real_escape_string( $player->GetName()),
					$server->GetId(),
					Action( $_GET["action"] ),
					$player->IsVip(),
					$player->IsAdmin()
				);
		}
		else if( isset($_GET["message"] ))
		{
			$split = explode( " ", $_GET["message"], 3 );
				
			if( $split[0] == "!offline" )
			{
				$query = sprintf(
						"INSERT INTO `mc_offline` ( `from`, `to`, `message`) VALUES ( '%s', '%s', '%s' )",
				$dblink->real_escape_string( $_GET["player"] ),
				$dblink->real_escape_string( $split[1] )/* username */,
				$dblink->real_escape_string( $split[2] )/* message */
				);
			}
			else
			{
				$query = sprintf(
					    "INSERT INTO `mc_chat` ( `chat_name`, `chat_message`, `server_id`, `chat_date`) VALUES ( '%s', '%s', '%d', UTC_TIMESTAMP());",
						$dblink->real_escape_string( $_GET["player"]),
						$dblink->real_escape_string( $_GET["message"]),
						$server->GetId()
					);
			}
		}
		else if( $_GET["action"] == "chest" && isset($_GET['type']))
		{
			//Log user access to chest!
			
			/* TODO Process this information! Chest Destroy
			 * new Parm("action", "chest"),
             * new Parm("type", "destroy"),
             * new Parm("player",player.getName())
			 */
			
			$query = sprintf(
					"INSERT INTO `` (``)",
				$dblink->real_escape_string($_GET["player"]),
				$dblink->real_escape_string($_GET["action"]),
				$dblink->real_escape_string($_GET["type"])
				);
			
			// AND
			
			/* TODO Process this information! Chest Access
			 * Parm[] Parm = {
             * new Parm("action", "chest"),
             * new Parm("type", "access"),
             * new Parm("player",player.getName())
             * };
             */
		}
		else if ($_GET["action"] == "tower")
		{
			/* TODO Process this information! DurpTower
			 * Parm[] Parm = {
			 * new Parm("action", "tower"),
             * new Parm("player",player.getName()),
             * new Parm("vip",Boolean.toString(player.isInGroup("vip"))),
             * new Parm("admin",Boolean.toString(player.isAdmin()))
             */
		}
		else if ($_GET["action"] == "ignite")
		{
			/* TODO Process this information! Ignite
			 * 
			 * Parm[] Parm = {
             * new Parm("action", "ignite"),
             * new Parm("source", Integer.toString(block.getStatus())),
             * new Parm("player",player.getName()),
             * new Parm("vip", Boolean.toString(player.isInGroup("vip")))
             * };
             */
		}	

		if( $dblink )
		{
			if( $dblink->query( $query ) )
			{
				echo "insert complete!\n";

				// Check for offline messages
				if( $_GET["action"] == "connect" )
				{
					$query = sprintf( "
							SELECT *
							FROM `killerabbit`.`mc_offline`
							WHERE `sent` = 0 AND `to` LIKE '%s'
							", $dblink->real_escape_string( $_GET['player'] )
						);
						
					if( $results = $dblink->query( $query ))
					{
						$player = new Player( $_GET['player'] );
						$permissions = $player->GetPermissions( $server->GetId() );
						
						// Print out permisions
						foreach ($permissions as $key => $value) {
							print( $key.":".$value."\n");
						}
						
						if ($server->ValidateVips())
						{
							print "processing offline messages\n";
								
							$server->Connect();
							$server->Auth();
								
							if( $player->HasActiveSubscription( $dblink ) )
							{
								print $server->ExecCommand( "tell ".$_GET['player']." Valid VIP Subscription found!" )."\n";
								print $server->ExecCommand( "modify ".$_GET['player']." g:vip ir:true" )."\n";
								//echo $server->ExecCommand( "monkey add vip ".$_GET['player'] );
								print "\nisVip:true\n";
							}
							else if( $player->GetGroup() == "vip" )
							{
								if( !$player->IsAdmin())
								{
									print $server->ExecCommand( "tell ".$_GET['player']." Your VIP subscription is invalid" )."\n";
									print $server->ExecCommand( "modify ".$_GET['player']." g:default ir:false" )."\n";
									//echo $server->ExecCommand( "monkey remove vip ".$_GET['player'] );
									print "\nisVip:false\n";
								}
								else
								{
									$server->ExecCommand( "tell ".$_GET['player']." Welcome back!");
								}
							}
							else
							{
								print "Player not vip"."\n";
								
								if ( (isset($permissions["canBuild"]) && $permissions["canBuild"] == "true") && (isset($permissions["isAdmin"]) && $permissions["isAdmin"] != "true")) {
									print $server->ExecCommand( "modify ".$_GET['player']." g:default ir:false" )."\n";
								}else if (isset($permissions["isAdmin"]) && $permissions["isAdmin"] == "true") {
									print $server->ExecCommand( "modify ".$_GET['player']." g:admins ir:true" )."\n";
								}
							}
						}
							
						if ($results->num_rows > 0 )
						{
							while ($row = $results->fetch_row())
							{
								print $server->ExecCommand(
										sprintf( "say from: %s to: %s msg: %s", $row[1], $row[2], $row[3] )
									);
							}
						}
						$results->close();
					}
					else
					{
						echo "unable to execute<br>".$query."<br>".$dblink->sqlstate;
					}
						
						
					//Process Subscriptions
						
						
				}
			}
			else
			{
				echo "unable to insert<br>".$query."<br>".$dblink->sqlstate;
			}
				
		}
		else
		{
			echo "unable to connect to mysql";
		}


		//Tidyup

		$dblink->close();
	} //EndProcess Chat and Login

	if( isset( $_GET["package"] ) && isset( $_GET["version"] ) && isset( $_GET["build"] ) )
	{
		echo "processing server update request<br/>";
		echo "get server package list<br>";
		echo "search package list for package<br>";
		echo "compare package versions<br>";
		echo "update is needed<br>";
		$package = new CServerPackage( array( "name" => $_GET["package"], "version" => $_GET["version"], "build" => $_GET["build"]));

		if( !$package->IsUptodate( "mc_packages"  ))
		{
			echo "Attempting to update server";
			$server->Connect();
			if( $server->Auth() )
			{
				echo "excuting update<br/>";
				echo $server->ExecCommand( "say <PHP> forcing update" );
				echo $server->ExecCommand( $package->GetUpdateCmd() );

				if ( $_GET["version"]  == "0.5" && $_GET["package"] == "CppMonkeyAdmin" )
				{
					sleep( 10 );
					echo $server->ExecCommand( "reloadplugin ".$package->GetName() );
				}
			}
			else
			{
				echo "Auth failed";
			}
		}
	}
}
else
{
	if( !$server && isset( $_GET['rcon-port'] ) )
	{
		echo "Unable to load server<br/>No servers with the IP {$server_ip}<br>";
		$server = $minecraft->CreateServer( $server_ip, $_GET['rcon-port']);
	}
	echo "invalid query";
}
?>