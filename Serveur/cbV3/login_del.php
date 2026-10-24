<?php
/* Program: login_del.php
 * Login page for account deletion request
 */
session_start();
include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!isset($_POST['family']) || $_POST['family'] === 'nofamily') {
    include('login_form_del.inc');
    exit;
}

$family_req   = trim($_POST['family']);
$member_req   = trim($_POST['member'] ?? '');
$password_req = trim($_POST['password'] ?? '');

$cxn = db_connect($host, $user, $password, $nombase, $port);
if (!$cxn) {
    $message = "Erreur de connexion BDD";
    include("login_form_del.inc");
    exit;
}

$stmt = mysqli_prepare($cxn, "SELECT id_user, pass FROM userdb WHERE family = ? AND name = ?");
$login_ok = false;
$id_user = null;

if ($stmt) {
    mysqli_stmt_bind_param($stmt, "ss", $family_req, $member_req);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);
    if ($result && mysqli_num_rows($result) === 1) {
        $row = mysqli_fetch_assoc($result);
        $stored_pass = $row['pass'];
        $id_user = $row['id_user'];

        if (pwdmatches($password_req, $stored_pass)) {
            $login_ok = true;
        }
    }
    mysqli_stmt_close($stmt);
}

if ($login_ok) {
    $_SESSION['auth'] = 'yes';
    $_SESSION['family'] = $family_req;
    $_SESSION['member'] = $member_req;
    $_SESSION['id_user'] = $id_user;

    header("Location: userdelpage.php");
    exit();
} else {
    $message = 'Utilisateur non trouvé<br>';
    include("login_form_del.inc");
}
?>
