<?php
/* Program: getcommentsofrecipe.php
 * Download comments related to a specific recipe
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
if (!preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe");

include("itchekuser.inc");

$sql = "SELECT comment, date_comment, commentsdb.id_user, family, name
        FROM commentsdb
        INNER JOIN userdb ON userdb.id_user = commentsdb.id_user
        WHERE commentsdb.id_recipe = ?";

$rows = db_prepared_select_all($cxn, $sql, "s", array($idrecipe_req));
if ($rows === false) exitfail("No result");

echo json_encode($rows);
exitsuccess($cxn);
?>
