<?php
/* Program: updatestatusrequest.php
 * Update status of request pknum
 * Legacy endpoint (not called by the current app, see acceptorrefuserequest.php):
 * authentication is mandatory and only the recipe owner may change the status.
 */

include("ikonexct007.inc");
error_log("CookBook: legacy endpoint called: " . basename(__FILE__));

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    echo $notfound;
    exit;
}

$pknum_req  = $_POST['pknum'] ?? '';
$status_req = $_POST['status'] ?? '';
$iduser_req = $_POST['iduser'] ?? '';

if (!preg_match($pat_num, $pknum_req) || !preg_match($pat_uuid, $iduser_req)) {
    echo $notfound;
    exit;
}
if (!in_array($status_req, array('ISSUED', 'ACCEPTED', 'DENIED'))) {
    echo $notfound;
    exit;
}

include("itchekpwd.inc");

$sql = "SELECT re.id_user FROM requestdb rq
        INNER JOIN recipedb re ON re.id_recipe = rq.id_recipe
        WHERE rq.pknum = ?";
$rows = db_prepared_select_all($cxn, $sql, "i", array($pknum_req));
if (!$rows || count($rows) !== 1 || $rows[0]['id_user'] !== $iduser_req) {
    exitfail("User is not recipe owner");
}

$stmt = mysqli_prepare($cxn, "UPDATE `requestdb` SET `status` = ? WHERE pknum = ?");
if (!$stmt) {
    echo $notfound;
    exit;
}

mysqli_stmt_bind_param($stmt, "si", $status_req, $pknum_req);
$res = mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

if (!$res) {
    echo $notfound;
    exit;
}

echo $found;
mysqli_close($cxn);
?>
