<?php
/* Program: getagenda.php
 * Entries of the family agenda or of the personal agenda between two days (included)
 * POST: iduser, pwd, scope (FAMILY|PERSO), date_from, date_to (YYYY-MM-DD, at most 92 days)
 * Answer: JSON list (possibly empty) of entries
 */

include("ikonexct007.inc");
include("agenda.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$iduser_req = $_POST['iduser'] ?? '';
$scope_req  = $_POST['scope'] ?? '';
$from_req   = $_POST['date_from'] ?? '';
$to_req     = $_POST['date_to'] ?? '';

if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");
if (!preg_match($pat_scope, $scope_req)) exitfail("Error in scope");
if (!agenda_valid_day($from_req) || !agenda_valid_day($to_req)) exitfail("Error in date");

$span = (strtotime($to_req) - strtotime($from_req)) / 86400;
if ($span < 0 || $span > $agenda_max_days) exitfail("Error in date range");

include("itchekpwd.inc");

$select = "SELECT a.id_entry, a.scope, a.id_user, u.name AS author_name, a.date_meal, a.meal,
                  a.id_recipe, a.label, a.date_update
           FROM agendadb a
           LEFT JOIN userdb u ON u.id_user = a.id_user ";

if ($scope_req === 'FAMILY') {
    $family = agenda_user_family($cxn, $iduser_req);
    $rows = db_prepared_select_all($cxn,
        $select . "WHERE a.scope = 'FAMILY' AND a.family = ? AND a.date_meal BETWEEN ? AND ?
                   ORDER BY a.date_meal, a.meal, a.pknum",
        "sss", array($family, $from_req, $to_req));
} else {
    $rows = db_prepared_select_all($cxn,
        $select . "WHERE a.scope = 'PERSO' AND a.id_user = ? AND a.date_meal BETWEEN ? AND ?
                   ORDER BY a.date_meal, a.meal, a.pknum",
        "sss", array($iduser_req, $from_req, $to_req));
}

if ($rows === false) exitfail("Read error");

echo json_encode($rows);
exitsuccess($cxn);
?>
