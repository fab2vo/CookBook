<?php
/* Program: uploadphotointorecipe.php
 * Update photo in recipedb
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$image_req    = $_POST['image'] ?? '';
$date_req     = $_POST['date'] ?? '';

if (!preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe");
if (!preg_match($pat_date, $date_req)) exitfail("Error in date format");

$iduser_req = finduserofrecipe($cxn, $idrecipe_req);
// The app sends no user id: accept the owner password or the one of a user having the recipe
$pwd_recipe_scope = $idrecipe_req;
include("itchekpwd.inc");

$stmt = mysqli_prepare($cxn, "UPDATE `recipedb` SET `photo` = ?, `lastupdate_photo` = ? WHERE id_recipe = ?");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "sss", $image_req, $date_req, $idrecipe_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) exitfail("Failed to update photo");

echo $found;
exitsuccess($cxn);
?>
