<?php

include "config.php";

include "./lib/minecraft.class.php";

$servers = new Minecraft();
$servers->BuildServerList();

$servers->DumpJSON();

?>