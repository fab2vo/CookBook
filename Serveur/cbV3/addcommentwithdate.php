<?php
/* Program: addcommentwithdate.php
 * Create new comment for a recipe with explicit date
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$iduser_req   = $_POST['idfrom'] ?? '';
$comment_req  = trim($_POST['comment'] ?? '');
$date_req     = $_POST['date'] ?? '';

if (!preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe");
if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");
if (!preg_match($pat_date, $date_req)) exitfail("Error in date format");
if ($comment_req === "..." || $comment_req === "") exitfail("Error in comment");
if (!preg_match($pat_etape, $comment_req)) exitfail("Error in comment format");

include("itchekpwd.inc");

// Check if recipe exists
$rows = db_prepared_select_all($cxn, "SELECT id_recipe FROM `recipedb` WHERE id_recipe = ?", "s", array($idrecipe_req));
if (!$rows || count($rows) !== 1) {
    exitfail("No / too many recipe found");
}

// Check if user exists
$rows = db_prepared_select_all($cxn, "SELECT id_user FROM userdb WHERE id_user = ?", "s", array($iduser_req));
if (!$rows || count($rows) !== 1) {
    exitfail("No / too many user found");
}

// Check if comment already exists
$rows = db_prepared_select_all($cxn, "SELECT pknum FROM commentsdb WHERE id_user = ? AND id_recipe = ? AND date_comment = ?", "sss", array($iduser_req, $idrecipe_req, $date_req));
if ($rows && count($rows) > 0) {
    exitfail("Comment already exists");
}

// Insert comment
$stmt = mysqli_prepare($cxn, "INSERT INTO `commentsdb` (`id_recipe`, `comment`, `id_user`, `date_comment`) VALUES (?, ?, ?, ?)");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "ssss", $idrecipe_req, $comment_req, $iduser_req, $date_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) exitfail("Insert failed");

echo $found;
exitsuccess($cxn);
?>
