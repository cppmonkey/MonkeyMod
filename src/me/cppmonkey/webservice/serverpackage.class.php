<?php

class CServerPackage
{
    var $name;
    var $version;
    var $build;

    function CServerPackage() {
        $this->name = $_GET["package"];
        $this->version = $_GET["version"];
        $this->build = $_GET["build"];
    }

    function GetName()
    {
        return $this->name;
    }

    function GetVersion()
    {
        return $this->version;
    }

    function GetBuild()
    {
        return $this->build;
    }

    function IsUptodate( $table = "mc_packages" )
    {
        global $dblink;

        $query = sprintf( "SELECT *
                FROM `%s`
                WHERE `name` = '%s'
                ",
                $table,
                $this->name );

        if( $results = $dblink->query( $query ) )
        {
            while ( $record = $results->fetch_assoc() )
            {
                //$record['id'];
                //$record['title'];
                //$record['address'];
                //$record['game_port'];
                //$record['admin_port'];
                //$record['admin_password'];
                if( $this->version < $record['version'] || $this ->build < $record['build'] )
                    return false;
            }
        }
        return true;
    }

    function GetUpdateCmd( $table = "mc_packages" )
    {
        global $dblink;

        $query = sprintf( "SELECT *
                FROM `%s`
                WHERE `name` = '%s'
                ",
                $table,
                $this->name );

        if( $results = $dblink->query( $query ) )
        {
            while ( $record = $results->fetch_assoc() )
            {
                //$record['id'];
                //$record['title'];
                //$record['address'];
                //$record['game_port'];
                //$record['admin_port'];
                //$record['admin_password'];
                return $record['update_cmd'];
            }
        }
        return false;
    }

}

?>