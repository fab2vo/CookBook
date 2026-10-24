<?php
/* ikonexct007.inc
 * Core functions, security validations, and portable database helpers (PHP 8.1 compatible)
 */
if (!defined('IN_CB')) {
    define('IN_CB', true);
}

include_once("idconnect.inc");

// Never display PHP errors to clients (they may reveal paths or SQL), log them instead
ini_set('display_errors', '0');
ini_set('log_errors', '1');
// PHP errors and the "disabled/legacy endpoint called" traces go to cb_private/php_errors.log
ini_set('error_log', CB_PRIVATE_DIR . '/php_errors.log');

// Disable strict exception throwing in PHP 8.1 for MySQLi
mysqli_report(MYSQLI_REPORT_OFF);

date_default_timezone_set('Europe/Paris');

// Debug log is written outside the web root, next to the private config
$debugfilename = CB_PRIVATE_DIR . '/debug.log';

$pat_family = "/^[a-zA-Z0-9 !\?_\-\(\)\.À-ÿ]{3,50}$/u";
$pat_pwd    = "/^.{1,50}$/u";
$pat_name   = "/^[a-zA-Z0-9 _\-\(\)\.À-ÿ]{1,50}$/u";
$pat_uuid   = "/^[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}$/i";
$pat_etape  = "/^[\s\S]{0,500}$/u";
$pat_ing    = "/^[\s\S]{0,255}$/u";
$pat_date   = "/^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/";
$pat_num    = "/^[0-9]{1,9}$/";

$debug = 0;
$fcaller = basename($_SERVER['SCRIPT_FILENAME'] ?? '');
// Add a script name here only temporarily, while investigating an issue
$debugphpfiles = array();

if (in_array($fcaller, $debugphpfiles)) {
    $debug = 1;
}

function debugtrace($sin) {
    global $debug, $debugfilename;
    if ($debug == 1) {
        $open = @file_get_contents($debugfilename);
        $open .= date('Y-m-d H:i:s') . " - " . $sin . "\r\n";
        @file_put_contents($debugfilename, $open);
    }
}

function exitfail($m) {
    global $notfound;
    debugtrace("FAIL: " . $m);
    if ($notfound === false) {
        echo "FAIL: " . $m;
    } else {
        echo $notfound;
    }
    exit;
}

function exitok($m) {
    global $found;
    debugtrace("OK: " . $m);
    echo $found;
    exit;
}

function exitsuccess($cxn) {
    if ($cxn) {
        mysqli_close($cxn);
    }
    debugtrace("Success");
    exit;
}

/**
 * Connect to MySQL safely across PHP versions and server ports
 */
function db_connect($host, $user, $password, $nombase, $port = null) {
    $cxn = @mysqli_connect($host, $user, $password, $nombase, $port);
    if (!$cxn && $port !== null) {
        $cxn = @mysqli_connect($host, $user, $password, $nombase);
    }
    if ($cxn) {
        mysqli_set_charset($cxn, 'utf8');
    }
    return $cxn;
}

/**
 * Execute a SELECT prepared statement safely on any PHP server (with or without mysqlnd)
 */
function db_prepared_select_all($cxn, $sql, $types = "", $params = array()) {
    $stmt = mysqli_prepare($cxn, $sql);
    if (!$stmt) {
        return false;
    }
    if (!empty($types) && !empty($params)) {
        mysqli_stmt_bind_param($stmt, $types, ...$params);
    }
    if (!mysqli_stmt_execute($stmt)) {
        mysqli_stmt_close($stmt);
        return false;
    }

    $res = mysqli_stmt_get_result($stmt);
    if ($res !== false) {
        $rows = array();
        while ($r = mysqli_fetch_assoc($res)) {
            $rows[] = $r;
        }
        mysqli_stmt_close($stmt);
        return $rows;
    }

    // Fallback when mysqlnd driver is not installed on server
    mysqli_stmt_store_result($stmt);
    $meta = mysqli_stmt_result_metadata($stmt);
    if (!$meta) {
        mysqli_stmt_close($stmt);
        return array();
    }

    $row = array();
    $parameters = array($stmt);
    while ($field = mysqli_fetch_field($meta)) {
        $row[$field->name] = null;
        $parameters[] = &$row[$field->name];
    }

    call_user_func_array('mysqli_stmt_bind_result', $parameters);

    $results = array();
    while (mysqli_stmt_fetch($stmt)) {
        $x = array();
        foreach ($row as $key => $val) {
            $x[$key] = $val;
        }
        $results[] = $x;
    }

    mysqli_stmt_close($stmt);
    return $results;
}

// Value written in userdb.pass by userdelpage.php when an account is closed
$closed_account_pass = 'compteferme';

/**
 * Compare a submitted password with a stored one (hashed or legacy plain text).
 * A closed account never matches.
 */
function pwdmatches($pwd_req, $stored_pass) {
    global $closed_account_pass;
    if ($stored_pass === null || $stored_pass === '' || $stored_pass === $closed_account_pass) {
        return false;
    }
    return password_verify($pwd_req, $stored_pass) || $stored_pass === $pwd_req;
}

/**
 * Check password securely using prepared statements.
 * Validates $pwd_req against $iduser_req.
 * When $idrecipe_scope is given, also accepts the password of any user having that recipe
 * in his cookbook: the app sends the recipe OWNER id with the CONNECTED user password when
 * a recipe received from another family is modified (updateorcreaterecipe, uploadphotointorecipe).
 */
function checkpwd($cxn, $iduser_req, $pwd_req, $idrecipe_scope = null) {
    if (empty($pwd_req)) {
        return false;
    }

    // 1. Primary check: verify password against requested $iduser_req
    if (!empty($iduser_req)) {
        $rows = db_prepared_select_all($cxn, "SELECT pass FROM userdb WHERE id_user = ?", "s", array($iduser_req));
        if ($rows && count($rows) > 0 && pwdmatches($pwd_req, $rows[0]['pass'])) {
            return true;
        }
    }

    // 2. Scoped fallback: password of a user who has this recipe in his cookbook
    if (!empty($idrecipe_scope)) {
        $sql = "SELECT u.pass FROM cookbooks c
                INNER JOIN userdb u ON u.id_user = c.id_user
                WHERE c.id_recipe = ?";
        $rows2 = db_prepared_select_all($cxn, $sql, "s", array($idrecipe_scope));
        if ($rows2) {
            foreach ($rows2 as $r) {
                if (pwdmatches($pwd_req, $r['pass'])) {
                    return true;
                }
            }
        }
    }

    return false;
}

/**
 * Find owner user_id of a recipe
 */
function finduserofrecipe($cxn, $idrecipe_req) {
    $rows = db_prepared_select_all($cxn, "SELECT id_user FROM recipedb WHERE id_recipe = ?", "s", array($idrecipe_req));
    if (!$rows || count($rows) !== 1) {
        exitfail("No or too many users for recipe");
    }
    return $rows[0]['id_user'];
}

/**
 * Check if recipe is in user's cookbook
 */
function checkrecipeincb($cxn, $idrecipe_req, $iduser_req) {
    $rows = db_prepared_select_all($cxn, "SELECT pknum FROM cookbooks WHERE id_user = ? AND id_recipe = ?", "ss", array($iduser_req, $idrecipe_req));
    return ($rows && count($rows) > 0);
}
?>
