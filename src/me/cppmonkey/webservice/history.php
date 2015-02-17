<?php
error_reporting(E_ALL | E_STRICT);

include "config.php";

include "./lib/minecraft.class.php";

if (!isset($dblink)) {
    $dblink = new mysqli( $dbserver, $dbuser, $dbpass, $dbname );
}

if( isset($_GET["serverip"])) {

    $minecraft = new Minecraft();

    $minecraft->BuildServerList();

    $server = $minecraft->ServerFromIp($_GET['serverip']);

    if($server != null) {

        $query = sprintf("CALL GetServerHistory('%1\$d', '%2\$d', '%3\$d')",
                $server->GetId(),
                (isset($_GET["start"]) ? $_GET["start"]:0),
                (isset($_GET["limit"]) ? $_GET["limit"]:30)
        );

        if( !mysqli_connect_errno() ) {
            if( $results = $dblink->query( $query ) ) {
                echo "[";
                $first = true;
                if( $results->num_rows > 0 ){
                    while ( $record = $results->fetch_assoc()) {
                        if( $first ) {
                            echo json_encode($record);
                            $first = false;
                        } else {
                            echo ",".json_encode($record);
                        }
                    }
                } else {
                    echo '{"name":"none","msg":"none","timestamp":"none"}';
                }
                echo "]";
            } else {
                print_r($dblink->error_list);
            }
        } else {
            echo '[{"name":"error","msg":"'.$dblink->error.'"}]';
        }
    } else {
        echo '[{"name":"error","msg":"Unable to find server with ip "'.$_GET['serverip'].',"timestamp":"none"}]';
    }


} else {
    echo '[{"name":"error","msg":" serverip not specified"}]';
}
?>
