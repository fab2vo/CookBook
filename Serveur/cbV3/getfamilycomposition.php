<?php
/* Program: getfamilycomposition.php
 * Get family members for family login / display
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$family_req = $_POST['family'] ?? '';
if (!preg_match($pat_family, $family_req)) exitfail("Error in family");

$rows = db_prepared_select_all($cxn, "SELECT id_user, name, last_sync, pass FROM userdb WHERE family = ?", "s", array($family_req));

if ($rows === false || count($rows) === 0) {
    exitfail("Nobody in family");
}

echo json_encode($rows);
exitsuccess($cxn);
?>
