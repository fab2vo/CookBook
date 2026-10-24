<?php
/* Program: getrequests.php
 * Download requests related to user
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$iduser_req = $_POST['iduser'] ?? '';
if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");

include("itchekpwd.inc");

$sql = "SELECT rq.pknum, rq.id_recipe, rq.id_from, u.family, u.name, date_creation, message, rq.status, re.title
        FROM requestdb rq
        LEFT JOIN recipedb re ON re.id_recipe = rq.id_recipe
        LEFT JOIN userdb u ON u.id_user = rq.id_from
        WHERE rq.status = 'ISSUED' AND re.id_user = ?";

$rows = db_prepared_select_all($cxn, $sql, "s", array($iduser_req));
if ($rows === false) exitfail("No result");

echo json_encode($rows);
exitsuccess($cxn);
?>
