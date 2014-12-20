<?php

include( "player.class.php" );


$player = new Player( "caboose89" );

echo "VIP? ".$player->ActiveSubscription( $dblink = new mysqli( "mysql.cppmonkey.net", $dbuser, $dbpass, $dbname ) );

if( $player->GetGroup() == "vip" )
{
    if( !$player->ActiveSubscription( $dblink ) )
    {
        echo "invalid or expired subscription";
    }
}

?>