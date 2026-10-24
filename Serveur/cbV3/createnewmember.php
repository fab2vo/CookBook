<?php
/* Program: createnewmember.php
 * Create new user in userdb and populate initial cookbook
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

// Verify family password
$stmt = mysqli_prepare($cxn, "SELECT pass FROM userdb WHERE family = ? GROUP BY pass");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "s", $family_req);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

if (!$result || mysqli_num_rows($result) === 0) {
    mysqli_stmt_close($stmt);
    exitfail("Family not found");
}

$family_pwd_ok = false;
while ($row = mysqli_fetch_assoc($result)) {
    $stored_pass = $row['pass'];
    if (pwdmatches($pass_req, $stored_pass)) {
        $family_pwd_ok = true;
        break;
    }
}
mysqli_stmt_close($stmt);

if (!$family_pwd_ok) {
    exitfail("Wrong family password");
}

$stmt = mysqli_prepare($cxn, "INSERT INTO `userdb` (`id_user`, `name`, `family`, `last_sync`, `pass`) VALUES (?, ?, ?, NOW(), ?)");
if (!$stmt) exitfail("Prepared statement error");

mysqli_stmt_bind_param($stmt, "ssss", $iduser_req, $name_req, $family_req, $pass_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) exitfail("Failed to insert user");

// Populate user's cookbook with family recipes
$sql = "SELECT cookbooks.id_user, id_recipe, status, message, id_from
        FROM `cookbooks`
        LEFT JOIN userdb ON cookbooks.id_user = userdb.id_user
        WHERE userdb.family = ? AND cookbooks.status = 'Visible'
        GROUP BY id_recipe";

$stmt = mysqli_prepare($cxn, $sql);
mysqli_stmt_bind_param($stmt, "s", $family_req);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

if ($result) {
    while ($ligne = mysqli_fetch_assoc($result)) {
        $idrec = $ligne["id_recipe"];
        $st = $ligne["status"];
        $me = $ligne["message"];
        $idf = $ligne["id_from"];

        $stmt2 = mysqli_prepare($cxn, "INSERT INTO `cookbooks` (`id_user`, `id_recipe`, `status`, `message`, `id_from`) VALUES (?, ?, ?, ?, ?)");
        if ($stmt2) {
            mysqli_stmt_bind_param($stmt2, "sssss", $iduser_req, $idrec, $st, $me, $idf);
            mysqli_stmt_execute($stmt2);
            mysqli_stmt_close($stmt2);
        }
    }
}
mysqli_stmt_close($stmt);

echo $found;
debugtrace("Success");
exitsuccess($cxn);
?>
