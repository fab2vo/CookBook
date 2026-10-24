<?php
/* Program: deleterecipe.php
 * Remove recipe from user's cookbook
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

$stmt = mysqli_prepare($cxn, "DELETE FROM `cookbooks` WHERE id_user = ? AND id_recipe = ?");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "ss", $iduser_req, $idrecipe_req);
mysqli_stmt_execute($stmt);
$affected = mysqli_stmt_affected_rows($stmt);
mysqli_stmt_close($stmt);

if ($affected === 0) exitfail("No delete");

echo $found;
exitsuccess($cxn);
?>
