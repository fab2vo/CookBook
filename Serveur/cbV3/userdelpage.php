<?php
/* Program: userdelpage.php
 * Account deletion / closing page
 */
session_start();
include('ikonexct007.inc');

if (!isset($_SESSION['auth']) || $_SESSION['auth'] !== 'yes') {
    debugtrace("Userdelpage page: not authorized");
    header("Location: login_del.php");
    exit();
}

$id_user_session = $_SESSION['id_user'] ?? '';
$cxn = db_connect($host, $user, $password, $nombase, $port);

if (!$cxn) {
    echo 'Failed';
    exit;
}

$stmt = mysqli_prepare($cxn, "UPDATE `userdb` SET pass = 'compteferme' WHERE id_user = ?");
if ($stmt) {
    mysqli_stmt_bind_param($stmt, "s", $id_user_session);
    $res = mysqli_stmt_execute($stmt);
    mysqli_stmt_close($stmt);
    if (!$res) {
        echo 'Failed erasing account';
        exit;
    }
}

echo '<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="utf-8">
<title>Cookbook family</title>
</head>
<body style="background-color:MediumSeaGreen;">
<center>
<h1 style="color:Orange;">' . htmlspecialchars($_SESSION['member'] ?? '', ENT_QUOTES, 'UTF-8') . '@' . htmlspecialchars($_SESSION['family'] ?? '', ENT_QUOTES, 'UTF-8') . '</h1>
<p>Account erased ! Sad to see you leave !</p>
</center>
</body>
</html>';
?>
