<?php
/* Program: ixupdateorcreaterecipe.php
 * Web portal update/create recipe
 */
include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exiterror("Database connection error");
}

session_start();
if (!isset($_SESSION['auth']) || $_SESSION['auth'] !== "yes") {
    exiterror("Not authorized");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$iduser_req   = $_POST['iduser'] ?? '';

if (!preg_match($pat_uuid, $iduser_req)) exiterror("Error in Id user " . $iduser_req);
if (!preg_match($pat_uuid, $idrecipe_req)) exiterror("Error in Id recipe " . $idrecipe_req);

$pattern = "/[A-Za-zÀ-ÿ #-_!?.,:()&\$£€%*+0-9]{1,80}/u";
$title_req = trim($_POST['title'] ?? '');
if (!preg_match($pattern, $title_req)) exiterror("Error in title " . $title_req);

$pattern = "/[A-Za-zÀ-ÿ #-_!?.,(\$£€)%*&:+0-9]{0,254}/u";
$source_req = trim($_POST['source'] ?? '');
if (!preg_match($pattern, $source_req)) exiterror("Error in source " . $source_req);

$sourceurl_req = trim($_POST['sourceurl'] ?? '');
if (!empty($sourceurl_req) && !preg_match('/^(http|https|ftp):\/\/([A-Z0-9][A-Z0-9_-]*(?:\.[A-Z0-9][A-Z0-9_-]*)+):?(\d+)?\/?/i', $sourceurl_req)) {
    exiterror("Error in source url " . $sourceurl_req);
}

$nbpers_req = (int)($_POST['nbpers'] ?? 0);
if (($nbpers_req < 1) || ($nbpers_req > 12)) exiterror("Error in nb pers " . $nbpers_req);

$pattern = $pat_etape;
$etape01_req = $_POST['etape01'] ?? ''; if (!preg_match($pattern, $etape01_req)) exiterror("Error in step 1");
$etape02_req = $_POST['etape02'] ?? ''; if (!preg_match($pattern, $etape02_req)) exiterror("Error in step 2");
$etape03_req = $_POST['etape03'] ?? ''; if (!preg_match($pattern, $etape03_req)) exiterror("Error in step 3");
$etape04_req = $_POST['etape04'] ?? ''; if (!preg_match($pattern, $etape04_req)) exiterror("Error in step 4");
$etape05_req = $_POST['etape05'] ?? ''; if (!preg_match($pattern, $etape05_req)) exiterror("Error in step 5");
$etape06_req = $_POST['etape06'] ?? ''; if (!preg_match($pattern, $etape06_req)) exiterror("Error in step 6");
$etape07_req = $_POST['etape07'] ?? ''; if (!preg_match($pattern, $etape07_req)) exiterror("Error in step 7");
$etape08_req = $_POST['etape08'] ?? ''; if (!preg_match($pattern, $etape08_req)) exiterror("Error in step 8");
$etape09_req = $_POST['etape09'] ?? ''; if (!preg_match($pattern, $etape09_req)) exiterror("Error in step 9");

$season_req = trim($_POST['season'] ?? '');
if (!in_array($season_req, array('SUMMER', 'WINTER', 'ALLYEAR'))) exiterror("Error in season " . $season_req);

$difficulty_req = trim($_POST['difficulty'] ?? '');
if (!in_array($difficulty_req, array('QUICK', 'EASY', 'ELABORATE', 'SOPHISTICATED', 'UNDEFINED'))) exiterror("Error in difficulty " . $difficulty_req);

$type_req = trim($_POST['type'] ?? '');
if (!in_array($type_req, array('APERITIF', 'STARTER', 'MAIN', 'DESSERT', 'SIDE', 'OTHER'))) exiterror("Error in type " . $type_req);

$pattern = $pat_ing;
$ing01_req = $_POST['ing01'] ?? ''; if (!preg_match($pattern, $ing01_req)) exiterror("Error in ingredient 1");
$ing02_req = $_POST['ing02'] ?? ''; if (!preg_match($pattern, $ing02_req)) exiterror("Error in ingredient 2");
$ing03_req = $_POST['ing03'] ?? ''; if (!preg_match($pattern, $ing03_req)) exiterror("Error in ingredient 3");
$ing04_req = $_POST['ing04'] ?? ''; if (!preg_match($pattern, $ing04_req)) exiterror("Error in ingredient 4");
$ing05_req = $_POST['ing05'] ?? ''; if (!preg_match($pattern, $ing05_req)) exiterror("Error in ingredient 5");
$ing06_req = $_POST['ing06'] ?? ''; if (!preg_match($pattern, $ing06_req)) exiterror("Error in ingredient 6");
$ing07_req = $_POST['ing07'] ?? ''; if (!preg_match($pattern, $ing07_req)) exiterror("Error in ingredient 7");
$ing08_req = $_POST['ing08'] ?? ''; if (!preg_match($pattern, $ing08_req)) exiterror("Error in ingredient 8");
$ing09_req = $_POST['ing09'] ?? ''; if (!preg_match($pattern, $ing09_req)) exiterror("Error in ingredient 9");
$ing10_req = $_POST['ing10'] ?? ''; if (!preg_match($pattern, $ing10_req)) exiterror("Error in ingredient 10");
$ing11_req = $_POST['ing11'] ?? ''; if (!preg_match($pattern, $ing11_req)) exiterror("Error in ingredient 11");
$ing12_req = $_POST['ing12'] ?? ''; if (!preg_match($pattern, $ing12_req)) exiterror("Error in ingredient 12");
$ing13_req = $_POST['ing13'] ?? ''; if (!preg_match($pattern, $ing13_req)) exiterror("Error in ingredient 13");
$ing14_req = $_POST['ing14'] ?? ''; if (!preg_match($pattern, $ing14_req)) exiterror("Error in ingredient 14");
$ing15_req = $_POST['ing15'] ?? ''; if (!preg_match($pattern, $ing15_req)) exiterror("Error in ingredient 15");

$date_req = $_POST['date'] ?? '';
if (!preg_match($pat_date, $date_req)) exiterror("Error in date format " . $date_req);

$rows = db_prepared_select_all($cxn, "SELECT id_recipe, id_user FROM recipedb WHERE id_recipe = ?", "s", array($idrecipe_req));
$row_count = ($rows) ? count($rows) : 0;

// The logged-in member may only edit a recipe of his cookbook, without changing its owner,
// and may only create a recipe for himself
$id_user_session = $_SESSION['id_user'] ?? '';
if ($row_count > 0) {
    if (!checkrecipeincb($cxn, $idrecipe_req, $id_user_session)) exiterror("Not authorized");
    if ($rows[0]['id_user'] !== $iduser_req) exiterror("Not authorized");
} elseif ($iduser_req !== $id_user_session) {
    exiterror("Not authorized");
}

if ($row_count == 0) {
    $sql = "INSERT INTO `recipedb`(`id_recipe`,`id_user`,`lastupdate_recipe`, `lastupdate_photo`, `title`, `source`, `source_url`, `nb_pers`,
            `etape01`, `etape02`, `etape03`, `etape04`, `etape05`, `etape06`, `etape07`, `etape08`, `etape09`,
            `season`, `difficulty`,`type`,
            `ing01`, `ing02`, `ing03`, `ing04`, `ing05`, `ing06`, `ing07`, `ing08`, `ing09`, `ing10`,
            `ing11`, `ing12`, `ing13`, `ing14`, `ing15`)
            VALUES (?, ?, ?, '1900-01-01 00:00:00', ?, ?, ?, ?,
            ?, ?, ?, ?, ?, ?, ?, ?, ?,
            ?, ?, ?,
            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?)";
    $stmt = mysqli_prepare($cxn, $sql);
    mysqli_stmt_bind_param($stmt, "ssssssisssssssssssssssssssssssssss",
        $idrecipe_req, $iduser_req, $date_req, $title_req, $source_req, $sourceurl_req, $nbpers_req,
        $etape01_req, $etape02_req, $etape03_req, $etape04_req, $etape05_req, $etape06_req, $etape07_req, $etape08_req, $etape09_req,
        $season_req, $difficulty_req, $type_req,
        $ing01_req, $ing02_req, $ing03_req, $ing04_req, $ing05_req, $ing06_req, $ing07_req, $ing08_req, $ing09_req, $ing10_req,
        $ing11_req, $ing12_req, $ing13_req, $ing14_req, $ing15_req
    );
    $res = mysqli_stmt_execute($stmt);
    mysqli_stmt_close($stmt);
    if (!$res) exiterror("MySQL insert fail");
} else {
    $sql = "UPDATE `recipedb` SET `id_user` = ?, `lastupdate_recipe` = ?,
            `title` = ?, `source` = ?, `source_url` = ?, `nb_pers` = ?,
            `etape01` = ?, `etape02` = ?, `etape03` = ?, `etape04` = ?, `etape05` = ?, `etape06` = ?, `etape07` = ?, `etape08` = ?, `etape09` = ?,
            `season` = ?, `difficulty` = ?, `type` = ?,
            `ing01` = ?, `ing02` = ?, `ing03` = ?, `ing04` = ?, `ing05` = ?, `ing06` = ?, `ing07` = ?, `ing08` = ?, `ing09` = ?, `ing10` = ?,
            `ing11` = ?, `ing12` = ?, `ing13` = ?, `ing14` = ?, `ing15` = ?
            WHERE id_recipe = ?";
    $stmt = mysqli_prepare($cxn, $sql);
    mysqli_stmt_bind_param($stmt, "sssssissssssssssssssssssssssssssss",
        $iduser_req, $date_req, $title_req, $source_req, $sourceurl_req, $nbpers_req,
        $etape01_req, $etape02_req, $etape03_req, $etape04_req, $etape05_req, $etape06_req, $etape07_req, $etape08_req, $etape09_req,
        $season_req, $difficulty_req, $type_req,
        $ing01_req, $ing02_req, $ing03_req, $ing04_req, $ing05_req, $ing06_req, $ing07_req, $ing08_req, $ing09_req, $ing10_req,
        $ing11_req, $ing12_req, $ing13_req, $ing14_req, $ing15_req, $idrecipe_req
    );
    $res = mysqli_stmt_execute($stmt);
    mysqli_stmt_close($stmt);
    if (!$res) exiterror("MySQL update fail");
}

mysqli_close($cxn);
header("Location: ix.php");
exit;

function exiterror($p) {
    $pk = $_POST['pknum'] ?? 0;
    header("Location: ix.php?recipenum=" . $pk . "&edit=yes&logmessage=" . urlencode($p));
    exit;
}
?>
