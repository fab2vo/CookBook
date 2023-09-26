<?php
/* itchekuser.inc
 * check that id_user has the recipe in its cookbook 
 * need  $cxn $idrecipe_req (id_user and pwd)       
 */ 
 
 if (isset($_POST['iduser'])) {
	$iduser_req=$_POST['iduser'];
	$pwd_req=trim(addslashes($_POST['pwd']));
	if (! preg_match($pat_pwd, $pwd_req)) exitfail("Error in pwd ".$pwd_req);
	if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user ".$iduser_req);
	if (!checkrecipeincb($cxn, $idrecipe_req,$iduser_req)) exitfail("User ".$iduser_req."has not recipe ".$idrecipe_req." in its cookbook");
	if (!checkpwd($cxn, $iduser_req, $pwd_req)) exitfail("wrong pwd");
	debugtrace("Pwd and user given and checked");
 } else  {
	// nothing - do
	debugtrace("Pwd and user not given");
 }
?>

