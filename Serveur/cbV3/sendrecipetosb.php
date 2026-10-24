<?php
/* Program: sendrecipetosb.php
 * Make an entry into cookbook with status Submitted
 * Legacy endpoint (not called by the current app): sender authentication is mandatory
 * and the sender must have the recipe in his own cookbook.
 */

include("ikonexct007.inc");
error_log("CookBook: legacy endpoint called: " . basename(__FILE__));

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    echo $notfound;
    exit;
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$idto_req     = $_POST['iduser'] ?? '';
$idfrom_req   = $_POST['idfrom'] ?? '';
$message_req  = trim($_POST['message'] ?? '');

if (!preg_match($pat_uuid, $idto_req) || !preg_match($pat_uuid, $idrecipe_req) || !preg_match($pat_uuid, $idfrom_req)) {
    echo $notfound;
    exit;
}
if (!preg_match($pat_etape, $message_req)) {
    echo $notfound;
    exit;
}

$iduser_req = $idfrom_req;
include("itchekpwd.inc");

if (!checkrecipeincb($cxn, $idrecipe_req, $idfrom_req)) {
    exitfail("Recipe not in sender cookbook");
}

$rows = db_prepared_select_all($cxn, "SELECT id_user FROM userdb WHERE id_user = ?", "s", array($idto_req));
if (!$rows || count($rows) !== 1) {
    exitfail("Recipient not found");
}

if (checkrecipeincb($cxn, $idrecipe_req, $idto_req)) {
    exitfail("Recipe already in recipient cookbook");
}

$status_recipe = "Submitted";
$stmt = mysqli_prepare($cxn, "INSERT INTO `cookbooks` (`id_user`, `id_recipe`, `status`, `message`, `id_from`) VALUES (?, ?, ?, ?, ?)");
if (!$stmt) {
    echo $notfound;
    exit;
}

mysqli_stmt_bind_param($stmt, "sssss", $idto_req, $idrecipe_req, $status_recipe, $message_req, $idfrom_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) {
    echo $notfound;
    exit;
}

echo $found;
mysqli_close($cxn);
?>
