<?php
/* Program: getnotes.php
 * Download ratings related to user's cookbook
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$iduser_req = $_POST['iduser'] ?? '';
if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");

include("itchekpwd.inc");

$sql = "SELECT notesdb.id_recipe, note, notesdb.id_user, date_note, family, name
        FROM notesdb
        INNER JOIN cookbooks ON cookbooks.id_recipe = notesdb.id_recipe
        INNER JOIN userdb ON userdb.id_user = notesdb.id_user
        WHERE cookbooks.id_user = ?";

$rows = db_prepared_select_all($cxn, $sql, "s", array($iduser_req));
if ($rows === false) exitfail("No result");

echo json_encode($rows);
debugtrace("Number of notes : " . count($rows));
exitsuccess($cxn);
?>
