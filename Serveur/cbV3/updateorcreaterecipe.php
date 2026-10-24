<?php
/* Program: updateorcreaterecipe.php
 * Update if recipe exists in recipedb or create new entry
 */

include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection failed");
}

$idrecipe_req = $_POST['idrecipe'] ?? '';
$iduser_req   = $_POST['iduser'] ?? '';

if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");
if (!preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe");

$title_req = trim($_POST['title'] ?? '');
if (!preg_match("/^[\s\S]{1,100}$/u", $title_req)) exitfail("Error in title");

$source_req = trim($_POST['source'] ?? '');
if (!preg_match("/^[\s\S]{0,255}$/u", $source_req)) exitfail("Error in source");

$sourceurl_req = trim($_POST['sourceurl'] ?? '');
if (!empty($sourceurl_req) && !preg_match('/^(http|https|ftp):\/\/([A-Z0-9][A-Z0-9_-]*(?:\.[A-Z0-9][A-Z0-9_-]*)+):?(\d+)?\/?/i', $sourceurl_req)) {
    // If not a standard URL, allow any string up to 255 chars
    if (!preg_match("/^[\s\S]{0,255}$/u", $sourceurl_req)) exitfail("Error in source url");
}

$nbpers_req = (int)($_POST['nbpers'] ?? 0);
if (($nbpers_req < 1) || ($nbpers_req > 12)) exitfail("Error in nb pers");

$pattern = $pat_etape;
$etape01_req = $_POST['etape01'] ?? ''; if (!preg_match($pattern, $etape01_req)) exitfail("Error in step 1");
$etape02_req = $_POST['etape02'] ?? ''; if (!preg_match($pattern, $etape02_req)) exitfail("Error in step 2");
$etape03_req = $_POST['etape03'] ?? ''; if (!preg_match($pattern, $etape03_req)) exitfail("Error in step 3");
$etape04_req = $_POST['etape04'] ?? ''; if (!preg_match($pattern, $etape04_req)) exitfail("Error in step 4");
$etape05_req = $_POST['etape05'] ?? ''; if (!preg_match($pattern, $etape05_req)) exitfail("Error in step 5");
$etape06_req = $_POST['etape06'] ?? ''; if (!preg_match($pattern, $etape06_req)) exitfail("Error in step 6");
$etape07_req = $_POST['etape07'] ?? ''; if (!preg_match($pattern, $etape07_req)) exitfail("Error in step 7");
$etape08_req = $_POST['etape08'] ?? ''; if (!preg_match($pattern, $etape08_req)) exitfail("Error in step 8");
$etape09_req = $_POST['etape09'] ?? ''; if (!preg_match($pattern, $etape09_req)) exitfail("Error in step 9");

$season_req = trim($_POST['season'] ?? '');
if (!in_array($season_req, array('SUMMER', 'WINTER', 'ALLYEAR'))) exitfail("Error in season");

$difficulty_req = trim($_POST['difficulty'] ?? '');
if (!in_array($difficulty_req, array('QUICK', 'EASY', 'ELABORATE', 'SOPHISTICATED', 'UNDEFINED'))) exitfail("Error in difficulty");

$type_req = trim($_POST['type'] ?? '');
if (!in_array($type_req, array('APERITIF', 'STARTER', 'MAIN', 'DESSERT', 'SIDE', 'OTHER'))) exitfail("Error in type");

$pattern = $pat_ing;
$ing01_req = $_POST['ing01'] ?? ''; if (!preg_match($pattern, $ing01_req)) exitfail("Error in ingredient 1");
$ing02_req = $_POST['ing02'] ?? ''; if (!preg_match($pattern, $ing02_req)) exitfail("Error in ingredient 2");
$ing03_req = $_POST['ing03'] ?? ''; if (!preg_match($pattern, $ing03_req)) exitfail("Error in ingredient 3");
$ing04_req = $_POST['ing04'] ?? ''; if (!preg_match($pattern, $ing04_req)) exitfail("Error in ingredient 4");
$ing05_req = $_POST['ing05'] ?? ''; if (!preg_match($pattern, $ing05_req)) exitfail("Error in ingredient 5");
$ing06_req = $_POST['ing06'] ?? ''; if (!preg_match($pattern, $ing06_req)) exitfail("Error in ingredient 6");
$ing07_req = $_POST['ing07'] ?? ''; if (!preg_match($pattern, $ing07_req)) exitfail("Error in ingredient 7");
$ing08_req = $_POST['ing08'] ?? ''; if (!preg_match($pattern, $ing08_req)) exitfail("Error in ingredient 8");
$ing09_req = $_POST['ing09'] ?? ''; if (!preg_match($pattern, $ing09_req)) exitfail("Error in ingredient 9");
$ing10_req = $_POST['ing10'] ?? ''; if (!preg_match($pattern, $ing10_req)) exitfail("Error in ingredient 10");
$ing11_req = $_POST['ing11'] ?? ''; if (!preg_match($pattern, $ing11_req)) exitfail("Error in ingredient 11");
$ing12_req = $_POST['ing12'] ?? ''; if (!preg_match($pattern, $ing12_req)) exitfail("Error in ingredient 12");
$ing13_req = $_POST['ing13'] ?? ''; if (!preg_match($pattern, $ing13_req)) exitfail("Error in ingredient 13");
$ing14_req = $_POST['ing14'] ?? ''; if (!preg_match($pattern, $ing14_req)) exitfail("Error in ingredient 14");
$ing15_req = $_POST['ing15'] ?? ''; if (!preg_match($pattern, $ing15_req)) exitfail("Error in ingredient 15");

$date_req = $_POST['date'] ?? '';
if (!preg_match($pat_date, $date_req)) exitfail("Error in date format");

$message_req = $_POST['message'] ?? "Nouvelle recette dans la famille";
if (!preg_match($pat_etape, $message_req)) exitfail("Error in message");

// Check if recipe exists
$rows = db_prepared_select_all($cxn, "SELECT id_recipe, id_user FROM recipedb WHERE id_recipe = ?", "s", array($idrecipe_req));
$row_count = ($rows) ? count($rows) : 0;

if ($row_count > 0) {
    // Update: iduser is the owner sent by the app, it must be the current owner (no ownership change)
    if ($rows[0]['id_user'] !== $iduser_req) exitfail("User is not recipe owner");
    // The connected user may be a member of another family having the recipe in his cookbook
    $pwd_recipe_scope = $idrecipe_req;
}
// Creation: the password must be the one of $iduser_req (the creator)

include("itchekpwd.inc");

if ($row_count == 0) {
    // Insert new recipe (34 parameters)
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
    if (!$stmt) exitfail("Prepared statement error for insert");

    $types = "ssssssisssssssssssssssssssssssssss";
    mysqli_stmt_bind_param($stmt, $types,
        $idrecipe_req, $iduser_req, $date_req, $title_req, $source_req, $sourceurl_req, $nbpers_req,
        $etape01_req, $etape02_req, $etape03_req, $etape04_req, $etape05_req, $etape06_req, $etape07_req, $etape08_req, $etape09_req,
        $season_req, $difficulty_req, $type_req,
        $ing01_req, $ing02_req, $ing03_req, $ing04_req, $ing05_req, $ing06_req, $ing07_req, $ing08_req, $ing09_req, $ing10_req,
        $ing11_req, $ing12_req, $ing13_req, $ing14_req, $ing15_req
    );
    $res = mysqli_stmt_execute($stmt);
    mysqli_stmt_close($stmt);
    if (!$res) exitfail("MySQL insert fail");
} else {
    // Update existing recipe (34 parameters)
    $sql = "UPDATE `recipedb` SET `id_user` = ?, `lastupdate_recipe` = ?,
            `title` = ?, `source` = ?, `source_url` = ?, `nb_pers` = ?,
            `etape01` = ?, `etape02` = ?, `etape03` = ?, `etape04` = ?, `etape05` = ?, `etape06` = ?, `etape07` = ?, `etape08` = ?, `etape09` = ?,
            `season` = ?, `difficulty` = ?, `type` = ?,
            `ing01` = ?, `ing02` = ?, `ing03` = ?, `ing04` = ?, `ing05` = ?, `ing06` = ?, `ing07` = ?, `ing08` = ?, `ing09` = ?, `ing10` = ?,
            `ing11` = ?, `ing12` = ?, `ing13` = ?, `ing14` = ?, `ing15` = ?
            WHERE id_recipe = ?";
    $stmt = mysqli_prepare($cxn, $sql);
    if (!$stmt) exitfail("Prepared statement error for update");

    $types = "sssssissssssssssssssssssssssssssss";
    mysqli_stmt_bind_param($stmt, $types,
        $iduser_req, $date_req, $title_req, $source_req, $sourceurl_req, $nbpers_req,
        $etape01_req, $etape02_req, $etape03_req, $etape04_req, $etape05_req, $etape06_req, $etape07_req, $etape08_req, $etape09_req,
        $season_req, $difficulty_req, $type_req,
        $ing01_req, $ing02_req, $ing03_req, $ing04_req, $ing05_req, $ing06_req, $ing07_req, $ing08_req, $ing09_req, $ing10_req,
        $ing11_req, $ing12_req, $ing13_req, $ing14_req, $ing15_req, $idrecipe_req
    );
    $res = mysqli_stmt_execute($stmt);
    mysqli_stmt_close($stmt);
    if (!$res) exitfail("MySQL update fail");
}

if ($row_count == 0) {
    // Insert for each family member
    $user_rows = db_prepared_select_all($cxn, "SELECT family FROM userdb WHERE id_user = ?", "s", array($iduser_req));
    $family_req = ($user_rows && count($user_rows) > 0) ? $user_rows[0]['family'] : '';

    $member_rows = db_prepared_select_all($cxn, "SELECT id_user FROM userdb WHERE family = ?", "s", array($family_req));

    if ($member_rows) {
        foreach ($member_rows as $ligne) {
            $m_iduser = $ligne['id_user'];
            $status_req = ($m_iduser == $iduser_req) ? 'Visible' : 'Submitted';

            $stmt2 = mysqli_prepare($cxn, "INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) VALUES (?, ?, ?, ?, ?)");
            if ($stmt2) {
                mysqli_stmt_bind_param($stmt2, "sssss", $m_iduser, $idrecipe_req, $status_req, $message_req, $iduser_req);
                mysqli_stmt_execute($stmt2);
                mysqli_stmt_close($stmt2);
            }
        }
    }
}

echo $found;
exitsuccess($cxn);
?>
