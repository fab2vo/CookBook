<?php
/* Program: moverecipetocb.php
 * DISABLED: let any user add any recipe to his cookbook (bypassing requests),
 * with unchecked status and sender. Not called by the app.
 * Calls are logged to detect a remaining user before deleting this file.
 */
include("ikonexct007.inc");
error_log("CookBook: disabled endpoint called: " . basename(__FILE__) . " from " . ($_SERVER['REMOTE_ADDR'] ?? '?'));
exitfail("Endpoint disabled");
?>
