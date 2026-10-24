<?php
/* itchekuser.inc
 * Strictly checks that id_user is provided, authenticated, and owns/has recipe in cookbook
 */
if (!defined('IN_CB')) {
    define('IN_CB', true);
}

if (!isset($_POST['iduser']) || trim($_POST['iduser']) === '') {
    exitfail("No user ID provided");
}

$iduser_req = trim($_POST['iduser']);

if (!isset($_POST['pwd']) || trim($_POST['pwd']) === '') {
    exitfail("No password provided");
}

$pwd_req = trim($_POST['pwd']);

if (!preg_match($pat_pwd, $pwd_req)) {
    exitfail("Error in pwd format");
}
if (!preg_match($pat_uuid, $iduser_req)) {
    exitfail("Error in Id user format");
}

if (isset($idrecipe_req) && !empty($idrecipe_req)) {
    if (!checkrecipeincb($cxn, $idrecipe_req, $iduser_req)) {
        exitfail("Recipe not in user cookbook");
    }
}

if (!checkpwd($cxn, $iduser_req, $pwd_req)) {
    exitfail("Wrong pwd for user");
}

debugtrace("User and pwd checked successfully for " . $iduser_req);
?>
