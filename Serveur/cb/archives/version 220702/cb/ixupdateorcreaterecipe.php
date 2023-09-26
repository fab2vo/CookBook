<?php
/* Program: ixupdateorcreaterecipe.php
 * Update if the recipe exists in recipedB
 * Or create new entry into recipedb and in insert it in the familyCB
 * in submitted status for the other
 * input : all fields of recipe but photo and date
 * output : false or true
 */
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 session_start();
 if ($_SESSION['auth']!="yes") exiterror("Not authorized");
 /// check inputs
 $idrecipe_req=$_POST['idrecipe'];
 $iduser_req=$_POST['iduser'];
 if (! preg_match($pat_uuid, $iduser_req)) exiterror("Error in Id user ".$iduser_req);
 if (! preg_match($pat_uuid, $idrecipe_req)) exiterror("Error in Id recipe ".$idrecipe_req);
 $pattern="/[A-Za-zÀ-ÿ #-_!?.,:()&\$£€%*+0-9]{1,80}/"; 
 $title_req=addslashes($_POST['title']);
 if (! preg_match($pattern, $title_req)) exiterror("Error in title ".$title_req);
 $pattern="/[A-Za-zÀ-ÿ #-_!?.,(\$£€)%*&:+0-9]{0,254}/";  
 $source_req=addslashes($_POST['source']);
 if (! preg_match($pattern, $source_req)) exiterror("Error in source ".$source_req);
 $sourceurl_req=trim($_POST['sourceurl']);
 if (! preg_match('/^(http|https|ftp):\/\/([A-Z0-9][A-Z0-9_-]*(?:.[A-Z0-9][A-Z0-9_-]*)+):?(d+)?\/?/i', $sourceurl_req)) exiterror("Error in source url ".$sourceurl_req);
 $nbpers_req=$_POST['nbpers'];
 if (($nbpers_req<1)||($nbpers_req>12)) exiterror("Error in nb pers ".$nbpers_req);
 $pattern=$pat_etape; 
 $etape01_req=addslashes($_POST['etape01']); 
 if (! preg_match($pattern, $etape01_req)) exiterror("Error in step ".$etape01_req);
 $etape02_req=addslashes($_POST['etape02']);
 if (! preg_match($pattern, $etape02_req)) exiterror("Error in step ".$etape02_req); 
 $etape03_req=addslashes($_POST['etape03']); 
  if (! preg_match($pattern, $etape03_req)) exiterror("Error in step ".$etape03_req);
 $etape04_req=addslashes($_POST['etape04']);
 if (! preg_match($pattern, $etape04_req)) exiterror("Error in step ".$etape04_req); 
 $etape05_req=addslashes($_POST['etape05']); 
  if (! preg_match($pattern, $etape05_req)) exiterror("Error in step ".$etape05_req);
 $etape06_req=addslashes($_POST['etape06']); 
 if (! preg_match($pattern, $etape06_req)) exiterror("Error in step ".$etape06_req); 
 $etape07_req=addslashes($_POST['etape07']); 
  if (! preg_match($pattern, $etape07_req)) exiterror("Error in step ".$etape07_req);
 $etape08_req=addslashes($_POST['etape08']); 
  if (! preg_match($pattern, $etape08_req)) exiterror("Error in step ".$etape08_req);
 $etape09_req=addslashes($_POST['etape09']);  
 if (! preg_match($pattern, $etape09_req)) exiterror("Error in step ".$etape09_req); 
 $season_req=trim($_POST['season']);
 if (!in_array($season_req, array('SUMMER','WINTER','ALLYEAR'))) exiterror("Error in season ".$season_req); 
 $difficulty_req=trim($_POST['difficulty']);
 if (!in_array( $difficulty_req, array('QUICK','EASY','ELABORATE','SOPHISTICATED','UNDEFINED'))) exiterror("Error in difficulty ". $difficulty_req); 
 $type_req=trim($_POST['type']);
 if (!in_array( $type_req, array('APERITIF','STARTER','MAIN','DESSERT','SIDE','OTHER'))) exiterror("Error in type ". $type_req); 
 $pattern=$pat_ing;
 $ing01_req=addslashes($_POST['ing01']);
 if (! preg_match($pattern, $ing01_req)) exiterror("Error in ingredient ".$ing01_req);
 $ing02_req=addslashes($_POST['ing02']);
 if (! preg_match($pattern, $ing02_req)) exiterror("Error in ingredient ".$ing02_req);
 $ing03_req=addslashes($_POST['ing03']);
 if (! preg_match($pattern, $ing03_req)) exiterror("Error in ingredient ".$ing03_req);
 $ing04_req=addslashes($_POST['ing04']);
 if (! preg_match($pattern, $ing04_req)) exiterror("Error in ingredient ".$ing04_req);
 $ing05_req=addslashes($_POST['ing05']);
 if (! preg_match($pattern, $ing05_req)) exiterror("Error in ingredient ".$ing05_req);
 $ing06_req=addslashes($_POST['ing06']);
 if (! preg_match($pattern, $ing06_req)) exiterror("Error in ingredient ".$ing06_req);
 $ing07_req=addslashes($_POST['ing07']);
 if (! preg_match($pattern, $ing07_req)) exiterror("Error in ingredient ".$ing07_req);
 $ing08_req=addslashes($_POST['ing08']);
 if (! preg_match($pattern, $ing08_req)) exiterror("Error in ingredient ".$ing08_req);
 $ing09_req=addslashes($_POST['ing09']);
 if (! preg_match($pattern, $ing09_req)) exiterror("Error in ingredient ".$ing09_req);
 $ing10_req=addslashes($_POST['ing10']);
 if (! preg_match($pattern, $ing10_req)) exiterror("Error in ingredient ".$ing10_req);
 $ing11_req=addslashes($_POST['ing11']);
 if (! preg_match($pattern, $ing11_req)) exiterror("Error in ingredient ".$ing11_req);
 $ing12_req=addslashes($_POST['ing12']);
 if (! preg_match($pattern, $ing12_req)) exiterror("Error in ingredient ".$ing12_req);
 $ing13_req=addslashes($_POST['ing13']);
 if (! preg_match($pattern, $ing13_req)) exiterror("Error in ingredient ".$ing13_req);
 $ing14_req=addslashes($_POST['ing14']);
 if (! preg_match($pattern, $ing14_req)) exiterror("Error in ingredient ".$ing14_req);
 $ing15_req=addslashes($_POST['ing15']);
 if (! preg_match($pattern, $ing15_req)) exiterror("Error in ingredient ".$ing15_req);
 $date_req=$_POST['date'];
 if (! preg_match($pat_date, $date_req)) exiterror("Error in date format ".$date_req);
 
 /* Trouver la recette */
 $sql="SELECT * FROM `recipedb` WHERE id_recipe='$idrecipe_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) {debugtrace("Error cxn");exiterror("Error cxn"); } 
 $row_count=$result->num_rows;

