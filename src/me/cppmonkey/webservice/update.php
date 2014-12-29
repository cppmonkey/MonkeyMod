<?php
error_reporting(E_ALL | E_STRICT);
include "config.php";
include "./lib/minecraft.class.php";

function ReportError( $strMsg = "" ){
    $mailTo = "paul@cppmonkey.net";
    $mailSubject = "update.php error ";

    if(isset($_GET["player"])){
        $mailSubject .= $_GET["player"].(isset($_GET["action"])?" ":"");
    }

    if(isset($_GET["action"])){
        $mailSubject .= $_GET["action"];
    }

    $bt = debug_backtrace();
    $caller = array_shift($bt);

    $mailMessage = "
    \$_GET data: \n".print_r($_GET, TRUE)."\n\n".
    "\$_POST data:\n".print_r($_POST, TRUE)."\n\n".
    "\$_SERVER data:\n".print_r($_SERVER, TRUE)."\n\n".
    
    $strMsg."\n\n".
    $caller['file']." ".$caller['line']."\n\n".
    $_SERVER['SERVER_NAME'].$_SERVER['REQUEST_URI']."&serverip=".$_SERVER['REMOTE_ADDR'];
    echo $mailMessage;
    //mail($mailTo, $mailSubject, $mailMessage);
}

$dblink = new mysqli( $dbserver, $dbuser, $dbpass, $dbname );

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

if( !$server && (isset($_GET['rcon-port']) || isset($_GET['port']))) {
    echo "Unable to load server<br/>No servers with the IP {$server_ip}<br>";
    $server = $minecraft->CreateServer( $server_ip );
}

if( isset($_GET["action"]) && $server ) {
    $query = "";

    if(isset( $_GET["player"]) || isset($_GET["player_id"])) {
        include_once "./lib/player.class.php";
        $player = new Player();

        $action = Action($_GET["action"]);
        if (!isset($_GET["record"]) &&
                ( $action == CONNECT || $action == DISCONNECT || $action == CHEST || $action == MODIFY || $action == TOWER || $action == IGNITE )) {

            $query = sprintf(
                    "CALL LogPlayerAction('%d', '%d', '%d', UTC_TIMESTAMP())",
                    $server->GetId(),
                    $player->GetId(),
                    $action
            );
        } else if( isset($_GET["message"])) {
            $query = sprintf(
                    "CALL LogPlayerChat( '%d', '%d', '%s', UTC_TIMESTAMP());",
                    $server->GetId(),
                    $player->GetId(),
                    $_GET["message"]
            );
        }
        
        if( $action == CONNECT ){
            echo "playerUID:".$player->GetId()."\n";
        }
    } else if ($_GET["action"] == "update") {

        $package = new CServerPackage($_GET);

        $query = sprintf(
                "CALL LogServerPackageStart('%d', '%d', '%s', '%s', UTC_TIMESTAMP())",
                $server->GetId(),
                2, // TODO Get Plugin ID
                $_GET["version"],
                $_GET["build"]
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
        ReportError("unable to connect to mysql".print_r($dblink->error_list,true));
    }

    if($_GET["action"] == "connect") {
        include_once "./lib/player.class.php";
        $player = new Player();
        $permissions = $player->GetPermissions( $server->GetId() );

        // Print out permisions
        foreach ($permissions as $key => $value) {
            print( $key.":".$value."\n");
        }
    }


    //Tidyup

    //$dblink->close();
    //EndProcess Chat and Login

    if( isset( $_GET["package"] ) && isset( $_GET["version"] ) && isset( $_GET["build"] ) ) {
        echo "processing server update request<br/>\n";
        echo "get server package list<br/>\n";
        echo "search package list for package<br/>\n";
        echo "compare package versions<br/>\n";
        echo "update is needed<br/>\n";
        echo "serverUID:".$server->GetId()."\n";
        $package = new CServerPackage();

        if( !$package->IsUptodate("mc_packages")) {
            echo "Attempting to update server<br/>\n";
            echo "false\n<br/>"; //for original build 16
        }
    }
}
?>