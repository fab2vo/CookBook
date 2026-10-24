<?php
/* Program: checkrequests.php
 * Check if requests exist for user
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$iduser_req = $_POST['iduser'] ?? '';
if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");

include("itchekpwd.inc");

$sql = "SELECT rq.pknum
        FROM requestdb rq
        LEFT JOIN recipedb re ON re.id_recipe = rq.id_recipe
        WHERE rq.status = 'ISSUED' AND re.id_user = ?";

$rows = db_prepared_select_all($cxn, $sql, "s", array($iduser_req));
if ($rows && count($rows) > 0) {
    exitok("Requetes trouvees");
}

echo $notfound;
exitsuccess($cxn);
?>
