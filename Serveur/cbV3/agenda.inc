<?php
/* agenda.inc
 * Shared helpers for the meal agenda endpoints (agendadb table, see SPEC_AGENDA.md)
 * An entry belongs either to a family (scope FAMILY: every member may read, change, delete it)
 * or to one user (scope PERSO: only its author). The family is always read from userdb,
 * never taken from the request.
 */
if (!defined('IN_CB')) {
    define('IN_CB', true);
}

$pat_day   = "/^(\d{4})-(\d{2})-(\d{2})$/";
$pat_scope = "/^(FAMILY|PERSO)$/";
$pat_meal  = "/^(LUNCH|DINNER)$/";
// Free text: no control characters and no < > (the label is also shown by the web portal)
$pat_label = "/^[^<>\\x00-\\x1F\\x7F]{1,255}$/u";

$agenda_max_per_meal = 2;
$agenda_max_days     = 92;

/** True when $d is a real calendar day written YYYY-MM-DD */
function agenda_valid_day($d) {
    global $pat_day;
    if (!preg_match($pat_day, $d, $m)) return false;
    return checkdate((int)$m[2], (int)$m[3], (int)$m[1]);
}

/** Family of the authenticated user (fails if unknown) */
function agenda_user_family($cxn, $iduser) {
    $rows = db_prepared_select_all($cxn, "SELECT family FROM userdb WHERE id_user = ?", "s", array($iduser));
    if (!$rows || count($rows) !== 1) exitfail("Unknown user");
    return $rows[0]['family'];
}

/** Loads an entry and checks the user may change it (same family for FAMILY, author for PERSO) */
function agenda_load_owned_entry($cxn, $identry, $iduser, $family) {
    $rows = db_prepared_select_all($cxn,
        "SELECT id_entry, family, id_user, scope, date_meal, meal FROM agendadb WHERE id_entry = ?",
        "s", array($identry));
    if (!$rows || count($rows) !== 1) exitfail("Unknown entry");
    $e = $rows[0];
    if ($e['scope'] === 'FAMILY' && $e['family'] !== $family) exitfail("Entry not in user family");
    if ($e['scope'] === 'PERSO' && $e['id_user'] !== $iduser) exitfail("Entry not owned by user");
    return $e;
}

/** Fails when the meal (day + lunch/dinner) of this agenda already holds the maximum of entries.
 *  $exclude_entry: entry being moved, not counted ('' when adding) */
function agenda_check_slot($cxn, $scope, $family, $iduser, $day, $meal, $exclude_entry) {
    global $agenda_max_per_meal;
    if ($scope === 'FAMILY') {
        $rows = db_prepared_select_all($cxn,
            "SELECT COUNT(*) AS nb FROM agendadb WHERE scope = 'FAMILY' AND family = ? AND date_meal = ? AND meal = ? AND id_entry <> ?",
            "ssss", array($family, $day, $meal, $exclude_entry));
    } else {
        $rows = db_prepared_select_all($cxn,
            "SELECT COUNT(*) AS nb FROM agendadb WHERE scope = 'PERSO' AND id_user = ? AND date_meal = ? AND meal = ? AND id_entry <> ?",
            "ssss", array($iduser, $day, $meal, $exclude_entry));
    }
    if ($rows === false) exitfail("Slot check error");
    if ((int)$rows[0]['nb'] >= $agenda_max_per_meal) exitfail("Slot full");
}

/** True when the recipe is planned in the user's family agenda or in his personal agenda */
function agenda_recipe_visible($cxn, $idrecipe, $iduser, $family) {
    $rows = db_prepared_select_all($cxn,
        "SELECT pknum FROM agendadb WHERE id_recipe = ?
           AND ((scope = 'FAMILY' AND family = ?) OR (scope = 'PERSO' AND id_user = ?)) LIMIT 1",
        "sss", array($idrecipe, $family, $iduser));
    return ($rows && count($rows) > 0);
}

/** Label stored with the entry: the recipe title when a recipe is given, else the free text.
 *  A recipe may be planned only if it is in the user's cookbook or already planned in an agenda
 *  he can see: otherwise any recipe id (shown by the community screen) could be planned, then
 *  read in full by getagendarecipe.php without the owner's agreement. */
function agenda_label($cxn, $idrecipe, $label, $iduser, $family) {
    global $pat_label;
    if ($idrecipe !== '') {
        $rows = db_prepared_select_all($cxn, "SELECT title FROM recipedb WHERE id_recipe = ?", "s", array($idrecipe));
        if (!$rows || count($rows) !== 1) exitfail("Unknown recipe");
        if (!checkrecipeincb($cxn, $idrecipe, $iduser) && !agenda_recipe_visible($cxn, $idrecipe, $iduser, $family)) {
            exitfail("Recipe not accessible");
        }
        return $rows[0]['title'];   // recipedb.title is varchar(255), like agendadb.label
    }
    if (!preg_match($pat_label, $label)) exitfail("Error in label");
    return $label;
}

/** Reads and checks the fields shared by add and update; returns array(day, meal, idrecipe, label) */
function agenda_read_meal_fields() {
    global $pat_meal, $pat_uuid;
    $day      = $_POST['date'] ?? '';
    $meal     = $_POST['meal'] ?? '';
    $idrecipe = trim($_POST['idrecipe'] ?? '');
    $label    = trim($_POST['label'] ?? '');
    if (!agenda_valid_day($day)) exitfail("Error in date");
    if (!preg_match($pat_meal, $meal)) exitfail("Error in meal");
    if ($idrecipe !== '' && !preg_match($pat_uuid, $idrecipe)) exitfail("Error in Id recipe");
    return array($day, $meal, $idrecipe, $label);
}

/** Executes a write prepared statement, fails on error */
function agenda_write($cxn, $sql, $types, $params) {
    $stmt = mysqli_prepare($cxn, $sql);
    if (!$stmt) exitfail("Prepared statement error");
    mysqli_stmt_bind_param($stmt, $types, ...$params);
    $res = mysqli_stmt_execute($stmt);
    mysqli_stmt_close($stmt);
    if (!$res) exitfail("Write error");
}
?>
