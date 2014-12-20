<?php
error_reporting(E_ALL | E_STRICT);
include "config.php";
include "minecraft.class.php";

function ReportError( $strMsg = "" ){
	$mailTo = "paul@cppmonkey.net";
    $mailSubject = "update.php error ";

    if(isset($_GET["action"])){
        $mailSubject .= $_GET["action"];
    }

	$bt = debug_backtrace();
     $caller = array_shift($bt);

	$mailMessage = "
	\$_GET data: \n".print_r($_GET, TRUE)."\n\n".
	"\$_POST data:\n".print_r($_POST, TRUE)."\n\n".
	$strMsg."\n\n".
	$caller['file']." ".$caller['line']."\n\n".
	$_SERVER['SERVER_NAME'].$_SERVER['REQUEST_URI'];


    // mail($mailTo, $mailSubject, $mailMessage);

    print_r($mailMessage);
}

$dblink = new mysqli( $dbserver, $dbuser, $dbpass, "killerabbit" );

define("CONNECT", 1);
define("DISCONNECT", 2);
define("IGNITE", 3);
define("CHEST", 4);
define("TOWER", 5);
define("MODIFY", 6);
define("UPDATE", 7);
define("BUILD", 8);

function Action( $strAction )
{
	if ($strAction == "connect")
		return CONNECT;
	if ($strAction == "disconnect")
		return DISCONNECT;
	if ($strAction == "ignite" || $strAction == "ignite-attempt")
	return IGNITE;
	if ($strAction == "chest" || $strAction == "chest-break-attempt" || $strAction == "attempt_to_open_chest" || $strAction == "attempt_to_unlock_chest")
		return CHEST;
	if ($strAction == "tower")
		return TOWER;
	if ($strAction == "modify")
		return MODIFY;
	if ($strAction == "update")
		return UPDATE;
	if ($strAction == "build" || $strAction == "build-attempt" || $strAction == "block-break-attempt")
		return BUILD;
}

$minecraft = new Minecraft();
$minecraft->BuildServerList();

$server_ip = "";

if( isset( $_GET['serverip'] ))
	$server_ip = $_GET['serverip'];
else
	$server_ip = $_SERVER['REMOTE_ADDR'];

$server = $minecraft->ServerFromIp( $server_ip );