if ($row_count==0) {
	/* Si pas trouvée, INSERT une nouvelle entree dans recipedB */
 $sql=" INSERT INTO `recipedb`(`id_recipe`,`id_user`,`lastupdate_recipe`, `lastupdate_photo`, `title`, `source`, `source_url`, `nb_pers`, 
								`etape01`, `etape02`, `etape03`, `etape04`, `etape05`, `etape06`, `etape07`, `etape08`, `etape09`,
								`season`, `difficulty`,`type`,
								`ing01`, `ing02`, `ing03`, `ing04`, `ing05`, `ing06`, `ing07`, `ing08`, `ing09`, `ing10`,
								`ing11`, `ing12`, `ing13`, `ing14`, `ing15`)
		VALUES ( '$idrecipe_req','$iduser_req','$date_req','1900-01-01 00:00:00','$title_req', '$source_req', '$sourceurl_req','$nbpers_req',
			'$etape01_req','$etape02_req','$etape03_req','$etape04_req','$etape05_req', 
			'$etape06_req','$etape07_req','$etape08_req','$etape09_req',
			'$season_req','$difficulty_req','$type_req',
			'$ing01_req','$ing02_req','$ing03_req','$ing04_req','$ing05_req',
			'$ing06_req','$ing07_req','$ing08_req','$ing09_req','$ing10_req',
			'$ing11_req','$ing12_req','$ing13_req','$ing14_req','$ing15_req') ";
 } else {
	 /* Si trouvée, UPDATE la ligne dans recipedB */
	 $sql="UPDATE `recipedb` SET `id_user`='$iduser_req',`lastupdate_recipe`='$date_req',
		`title`='$title_req',`source`='$source_req',`source_url`='$sourceurl_req',`nb_pers`='$nbpers_req',
		`etape01`='$etape01_req',`etape02`='$etape02_req',`etape03`='$etape03_req',`etape04`='$etape04_req',
		`etape05`='$etape05_req',`etape06`='$etape06_req',`etape07`='$etape07_req',`etape08`='$etape08_req',`etape09`='$etape09_req',
		`season`='$season_req',`difficulty`='$difficulty_req',`type`='$type_req',
		`ing01`='$ing01_req',`ing02`='$ing02_req',`ing03`='$ing03_req',`ing04`='$ing04_req',`ing05`='$ing05_req',
		`ing06`='$ing06_req',`ing07`='$ing07_req',`ing08`='$ing08_req',`ing09`='$ing09_req',`ing10`='$ing10_req',
		`ing11`='$ing11_req',`ing12`='$ing12_req',`ing13`='$ing13_req',`ing14`='$ing14_req',`ing15`='$ing15_req'
		WHERE id_recipe='$idrecipe_req'";
 }
 
 debugtrace($sql);
 
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) 
 {	
	debugtrace("Error cxn");
 	exiterror("Error cxn");
 } 

if ($row_count==0) {
	/* Si nouvelle recette, INSERT une nouvelle entrée pour chaque membre de la famille */
	/* --- find family */
	$sql="SELECT `family` FROM `userdb` WHERE id_user='$iduser_req'";
	debugtrace($sql);
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) { debugtrace("error cxn "); exiterror("error cxn");}
	$ligne=mysqli_fetch_assoc($result);
	$family_req=$ligne['family'];
	/* --- fetch family members */
	$sql="SELECT `id_user` FROM `userdb` WHERE family='$family_req'";
	debugtrace($sql);
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) { debugtrace("error cxn "); exiterror("error cxn");}
	$row_member_count=$result->num_rows;
	if (!$result) { debugtrace("error cxn "); exiterror("error cxn");} 
	while ($ligne=mysqli_fetch_assoc($result)) {
	/* --- insert for each member */
		extract($ligne);
		if ($id_user==$iduser_req) {$status_req='Visible';} else {$status_req='Submitted';}
		$sql2=" INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) VALUES ('$id_user','$idrecipe_req','$status_req','Du nouveau !','$iduser_req')";
		debugtrace($sql);
		mysqli_query($cxn,'SET NAMES UTF8');
		$result2 = mysqli_query($cxn,$sql2);
		if (!$result) { debugtrace("error cxn "); exiterror("error cxn");}
	}
 }
mysqli_close($cxn);
header("Location: ix.php");
exit;

function exiterror($p){	
  header("Location: ix.php?recipenum=".$_POST['pknum']."&edit=yes&logmessage=".urlencode($p));
  exit;
}
exit;
?>
