<?php
/* Program: newrequest.php
 * Make an entry into requestdb with status ISSUED at current date
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$idfrom_req   = $_POST['idfrom'] ?? '';
$message_req  = trim($_POST['message'] ?? '');
$iduser_req   = $idfrom_req;

if (!preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe");
if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");
if (!preg_match($pat_etape, $message_req)) exitfail("Error in message");

include("itchekpwd.inc");

$rows = db_prepared_select_all($cxn, "SELECT `id_user` FROM `recipedb` WHERE id_recipe = ?", "s", array($idrecipe_req));
if (!$rows || count($rows) !== 1) {
    exitfail("No such recipe");
}

if ($rows[0]['id_user'] === $idfrom_req) {
    exitfail("requestor = recipe owner!");
}

$stmt = mysqli_prepare($cxn, "INSERT INTO requestdb (`id_recipe`, `id_from`, `date_creation`, `message`, `status`) VALUES (?, ?, NOW(), ?, 'ISSUED')");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "sss", $idrecipe_req, $idfrom_req, $message_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) exitfail("Failed to insert request");

echo $found;
exitsuccess($cxn);
?>
