<?php
/* Program: acceptrecipe.php
 * Update cookbooks to change status submitted into visible
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$iduser_req   = $_POST['iduser'] ?? '';

if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");
if (!preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe");

include("itchekpwd.inc");

$stmt = mysqli_prepare($cxn, "UPDATE `cookbooks` SET `status` = 'Visible' WHERE id_recipe = ? AND id_user = ?");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "ss", $idrecipe_req, $iduser_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) exitfail("No result");

echo $found;
exitsuccess($cxn);
?>
