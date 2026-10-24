<?php
/* Program: addnote.php
 * Create new rating for a recipe
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$iduser_req   = $_POST['idfrom'] ?? '';
$note_req     = trim($_POST['note'] ?? '');

if (!preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe");
if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");

$note_val = (int)$note_req;
if ($note_val < 1 || $note_val > 5) exitfail("Error in note");

include("itchekpwd.inc");

$stmt = mysqli_prepare($cxn, "INSERT INTO notesdb (`id_recipe`, `note`, `id_user`, `date_note`) VALUES (?, ?, ?, NOW())");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "sis", $idrecipe_req, $note_val, $iduser_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) exitfail("No note added");

echo $found;
exitsuccess($cxn);
?>
