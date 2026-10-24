<?php
/* Program: getcommunitynews.php
 * Collect recipe stamps from CookBook community with prepared statements
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$iduser_req = $_POST['iduser'] ?? '';
if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");

include("itchekpwd.inc");

$numstart_req = (int)($_POST['start'] ?? 0);
$numcount_req = (int)($_POST['count'] ?? 10);
if ($numstart_req < 0) $numstart_req = 0;
if ($numcount_req < 1 || $numcount_req > 100) $numcount_req = 10;

$comtype_req = $_POST['comtype'] ?? 'RECENT';
if (!in_array($comtype_req, array('RECENT', 'BESTNOTE', 'POPULAR'))) exitfail("Error in comtype");

$comparam_req = $_POST['comparam'] ?? 'WOP';
if (!in_array($comparam_req, array('WP', 'WOP'))) exitfail("Error in comparam");

$comscope_req = $_POST['comscope'] ?? 'ALL';
if (!in_array($comscope_req, array('ALL', 'CLOSE', 'FOLLOWER'))) exitfail("Error in comscope");

$wp = "";
if ($comparam_req === "WP") {
    $wp = ", r.photo ";
}

$sql = "SELECT r.id_recipe, r.title, r.season, r.type, r.difficulty" . $wp . ", u.id_user, u.name, u.family, r.lastupdate_recipe, x.noteavg, x.notecount
        FROM recipedb r
        LEFT JOIN userdb u ON u.id_user = r.id_user
        LEFT JOIN (SELECT id_recipe FROM cookbooks WHERE id_user = ?) AS c ON c.id_recipe = r.id_recipe
        LEFT JOIN (SELECT id_recipe, AVG(note) AS noteavg, COUNT(note) AS notecount, ROUND(AVG(note),2)+(AVG(note)>3.8)*COUNT(note)/(1+COUNT(note)/20)/20 AS notescore
            FROM notesdb GROUP BY id_recipe) AS x ON x.id_recipe = r.id_recipe
        LEFT JOIN (SELECT id_recipe, COUNT(id_user) AS recipecount FROM cookbooks GROUP BY id_recipe) AS y ON r.id_recipe = y.id_recipe ";

switch ($comscope_req) {
    case 'ALL':
        $sql .= "WHERE c.id_recipe IS NULL ";
        break;
    case 'CLOSE':
        $sql .= "LEFT JOIN (SELECT rz.id_user FROM recipedb rz LEFT JOIN cookbooks cz ON cz.id_user = ? AND cz.status = 'Visible' WHERE cz.id_recipe = rz.id_recipe) AS z ON z.id_user = r.id_user WHERE c.id_recipe IS NULL AND z.id_user IS NOT NULL ";
        break;
    case 'FOLLOWER':
        $sql .= "LEFT JOIN (SELECT rz.id_user FROM recipedb rz LEFT JOIN cookbooks cz ON cz.id_user = ? AND cz.status = 'Visible' WHERE cz.id_recipe = rz.id_recipe) AS z ON z.id_user = r.id_user LEFT JOIN (SELECT n.id_user FROM notesdb n LEFT JOIN cookbooks cw ON cw.id_user = ? AND cw.status = 'Visible' WHERE n.id_recipe = cw.id_recipe GROUP BY n.id_user) AS w ON w.id_user = r.id_user WHERE c.id_recipe IS NULL AND (z.id_user IS NOT NULL OR w.id_user IS NOT NULL) ";
        break;
}

switch ($comtype_req) {
    case 'RECENT':
        $sql .= "GROUP BY r.id_recipe ORDER BY r.lastupdate_recipe DESC LIMIT " . $numstart_req . "," . $numcount_req;
        break;
    case 'BESTNOTE':
        $sql .= "GROUP BY r.id_recipe ORDER BY x.notescore DESC LIMIT " . $numstart_req . "," . $numcount_req;
        break;
    case 'POPULAR':
        $sql .= "GROUP BY r.id_recipe ORDER BY y.recipecount DESC, x.notescore DESC LIMIT " . $numstart_req . "," . $numcount_req;
        break;
}

if ($comscope_req === 'ALL') {
    $rows = db_prepared_select_all($cxn, $sql, "s", array($iduser_req));
} elseif ($comscope_req === 'CLOSE') {
    $rows = db_prepared_select_all($cxn, $sql, "ss", array($iduser_req, $iduser_req));
} else { // FOLLOWER
    $rows = db_prepared_select_all($cxn, $sql, "sss", array($iduser_req, $iduser_req, $iduser_req));
}

if ($rows === false) exitfail("No result");

echo json_encode($rows);
exitsuccess($cxn);
?>
