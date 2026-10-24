<?php
/* Program: getrecipestamps.php
 * Extract recipe stamps
 */

include("ikonexct007.inc");

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    echo $notfound;
    exit;
}

$user_req = $_REQUEST['id_user'] ?? $_REQUEST['iduser'] ?? '';
if (!preg_match($pat_uuid, $user_req)) {
    echo $notfound;
    exit;
}

$iduser_req = $user_req;
include("itchekpwd.inc");

$sql = "SELECT cookbooks.id_recipe, lastupdate_recipe, lastupdate_photo,
        recipedb.id_user AS id_owner, status, message, id_from
        FROM cookbooks
        LEFT JOIN recipedb ON cookbooks.id_recipe = recipedb.id_recipe
        WHERE cookbooks.id_user = ?";

$rows = db_prepared_select_all($cxn, $sql, "s", array($user_req));
if ($rows === false) {
    echo $notfound;
    exit;
}

echo json_encode($rows);
mysqli_close($cxn);
?>
