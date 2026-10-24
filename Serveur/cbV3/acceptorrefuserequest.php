<?php
/* Program: acceptorrefuserequest.php
 * Manage the acceptance or denial of a recipe request
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$iduser_req = $_POST['iduser'] ?? '';
$pknum_req  = $_POST['pknum'] ?? '';
$status_req = $_POST['status'] ?? '';
$message_req = $_POST['message'] ?? "Message par defaut";

$accepted = "ACCEPTED";
$refused  = "DENIED";

if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");
if (!preg_match($pat_num, $pknum_req)) exitfail("Pknum inadequate");
if (!in_array($status_req, array($accepted, $refused))) exitfail("Error in status request");
if (!preg_match($pat_etape, $message_req)) exitfail("Error in message");

include("itchekpwd.inc");

// Find request
$rows = db_prepared_select_all($cxn, "SELECT id_recipe, id_from, status FROM `requestdb` WHERE pknum = ?", "i", array($pknum_req));
if (!$rows || count($rows) === 0) {
    exitfail("Request not found");
}

$idrecipe_req = $rows[0]['id_recipe'];
$idfrom_req   = $rows[0]['id_from'];
$status_db    = $rows[0]['status'];

// Only the recipe owner may accept OR deny a request on his recipe
$rows3 = db_prepared_select_all($cxn, "SELECT id_user FROM `recipedb` WHERE id_recipe = ?", "s", array($idrecipe_req));
if (!$rows3 || count($rows3) !== 1) {
    exitfail("Recipe not unique");
}
if ($rows3[0]['id_user'] !== $iduser_req) {
    exitfail("User is not recipe owner");
}

if ($status_db == $accepted) {
    if ($status_req == $accepted) exitok("Already accepted!");
    if ($status_req == $refused) exitok("Cannot accept denied request");
}
if ($status_db == $refused) {
    if ($status_req == $accepted) exitok("Cannot deny accepted recipe");
    if ($status_req == $refused) exitok("Already denied");
}

if ($status_req === $refused) {
    $stmt = mysqli_prepare($cxn, "UPDATE `requestdb` SET `status` = ? WHERE pknum = ?");
    mysqli_stmt_bind_param($stmt, "si", $status_req, $pknum_req);
    mysqli_stmt_execute($stmt);
    mysqli_stmt_close($stmt);
    exitok("Request DENIED executed");
}

// Check if recipe is already in cookbook
$rows2 = db_prepared_select_all($cxn, "SELECT pknum FROM `cookbooks` WHERE id_recipe = ? AND id_user = ?", "ss", array($idrecipe_req, $idfrom_req));
if ($rows2 && count($rows2) > 0) {
    exitok("Recipe already present!");
}


// Add submitted recipe to user's cookbook
$status_recipe = "Submitted";
$stmt = mysqli_prepare($cxn, "INSERT INTO `cookbooks` (`id_user`, `id_recipe`, `status`, `message`, `id_from`) VALUES (?, ?, ?, ?, ?)");
mysqli_stmt_bind_param($stmt, "sssss", $idfrom_req, $idrecipe_req, $status_recipe, $message_req, $iduser_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) exitfail("Recipe not added");

// Update request status to ACCEPTED
$stmt = mysqli_prepare($cxn, "UPDATE `requestdb` SET `status` = ? WHERE pknum = ?");
mysqli_stmt_bind_param($stmt, "si", $status_req, $pknum_req);
mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

echo $found;
exitsuccess($cxn);
?>
