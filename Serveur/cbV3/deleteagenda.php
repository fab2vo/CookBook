<?php
/* Program: deleteagenda.php
 * Deletes an agenda entry
 * POST: iduser, pwd, identry
 * Rights: any member of the family for a FAMILY entry, only its author for a PERSO entry
 * Answer: "1"
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

include("itchekpwd.inc");

$family = agenda_user_family($cxn, $iduser_req);
agenda_load_owned_entry($cxn, $identry_req, $iduser_req, $family);

agenda_write($cxn, "DELETE FROM agendadb WHERE id_entry = ?", "s", array($identry_req));

echo $found;
exitsuccess($cxn);
?>
