<?php

include "config.php";

include "minecraft.class.php";

$servers = new Minecraft();
$servers->BuildServerList();

$servers->DumpJSON();

?>