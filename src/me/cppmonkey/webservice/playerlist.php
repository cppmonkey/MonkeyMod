<?php
error_reporting(E_ALL | E_STRICT);

include "config.php";

include "./lib/minecraft.class.php";

if (!isset($dblink)) {
    $dblink = new mysqli( $dbserver, $dbuser, $dbpass, $dbname );
}

if (isset($_GET['serverip'])) {
    $minecraft = new Minecraft();

    $minecraft->BuildServerList();

    $server = $minecraft->ServerFromIp($_GET['serverip']);

    if ($server)
    {
        if (isset($_GET['userid'])) {
            $query = sprintf( "SELECT `permission`, `value`
                    FROM
                    `mc_permissions`

                    WHERE `server_id` = '%d' AND `player_id` = '%d';",
                    $dblink->real_escape_string( $server->GetId() ),
                    $dblink->real_escape_string( $_GET['userid'] )
            );
        }else{
            $query = sprintf( "SELECT
                    DISTINCT `mc_players`.`player_name` AS `name`, `mc_players`.`player_id` as `id`
                    FROM
                    `mc_transition`
                    INNER JOIN
                    `mc_players`
                    ON
                    `mc_transition`.`player_id` = `mc_players`.`player_id`

                    WHERE `server_id` = '%d' AND `mc_transition`.`timestamp` > NOW() - INTERVAL 3 MONTH;", $dblink->real_escape_string( $server->GetId() ) );
        }
    }
    else
    {
        echo '{"name":"error :- '.$dblink->error.'","id":"-1"}';
    }
}
else
{
    $query = "SELECT
    DISTINCT `mc_players`.`player_name` AS 'name', `mc_players`.`player_id` AS 'id'
    FROM
    `mc_transition`
    INNER JOIN
    `mc_players`
    ON
    `mc_transition`.`player_id` = `mc_players`.`player_id`;";
}

if( !mysqli_connect_errno() ) {
    if( $results = $dblink->query( $query ) ) {
        echo "[";
        // echo "Servers found ".$results->num_rows."<br />\n";

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
            echo '{"name":"none","id":"-1"}';
        }
        echo "]";
    } else {
        print_r($dblink->error_list);
    }
}else{
    echo '{"name":"error :- '.mysqli_connect_error().'","id":"-1"}';
}

?>