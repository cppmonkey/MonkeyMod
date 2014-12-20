<?php

include "serverpackage.class.php";

class MinecraftServer {
	var $id;
	var $title;
	var $address, $gamePort, $adminPort, $adminPass;
	var $ip;
	var $packages;

	var $rcon;


    function MinecraftServer( $details ) {
		$this->id		= $details['id'];
		$this->title		= $details['title'];
		$this->address	= $details['address'];
		$this->ip		= $details['server_ip'];
		$this->gamePort	= $details['game_port'];
		$this->adminPort	= $details['admin_port'];
		$this->adminPass	= $details['admin_password'];
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
    function GetIp() {
        return $this->ip;
    }
    function GetGamePort() {
        return $this->gamePort;
    }
    function GetAdminPort() {
        return $this->adminPort;
    }
    function GetAdminPass() {
        return $this->adminPass;
    }
    function GetOwnerId() {
        return $this->owner;
	}
	
    function UpdateString() {

	}

    function ValidateVips() {
        if ($this->owner > 0) {
			return true;
		}
		return false;
	}
}

?>