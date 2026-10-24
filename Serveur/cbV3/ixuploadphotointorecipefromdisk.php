<?php
/* Program: ixuploadphotointorecipefromdisk.php
 * Web portal photo upload to recipe
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection failed");
}

session_start();
if (!isset($_SESSION['auth']) || $_SESSION['auth'] !== "yes") {
    $recipenum = (int)($_POST['pknum'] ?? 0);
    header("Location: ix.php?recipenum=" . $recipenum . "&edit=yes&logmessage=" . urlencode("Not authorized"));
    exit;
}

$recipenum = (int)($_POST['pknum'] ?? 0);
$idrecipe_req = $_POST['idrecipe'] ?? '';

if (!preg_match($pat_uuid, $idrecipe_req)) {
    header("Location: ix.php?recipenum=" . $recipenum . "&edit=yes&logmessage=" . urlencode("Invalid recipe ID"));
    exit;
}

// The logged-in member may only change the photo of a recipe of his cookbook
if (!checkrecipeincb($cxn, $idrecipe_req, $_SESSION['id_user'] ?? '')) {
    header("Location: ix.php?recipenum=" . $recipenum . "&edit=yes&logmessage=" . urlencode("Not authorized"));
    exit;
}

if (!isset($_FILES["file"]) || $_FILES["file"]["error"] !== UPLOAD_ERR_OK) {
    header("Location: ix.php?recipenum=" . $recipenum . "&edit=yes&logmessage=" . urlencode("Error uploading file"));
    exit;
}

if ($_FILES["file"]["size"] > 500000) {
    header("Location: ix.php?recipenum=" . $recipenum . "&edit=yes&logmessage=" . urlencode("Sorry, file is too large (> 500KB)"));
    exit;
}

$img = file_get_contents($_FILES["file"]["tmp_name"]);
if (!$img) {
    header("Location: ix.php?recipenum=" . $recipenum . "&edit=yes&logmessage=" . urlencode("Could not read uploaded image"));
    exit;
}

$image_req = base64_encode($img);

$stmt = mysqli_prepare($cxn, "UPDATE `recipedb` SET `photo` = ?, `lastupdate_photo` = NOW() WHERE id_recipe = ?");
if (!$stmt) {
    header("Location: ix.php?recipenum=" . $recipenum . "&edit=yes&logmessage=" . urlencode("Database error"));
    exit;
}

mysqli_stmt_bind_param($stmt, "ss", $image_req, $idrecipe_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

mysqli_close($cxn);
debugtrace("Success photo upload");
$message = "Reload cookbook on your smartphone";
header("Location: ix.php?recipenum=" . $recipenum . "&edit=yes&logmessage=" . urlencode($message));
exit;
?>
