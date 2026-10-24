<?php
/* Program: sendmail.php
 * Submit recipe to another user by member/family name
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$idfrom_req   = $_POST['idfrom'] ?? '';
$family_req   = trim($_POST['family'] ?? '');
$membre_req   = trim($_POST['membre'] ?? '');
$message_req  = trim($_POST['message'] ?? '');

if (!preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe");
if (!preg_match($pat_uuid, $idfrom_req)) exitfail("Error in Id user from");
if (!preg_match($pat_family, $family_req)) exitfail("Error in family");
if (!preg_match($pat_name, $membre_req)) exitfail("Error in member");
if (!preg_match($pat_etape, $message_req)) exitfail("Error in message");

$iduser_req = $idfrom_req;
include("itchekpwd.inc");

// The sender can only share a recipe he has in his own cookbook
if (!checkrecipeincb($cxn, $idrecipe_req, $idfrom_req)) {
    exitfail("Recipe not in sender cookbook");
}

// Find recipient user
$rows = db_prepared_select_all($cxn, "SELECT `id_user` FROM `userdb` WHERE name = ? AND family = ?", "ss", array($membre_req, $family_req));
if (!$rows || count($rows) !== 1) {
    exitfail("No or too many users found");
}

$iduser_to = $rows[0]['id_user'];

// Check if recipe is already submitted
$rows2 = db_prepared_select_all($cxn, "SELECT id_user FROM `cookbooks` WHERE id_user = ? AND id_recipe = ?", "ss", array($iduser_to, $idrecipe_req));
if ($rows2 && count($rows2) > 0) {
    exitfail("Request already exists");
}

// Create entry
$stmt = mysqli_prepare($cxn, "INSERT INTO `cookbooks` (`id_user`, `id_recipe`, `status`, `message`, `id_from`) VALUES (?, ?, 'Submitted', ?, ?)");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "ssss", $iduser_to, $idrecipe_req, $message_req, $idfrom_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) exitfail("Failed to insert recipe in target cookbook");

echo $found;
exitsuccess($cxn);
?>
