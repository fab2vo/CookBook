<?php
/* itchekpwd.inc
 * Strictly enforces password verification for all requests
 */
if (!defined('IN_CB')) {
    define('IN_CB', true);
}

if (!isset($_POST['pwd']) || trim($_POST['pwd']) === '') {
    exitfail("No password provided!");
}

$pwd_req = trim($_POST['pwd']);

if (!isset($pat_pwd) || !preg_match($pat_pwd, $pwd_req)) {
    exitfail("Error in pwd format");
}

if (!isset($iduser_req) || empty($iduser_req)) {
    exitfail("No user ID provided for password verification");
}

// $pwd_recipe_scope may be set by the caller (recipe id) to accept the password
// of a user having that recipe in his cookbook, see checkpwd()
if (!checkpwd($cxn, $iduser_req, $pwd_req, $pwd_recipe_scope ?? null)) {
    exitfail("Wrong pwd for user ID");
}

debugtrace("Password checked successfully for user " . $iduser_req);
?>
