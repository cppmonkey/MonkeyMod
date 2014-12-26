<?php

include "serverpackage.class.php";

class MinecraftServer {
    var $id;
    var $title;
    var $address, $gamePort;
    var $ipv4, $ipv6;
    var $packages;

    var $rcon;


    function MinecraftServer( $details ) {
        $this->id		= $details['id'];
        $this->title		= $details['title'];
        $this->address	= $details['address'];
        $this->ipv4		= $details['ipv4'];
        $this->ipv6		= $details['ipv6'];
        $this->gamePort	= $details['game_port'];
        $this->owner	= $details['owner_id'];
    }

    function GetId() {
        return $this->id;
    }
    function GetTitle() {
        return $this->title;
    }
    function GetAddress() {
        return $this->address;
    }
    function GetIp( $ipv4 = true ) {
        if($ipv4){
            return $this->ipv4;
        }else{
            return $this->ipv6;
        }
    }
    function GetGamePort() {
        return $this->gamePort;
    }
    function GetOwnerId() {
        return $this->owner;
    }
}

?>