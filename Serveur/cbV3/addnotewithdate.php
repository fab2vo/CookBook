<?php
/* Program: addnotewithdate.php
 * Create new rating for a recipe with date
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$iduser_req   = $_POST['idfrom'] ?? '';
$note_req     = trim($_POST['note'] ?? '');
$date_req     = $_POST['date'] ?? '';

if (!preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe");
if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");

$note_val = (int)$note_req;
if ($note_val < 1 || $note_val > 5) exitfail("Error in note");

if (!preg_match($pat_date, $date_req)) exitfail("Error in date");

include("itchekpwd.inc");

// The app re-sends every note it does not find in getnotesofrecipe: an identical note
// (same user, recipe and date) is already stored, so answer OK without inserting it again
$rows = db_prepared_select_all($cxn, "SELECT pknum FROM notesdb WHERE id_user = ? AND id_recipe = ? AND date_note = ?", "sss", array($iduser_req, $idrecipe_req, $date_req));
if ($rows && count($rows) > 0) {
    exitok("Note already exists");
}

$stmt = mysqli_prepare($cxn, "INSERT INTO notesdb (`id_recipe`, `note`, `id_user`, `date_note`) VALUES (?, ?, ?, ?)");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "siss", $idrecipe_req, $note_val, $iduser_req, $date_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) exitfail("No note added");

echo $found;
exitsuccess($cxn);
?>
