<?php
/* itchekpwd.inc
 * for main identifiers 
 * need   $iduser_req       
 */ 
 if (isset($_POST['pwd'])) {
	 $pwd_req=trim(addslashes($_POST['pwd']));
	 if (! preg_match($pat_pwd, $pwd_req)) exitfail("Error in pwd ".$pwd_req);
	 debugtrace("Pwd given and checked");
 } else  {
	///find pwd - temporary ! final : exitfail("No password !");
	debugtrace("No pwd given, find pwd");
	$sql="SELECT name, family, last_sync, pass FROM userdb WHERE id_user='$iduser_req'";
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) exitfail("No result");
	$ligne=mysqli_fetch_assoc($result);
	$pwd_req=$ligne['pass'];
 }
 if (!checkpwd($cxn, $iduser_req, $pwd_req)) exitfail("wrong pwd id=".$iduser_req." and pass=".$pwd_req);
 // ajouter plus tard nombre max echec password
?>

