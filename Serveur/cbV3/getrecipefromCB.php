<?php
/* Program: getrecipefromCB.php
 * Download a recipe w/wo photo
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$withphototest = trim($_POST['withphoto'] ?? "0");

if (!preg_match($pat_uuid, $idrecipe_req)) {
    exitfail("Error in Id recipe");
}
if (($withphototest !== "0") && ($withphototest !== "1")) {
    exitfail("Error of withphoto");
}

include("itchekuser.inc");

$wp = "";
if ($withphototest == 1) {
    $wp = "photo, ";
}

$sql = "SELECT c.pknum, r.id_recipe, c.id_user, r.lastupdate_recipe, r.lastupdate_photo,
            r.title, r.source, r.source_url, r.nb_pers,
            etape01, etape02, etape03, etape04, etape05, etape06, etape07, etape08, etape09,
            season, difficulty, type,
            ing01, ing02, ing03, ing04, ing05, ing06, ing07, ing08, ing09, ing10,
            ing11, ing12, ing13, ing14, ing15, " . $wp . "
            u2.family AS owner_family, u2.name AS owner_name,
            c.message, c.status, c.id_from,
            r.id_user AS id_owner, u3.family AS from_family, u3.name AS from_name
            FROM recipedb r
            INNER JOIN userdb u2 ON r.id_user = u2.id_user
            LEFT JOIN cookbooks c ON c.id_recipe = r.id_recipe AND c.id_user = ?
            LEFT JOIN userdb u3 ON c.id_from = u3.id_user
        WHERE r.id_recipe = ?";

$rows = db_prepared_select_all($cxn, $sql, "ss", array($iduser_req, $idrecipe_req));
if ($rows === false) exitfail("No result");

echo json_encode($rows);
exitsuccess($cxn);
?>
