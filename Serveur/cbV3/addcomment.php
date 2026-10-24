<?php
/* Program: addcomment.php
 * Create new comment for a recipe
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    echo $notfound;
    exit;
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$iduser_req   = $_POST['idfrom'] ?? '';
$comment_req  = trim($_POST['comment'] ?? '');

if (!preg_match($pat_uuid, $idrecipe_req) || !preg_match($pat_uuid, $iduser_req)) {
    echo $notfound;
    exit;
}

include("itchekpwd.inc");

$stmt = mysqli_prepare($cxn, "INSERT INTO `commentsdb` (`id_recipe`, `comment`, `id_user`, `date_comment`) VALUES (?, ?, ?, NOW())");
if (!$stmt) {
    echo $notfound;
    exit;
}

mysqli_stmt_bind_param($stmt, "sss", $idrecipe_req, $comment_req, $iduser_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) {
    echo $notfound;
    exit;
}

echo $found;
mysqli_close($cxn);
?>
