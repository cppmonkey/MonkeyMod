<?php

include "player.class.php";

class PlayerList
{
    var $m_Players = array();

    function PlayerList( $listString )
    {
        $listString = trim( substr( $listString, 34 ));
        echo "server response: ".$listString."<br/>\nresponse count: ".count( $players )."<br />";

        if( $listString != "" )
        {
            //Parse player string
            $players = explode( " ", $listString );

            for( $i = 0; $i < count( $players ); $i++ )
            {
                $temp = new Player( trim($players[$i], ",") );
                array_push( $this->m_Players, $temp );
            }
        }
    }

    function Display()
    {
        for( $i = 0; $i < count( $this->m_Players ); $i++ )
        {
            $player = $this->m_Players[$i];


            if( $player->HasActiveSubscription( ))
            {
                echo "<div style='color:red'>";
            }else{
                echo "<div>";
            }
            echo $player->GetName();
            echo "</div><br />\n";
        }
    }

    function Size()
    {
        return count( $this->m_Players );
    }

}

?>