if( isset($_GET["action"]) && $server ) {
	$query = "";

    if(isset( $_GET["player"]) || isset($_GET["player_id"])) {
        $player = new Player();

        if (!isset($_GET["record"]) &&( $_GET["action"] == "connect" || $_GET["action"] == "disconnect" )) {


			$query = sprintf(
                    "INSERT INTO `mc_transition` ( `player_id`, `server_id`, `action`, `timestamp` )
					VALUES
                    ( '%d', '%d', '%d', UTC_TIMESTAMP() )",
                    $player->GetId(),
					$server->GetId(),
					Action( $_GET["action"] )
				);
        } else if( isset($_GET["message"])) {

				$query = sprintf(
                        "INSERT INTO `mc_chat` ( `player_id`, `chat_message`, `server_id`, `chat_date`) VALUES ( '%d', '%s', '%d', UTC_TIMESTAMP());",
                        $player->GetId(),
						$dblink->real_escape_string( $_GET["message"]),
						$server->GetId()
					);

        } else if(Action($_GET["action"]) == 4) {
			//Log user access to chest!

			/* TODO Process this information! Chest Destroy
			 * new Parm("action", "chest"),
             * new Parm("type", "destroy"),
             * new Parm("player",player.getName())
			 */

			$query = sprintf(
                    "INSERT INTO `mc_transition` (`player_id`, `action`, `server_id`, `timestamp`) VALUES ( '%d', '%d', '%d', UTC_TIMESTAMP())",
                    $player->GetId(),
				Action( $_GET["action"] ),
				/* $dblink->real_escape_string($_GET["type"]),*/
				$server->GetId()
				);

			// AND

			/* TODO Process this information! Chest Access
			 * Parm[] Parm = {
             * new Parm("action", "chest"),
             * new Parm("type", "access"),
             * new Parm("player",player.getName())
             * };
             */
        } else if ($_GET["action"] == "modify") {
			$query = sprintf(
                    "INSERT INTO `mc_transition` (`player_id`, `action`, `server_id`, `timestamp`) VALUES ( '%d', '%d', '%d', UTC_TIMESTAMP())",
                    $player->GetId(),
				Action( $_GET["action"] ),
				/* $dblink->real_escape_string($_GET["type"]),*/
				$server->GetId()
				);

        } else if ($_GET["action"] == "tower") {
			$query = sprintf(
                    "INSERT INTO `mc_transition` (`player_id`, `action`, `server_id`, `timestamp`) VALUES ( '%d', '%d', '%d', UTC_TIMESTAMP())",
                    $player->GetId(),
				Action( $_GET["action"] ),
				/* $dblink->real_escape_string($_GET["type"]),*/
				$server->GetId()
				);
			/* TODO Process this information! DurpTower
			 * Parm[] Parm = {
			 * new Parm("action", "tower"),
             * new Parm("player",player.getName()),
             * new Parm("vip",Boolean.toString(player.isInGroup("vip"))),
             * new Parm("admin",Boolean.toString(player.isAdmin()))
             */
        } else if (Action($_GET["action"])== IGNITE) {

			$query = sprintf(
                    "INSERT INTO `mc_transition` (`player_id`, `action`, `server_id`, `timestamp`) VALUES ( '%d', '%d', '%d', UTC_TIMESTAMP())",
                   $player->GetId(),
				Action( $_GET["action"] ),
				/* $dblink->real_escape_string($_GET["type"]),*/
				$server->GetId()
				);
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
    } else if ($_GET["action"] == "update") {

        $package = new CServerPackage($_GET);

			$query = sprintf(
                "INSERT INTO `mc_serverPackageStart` (`server_id`, `package_id`, `package_version`, `package_build`, `time`) VALUES ('%d', '%d', '%s', '%s', UTC_TIMESTAMP())",
						$server->GetId(),
                2, // TODO Get Plugin ID
						$dblink->real_escape_string($_GET["version"]),
						$dblink->real_escape_string($_GET["build"])
				/*				Action( $_GET["action"] ),*/
				/* $dblink->real_escape_string($_GET["type"]),*/

				);
			/* TODO Process this information! Ignite
			 *
			 * Parm[] parms = {
            new Parm("action", "update"),
            new Parm("package", this.getName()),
            new Parm("version", this.getVersion()),
            new Parm("build", this.getBuild()),
            new Parm("rcon-port", Integer.toString(getServer().getPort() + 10))
        };

             */
		}

    if($dblink && $query != "") {
        if($dblink->query($query)) {
				echo "insert complete!\n";
        } else {
            echo "unable to insert<br>".$query."<br>".print_r($dblink->error_list,true);
            ReportError("unable to insert<br>".$query."<br>".print_r($dblink->error_list,true));
        }
    } else if($query == "") {
        // Nothing to do
    } else {
        echo "unable to connect to mysql";
        ReportError("unable to connect to mysql");
    }

    if($_GET["action"] == "connect") {
					$query = sprintf( "
							SELECT *
							FROM `killerabbit`.`mc_offline`
							WHERE `sent` = 0 AND `to` LIKE '%s'
							", $dblink->real_escape_string( $_GET['player'] )
						);

        if($results = $dblink->query( $query )) {
						$player = new Player( $_GET['player'] );
						$permissions = $player->GetPermissions( $server->GetId() );

						// Print out permisions
						foreach ($permissions as $key => $value) {
							print( $key.":".$value."\n");
						}

            if ($server->ValidateVips()) {
                if( $player->HasActiveSubscription( $dblink )) {
								print "\nisVip:true\n";
                } else if( $player->GetGroup() == "vip" ) {
                    if( !$player->IsAdmin()) {
									print "\nisVip:false\n";
								}
								}
							}

						$results->close();
        } else {
            echo "unable to execute<br>".$query."<br>".print_r($dblink->error_list,true);
            ReportError("unable to insert<br>".$query."<br>".print_r($dblink->error_list,true));
					}
					//Process Subscriptions
		}


		//Tidyup

		//$dblink->close();
	//EndProcess Chat and Login

	if( isset( $_GET["package"] ) && isset( $_GET["version"] ) && isset( $_GET["build"] ) )
	{
		echo "processing server update request<br/>\n";
		echo "get server package list<br/>\n";
		echo "search package list for package<br/>\n";
		echo "compare package versions<br/>\n";
		echo "update is needed<br/>\n";
		$package = new CServerPackage( array( "name" => $_GET["package"], "version" => $_GET["version"], "build" => $_GET["build"]));

        if( !$package->IsUptodate("mc_packages")) {
			echo "Attempting to update server<br/>\n";
			echo "false\n<br/>"; //for original build 16

				echo "excuting update<br/>\n";

			}
		}
} else {
    if( !$server && isset($_GET['rcon-port'])) {
		echo "Unable to load server<br/>No servers with the IP {$server_ip}<br>";
		$server = $minecraft->CreateServer( $server_ip, $_GET['rcon-port']);
	}


	echo "invalid query";
    echo print_r($dblink->error_list,true);
    ReportError("invalid query<br>\n".print_r($server,true));

}
?>