<?php
/* Program: getcookbook.php
 * Download full recipes for user's cookbook
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection failed");
}

$iduser_req = $_POST['iduser'] ?? '';
if (!preg_match($pat_uuid, $iduser_req)) {
    exitfail("Error in Id user");
}

include("itchekpwd.inc");

$sql = "SELECT r.*, u2.id_user AS id_owner,
        u2.family AS owner_family, u2.name AS owner_name,
        message, status, id_from, u3.family AS from_family, u3.name AS from_name
        FROM cookbooks c
        INNER JOIN userdb u1 ON c.id_user = u1.id_user
        INNER JOIN recipedb r ON c.id_recipe = r.id_recipe
        INNER JOIN userdb u2 ON r.id_user = u2.id_user
        LEFT JOIN userdb u3 ON c.id_from = u3.id_user
        WHERE c.id_user = ?";

$rows = db_prepared_select_all($cxn, $sql, "s", array($iduser_req));
if ($rows === false) {
    exitfail("No result");
}

echo json_encode($rows);
debugtrace("Number of recipes : " . count($rows));
exitsuccess($cxn);
?>
