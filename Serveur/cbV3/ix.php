<?php
/* Program: ix.php
 * Interface web pour accès des recettes
 */

error_reporting(E_ALL);
ini_set('display_errors', 0);

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection failed");
}

session_start();

if (isset($_GET['recipenum'])) {
    $recipenum = (int)$_GET['recipenum'];
    $_SESSION['pknum'] = $recipenum;
} else {
    $recipenum = 0;
}

if (isset($_GET['status'])) {
    debugtrace("Status=" . $_GET['status']);
    if ($_GET['status'] == "logout") {
        $_SESSION['auth'] = 'no';
        $_SESSION['family'] = "";
        $_SESSION['pknum'] = 0;
        $expire = time() + (60 * 60 * 24 * 30);
        setcookie("cb_family", "", $expire, "/");
        $_SESSION['member'] = "";
        setcookie("cb_member", "", $expire, "/");
        $_SESSION['id_user'] = "";
        session_write_close();
        header("Location: ix.php");
        exit;
    }
}

if (isset($_SESSION['member']) && ($_SESSION['auth'] == "yes")) {
    debugtrace("Already logged as " . $_SESSION['member']);
    $member = $_SESSION['member'];
    $family = $_SESSION['family'];
    $id_user_session = $_SESSION['id_user'];
} else {
    if (isset($_GET['status']) && ($_GET['status'] == "login")) {
        $family_req   = trim($_POST['family'] ?? '');
        $member_req   = trim($_POST['member'] ?? '');
        $password_req = trim($_POST['password'] ?? '');

        $sql = "SELECT id_user, pass, family FROM userdb WHERE family = ? AND name = ?";
        $rows = db_prepared_select_all($cxn, $sql, "ss", array($family_req, $member_req));
        $login_ok = false;
        $id_user = null;

        if ($rows && count($rows) === 1) {
            $stored_pass = $rows[0]['pass'];
            $id_user = $rows[0]['id_user'];

            if (pwdmatches($password_req, $stored_pass)) {
                $login_ok = true;
            }
        }

        if (!$login_ok) {
            $message = 'Utilisateur ou mot de passe incorrect';
            session_write_close();
            header("Location: ix.php?logmessage=" . urlencode($message));
            exit;
        }

        $_SESSION['auth'] = 'yes';
        $_SESSION['family'] = $family_req;
        $expire = time() + (60 * 60 * 24 * 30);
        setcookie("cb_family", $family_req, $expire, "/");
        $_SESSION['member'] = $member_req;
        setcookie("cb_member", $member_req, $expire, "/");
        $_SESSION['id_user'] = $id_user;

        session_write_close();
        header("Location: ix.php", true, 301);
        exit();
    } else {
        unset($message);
        if (isset($_GET['logmessage'])) $message = $_GET['logmessage'];
        include('ixlogform.inc');
        exit();
    }
}

$display = "list";
$id_user = $_SESSION['id_user'];
include("ixdisheader.inc");

if (isset($_SESSION['pknum']) && ($_SESSION['pknum'] > 0)) {
    $recipenum = (int)$_SESSION['pknum'];
    if (findPknum($recipenum, $id_user, $cxn) == "none") {
        fillmain("Recipe cannot be displayed");
        $_SESSION['pknum'] = 0;
        exit;
    }
}

if ($recipenum > 0) {
    if (isset($_GET['edit'])) {
        $display = "edit";
    } else {
        $display = "display";
    }
}

if (isset($_GET['logmessage'])) $message = $_GET['logmessage'];
else $message = "";

switch ($display) {
    case 'list':
        include("ixlist.inc");
        fillmain($message);
        break;
    case 'erreur':
        fillmain($errecipe ?? "Error");
        break;
    case 'edit':
        include("ixeditrecipe.inc");
        fillmain($message);
        break;
    case 'display':
        include("ixdisplayrecipe.inc");
        fillmain("");
        break;
    default:
        debugtrace("Wrong value of display");
}
exit;

function fillmain($m) {
    echo '<br><center><p class="errormessage">' . htmlspecialchars($m, ENT_QUOTES, 'UTF-8') . '</p></center>';
    echo '</td></tr></table>
            <a href="ix.php">Home</a>
            </body></html>';
    $_SESSION['pknum'] = 0;
    return;
}

function findPknum($num, $user, $cxn) {
    $sql = "SELECT r.title FROM recipedb AS r
            LEFT JOIN cookbooks AS c ON r.id_recipe = c.id_recipe
            WHERE r.pknum = ? AND c.id_user = ?";
    $rows = db_prepared_select_all($cxn, $sql, "is", array($num, $user));
    if (!$rows || count($rows) !== 1) {
        return "none";
    }
    return $rows[0]['title'];
}
?>
