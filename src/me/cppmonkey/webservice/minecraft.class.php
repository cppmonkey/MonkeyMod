<?php

include "minecraftserver.class.php";
include "playerlist.class.php";

class Minecraft
{
	//private variables
	var $servers = array();


    function Minecraft() {
		// global variables required by this function
        global $dbserver, $dbuser, $dbpass, $dblink;

        if( !$dblink ) {
            $dblink = new mysqli( $dbserver, $dbuser, $dbpass, "killerabbit" );
		}
	}

    function BuildServerList() {
		global $dblink;

        if (isset($_GET['server_id'])) {
			$query = sprintf( "SELECT *
				FROM `mc_servers`
				WHERE `id` = '%d'
				", $dblink->real_escape_string( $_GET['server_id']));
        } else if (isset($_GET['owner_id'])) {
			$query = sprintf( "SELECT *
				FROM `mc_servers`
				WHERE `owner_id` = '%d'
				", $dblink->real_escape_string( $_GET['owner_id']));
		}
		else
		{
			$query = "
				SELECT *
				FROM `mc_servers`
				";
		}

		if( !mysqli_connect_errno() )
		{
			if( $results = $dblink->query( $query ) )
			{
				// echo "Servers found ".$results->num_rows."<br />\n";
				while ( $record = $results->fetch_assoc() )
				{
					//$record['id'];
					//$record['title'];
					//$record['address'];
					//$record['game_port'];
					//$record['admin_port'];
					//$record['admin_password'];
					array_push($this->servers, new MinecraftServer( $record ));
				}
			}
		}else{
			echo "invalid database link<br>";
			echo "Connect Error: " . mysqli_connect_error();
		}
	}// End BuildServerList()

	function BuildPlayerLists()
	{
		foreach( $this->servers as &$server )
		{
			$server->Connect();

			if( $server->Auth() )
			{
				$playerList = new PlayerList( $server->ExecCommand( "list" ), $this->dblink );
				$playerList->Display();
			}else{
				echo "Connection failed";
			}
		}

	}

    function Dump() {
        foreach( $this->servers as &$server ) {
			echo sprintf( "<div>%s</div>",
			$server->GetTitle()
			);
		}
	}

    function DumpJSON() {
		echo "[";

        if(count($this->servers) > 0) {
		echo sprintf( "{\"title\":\"%s\",\"ip\":\"%s\"}",
				$this->servers[0]->GetTitle(),
				$this->servers[0]->GetIp()
			);
            for( $i = 1; $i < count($this->servers); $i++ ) {

			echo sprintf( ",{\"title\":\"%s\",\"ip\":\"%s\"}",
				$this->servers[$i]->GetTitle(),
				$this->servers[$i]->GetIp()
			);
		}
        }
		echo "]";
	}

	function ServerFromIp( $ip )
	{
		// echo "searching for {$ip}<br/>";
		foreach( $this->servers as &$server )
		{
			// echo $server->GetTitle().":- ".$server->GetIp()."<br/>";
			if( $server->GetIp() == $ip )
			return $server;
		}

		return 0;
	}

	function CreateServer( $ip, $port )
	{
		Global $dblink;

		$server = new MinecraftServer( array(
			"id" => -1,
			"title" => $ip,
			"address" => $ip,
			"server_ip" => $ip,
			"game_port" => $port-10,
			"admin_port" => $port,
			"admin_password" => "",
			"owner_id" => 0
			));

		$query = sprintf(
			"INSERT INTO `mc_servers`
				(`title`, `address`, `server_ip`, `game_port`, `admin_port`, `admin_password`, `owner_id`)
			VALUES
				('%s', '%s', '%s', '%d', '%d', '%s', '%d')
			",
				$server->GetTitle(),
				$server->GetAddress(),
				$server->GetIp(),
				$server->GetGamePort(),
				$server->GetAdminPort(),
				$server->GetAdminPass(),
				$server->GetOwnerId()
			);

		$dblink->query( $query );


		array_push($this->servers, $server);

		return $server;
	}
}

?>