<?php

// Set server error output
error_reporting(E_ALL | E_STRICT);

include "config.php";
include "libs/serverpackage.class.php";

$dblink = new mysqli( "mysql.cppmonkey.net", $dbuser, $dbpass, $dbname );

$serverPackage = new CServerPackage($_GET);

if ($serverPackage->IsUptodate())
    echo "true";
else
    echo "false";

?>