<?php
/* Program: addagenda.php
 * Adds an entry (recipe or free text) to a meal of the family or personal agenda
 * POST: iduser, pwd, identry (UUID made by the app), scope (FAMILY|PERSO), date (YYYY-MM-DD),
 *       meal (LUNCH|DINNER), idrecipe (UUID or empty), label (free text, ignored when idrecipe is given)
 * Answer: "1", or "FAIL: Slot full" when the meal already holds 2 entries
 */

include("ikonexct007.inc");
include("agenda.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$iduser_req  = $_POST['iduser'] ?? '';
$identry_req = $_POST['identry'] ?? '';
$scope_req   = $_POST['scope'] ?? '';

if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");
if (!preg_match($pat_uuid, $identry_req)) exitfail("Error in Id entry");
if (!preg_match($pat_scope, $scope_req)) exitfail("Error in scope");
list($day, $meal, $idrecipe, $label) = agenda_read_meal_fields();

include("itchekpwd.inc");

// The app may send the same entry again (lost answer): already stored, answer OK
$rows = db_prepared_select_all($cxn, "SELECT pknum FROM agendadb WHERE id_entry = ?", "s", array($identry_req));
if ($rows && count($rows) > 0) {
    exitok("Entry already exists");
}

$family = agenda_user_family($cxn, $iduser_req);
$label  = agenda_label($cxn, $idrecipe, $label, $iduser_req, $family);
agenda_check_slot($cxn, $scope_req, $family, $iduser_req, $day, $meal, '');

$idrecipe_db = ($idrecipe === '') ? null : $idrecipe;
agenda_write($cxn,
    "INSERT INTO agendadb (id_entry, family, id_user, scope, date_meal, meal, id_recipe, label)
     VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
    "ssssssss",
    array($identry_req, $family, $iduser_req, $scope_req, $day, $meal, $idrecipe_db, $label));

echo $found;
exitsuccess($cxn);
?>
