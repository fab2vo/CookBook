<?php
/* Program: createnewfamily.php
 * Create new user and family in userdb
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$family_req = $_POST['family'] ?? '';
$name_req   = $_POST['name'] ?? '';
$pass_req   = $_POST['pwd'] ?? '';
$iduser_req = $_POST['iduser'] ?? '';

if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");
if (!preg_match($pat_pwd, $pass_req)) exitfail("Error in password");
if (!preg_match($pat_family, $family_req)) exitfail("Error in family");
if (!preg_match($pat_name, $name_req)) exitfail("Error in name");

// Check if family already exists
$stmt = mysqli_prepare($cxn, "SELECT pknum FROM userdb WHERE family = ?");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "s", $family_req);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

if ($result && mysqli_num_rows($result) > 0) {
    mysqli_stmt_close($stmt);
    exitfail("Family already exists");
}
mysqli_stmt_close($stmt);

$stmt = mysqli_prepare($cxn, "INSERT INTO `userdb` (`id_user`, `name`, `family`, `last_sync`, `pass`) VALUES (?, ?, ?, NOW(), ?)");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "ssss", $iduser_req, $name_req, $family_req, $pass_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) exitfail("Failed to insert new family");

echo $found;
debugtrace("Success creating new family");
exitsuccess($cxn);
?>
