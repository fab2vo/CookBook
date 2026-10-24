<?php
/* Program: getnotesofrecipe.php
 * Download ratings related to a specific recipe
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
if (!preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe");

include("itchekuser.inc");

$sql = "SELECT note, date_note, notesdb.id_user, family, name
        FROM notesdb
        INNER JOIN userdb ON userdb.id_user = notesdb.id_user
        WHERE notesdb.id_recipe = ?";

$rows = db_prepared_select_all($cxn, $sql, "s", array($idrecipe_req));
if ($rows === false) exitfail("No result");

echo json_encode($rows);
exitsuccess($cxn);
?>
