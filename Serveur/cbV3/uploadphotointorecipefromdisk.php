<?php
/* Program: uploadphotointorecipefromdisk.php
 * DISABLED: replaced a recipe photo by a server file without any authentication.
 * Not called by the app; the web portal uses ixuploadphotointorecipefromdisk.php.
 * Calls are logged to detect a remaining user before deleting this file.
 */
include("ikonexct007.inc");
error_log("CookBook: disabled endpoint called: " . basename(__FILE__) . " from " . ($_SERVER['REMOTE_ADDR'] ?? '?'));
exitfail("Endpoint disabled");
?>
