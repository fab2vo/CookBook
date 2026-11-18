<?php
/* Program: updateagenda.php
 * Changes an agenda entry: day, meal, recipe or free text (the scope never changes)
 * POST: iduser, pwd, identry, date (YYYY-MM-DD), meal (LUNCH|DINNER), idrecipe (UUID or empty), label
 * Rights: any member of the family for a FAMILY entry, only its author for a PERSO entry
 * Answer: "1", or "FAIL: Slot full" when the target meal already holds 2 other entries
 */

include("ikonexct007.inc");
include("agenda.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$iduser_req  = $_POST['iduser'] ?? '';
$identry_req = $_POST['identry'] ?? '';

if (!preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user");
if (!preg_match($pat_uuid, $identry_req)) exitfail("Error in Id entry");
list($day, $meal, $idrecipe, $label) = agenda_read_meal_fields();

include("itchekpwd.inc");

$family = agenda_user_family($cxn, $iduser_req);
$entry  = agenda_load_owned_entry($cxn, $identry_req, $iduser_req, $family);
$label  = agenda_label($cxn, $idrecipe, $label, $iduser_req, $family);
// Count the entries of the target meal of the entry's own agenda, the entry itself excluded
agenda_check_slot($cxn, $entry['scope'], $entry['family'], $entry['id_user'], $day, $meal, $identry_req);

$idrecipe_db = ($idrecipe === '') ? null : $idrecipe;
agenda_write($cxn,
    "UPDATE agendadb SET date_meal = ?, meal = ?, id_recipe = ?, label = ? WHERE id_entry = ?",
    "sssss",
    array($day, $meal, $idrecipe_db, $label, $identry_req));

echo $found;
exitsuccess($cxn);
?>
