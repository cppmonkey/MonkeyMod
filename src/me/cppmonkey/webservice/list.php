<?php

include "config.php";
include "minecraft.class.php";

$minecraft = new Minecraft();

$minecraft->BuildServerList();
$minecraft->BuildPlayerLists();

$minecraft->Dump();

?>