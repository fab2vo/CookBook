<?php
/* Program: updateorcreaterecipe.php
 * Update if the recipe exists in recipedB
 * Or create new entry into recipedb and in insert it in the familyCB
 * in submitted status for the other
 * input : all fields of recipe but photo and date
 * output : false or true
 */

 include ("idconnect.inc");
 
 $debug=0;
 if ($debug==1) {
 file_put_contents("debug.txt","Debut");  
 }
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase))
 {
	$message=mysqli_error($cxn);
	echo $notfound;
 	exit;
 }
 $idrecipe_req=$_POST['idrecipe'];
 $iduser_req=$_POST['iduser'];
 $title_req=addslashes($_POST['title']);
 $source_req=addslashes($_POST['source']);
 $sourceurl_req=$_POST['sourceurl'];
 $nbpers_req=$_POST['nbpers'];
 $etape01_req=addslashes($_POST['etape01']); 
 $etape02_req=addslashes($_POST['etape02']);  
 $etape03_req=addslashes($_POST['etape03']); 
 $etape04_req=addslashes($_POST['etape04']); 
 $etape05_req=addslashes($_POST['etape05']); 
 $etape06_req=addslashes($_POST['etape06']);  
 $etape07_req=addslashes($_POST['etape07']); 
 $etape08_req=addslashes($_POST['etape08']); 
 $etape09_req=addslashes($_POST['etape09']);   
 $season_req=$_POST['season'];
 $difficulty_req=$_POST['difficulty'];
 $ing01_req=addslashes($_POST['ing01']);
 $ing02_req=addslashes($_POST['ing02']);
 $ing03_req=addslashes($_POST['ing03']);
 $ing04_req=addslashes($_POST['ing04']);
 $ing05_req=addslashes($_POST['ing05']);
 $ing06_req=addslashes($_POST['ing06']);
 $ing07_req=addslashes($_POST['ing07']);
 $ing08_req=addslashes($_POST['ing08']);
 $ing09_req=addslashes($_POST['ing09']);
 $ing10_req=addslashes($_POST['ing10']);
 $ing11_req=addslashes($_POST['ing11']);
 $ing12_req=addslashes($_POST['ing12']);
 $ing13_req=addslashes($_POST['ing13']);
 $ing14_req=addslashes($_POST['ing14']);
 $ing15_req=addslashes($_POST['ing15']);
 $date_req=$_POST['date'];
 
 /* Trouver la recette */
 $sql="SELECT * FROM `recipedb` WHERE id_recipe='$idrecipe_req'";
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 
if ($debug==1) {
 $open=file_get_contents("debug.txt");
 $open .="   ";
 $open.=$sql;
 $open .=" -> ";
 file_put_contents("debug.txt",$open); 
}
if (!$result)
{	
	echo $notfound;
 	exit;
} 
$row_count=$result->num_rows;

if ($row_count==0) {
	/* Si pas trouvée, INSERT une nouvelle entree dans recipedB */
 $sql=" INSERT INTO `recipedb`(`id_recipe`,`id_user`,`lastupdate_recipe`, `lastupdate_photo`, `title`, `source`, `source_url`, `nb_pers`, 
								`etape01`, `etape02`, `etape03`, `etape04`, `etape05`, `etape06`, `etape07`, `etape08`, `etape09`,
								`season`, `difficulty`,
								`ing01`, `ing02`, `ing03`, `ing04`, `ing05`, `ing06`, `ing07`, `ing08`, `ing09`, `ing10`,
								`ing11`, `ing12`, `ing13`, `ing14`, `ing15`)
		VALUES ( '$idrecipe_req','$iduser_req','$date_req','','$title_req', '$source_req', '$sourceurl_req','$nbpers_req',
			'$etape01_req','$etape02_req','$etape03_req','$etape04_req','$etape05_req', 
			'$etape06_req','$etape07_req','$etape08_req','$etape09_req',
			'$season_req','$difficulty_req',
			'$ing01_req','$ing02_req','$ing03_req','$ing04_req','$ing05_req',
			'$ing06_req','$ing07_req','$ing08_req','$ing09_req','$ing10_req',
			'$ing11_req','$ing12_req','$ing13_req','$ing14_req','$ing15_req') ";
 } else {
	 /* Si trouvée, UPDATE la ligne dans recipedB */
	 $sql="UPDATE `recipedb` SET `id_user`='$iduser_req',`lastupdate_recipe`='$date_req',
		`title`='$title_req',`source`='$source_req',`source_url`='$sourceurl_req',`nb_pers`='$nbpers_req',
		`etape01`='$etape01_req',`etape02`='$etape02_req',`etape03`='$etape03_req',`etape04`='$etape04_req',
		`etape05`='$etape05_req',`etape06`='$etape06_req',`etape07`='$etape07_req',`etape08`='$etape08_req',`etape09`='$etape09_req',
		`season`='$season_req',`difficulty`='$difficulty_req',
		`ing01`='$ing01_req',`ing02`='$ing02_req',`ing03`='$ing03_req',`ing04`='$ing04_req',`ing05`='$ing05_req',
		`ing06`='$ing06_req',`ing07`='$ing07_req',`ing08`='$ing08_req',`ing09`='$ing09_req',`ing10`='$ing10_req',
		`ing11`='$ing11_req',`ing12`='$ing12_req',`ing13`='$ing13_req',`ing14`='$ing14_req',`ing15`='$ing15_req'
		WHERE id_recipe='$idrecipe_req'";
 }
 
 if ($debug==1) {
 $open=file_get_contents("debug.txt");
 $open .=" Request:  ";
 $open.=$sql;
 $open .="  ";
 file_put_contents("debug.txt",$open); 
 }
 
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 
 if ($debug==1) {
 $open=file_get_contents("debug.txt");
 $open .=" -> ";
 file_put_contents("debug.txt",$open); 
 }
 if (!$result)
{	
	echo $notfound;
 	exit;
} 
if ($row_count==0) {
	/* Si nouvelle recette, INSERT une nouvelle entrée pour chaque membre de la famille */
	/* --- find family */
	$sql="SELECT `family` FROM `userdb` WHERE id_user='$iduser_req'";
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) { echo $notfound; exit;}
	$ligne=mysqli_fetch_assoc($result);
	$family_req=$ligne['family'];
	/* --- fetch family members */
	$sql="SELECT `id_user` FROM `userdb` WHERE family='$family_req'";
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) { echo $notfound; exit;}
	$row_member_count=$result->num_rows;
	if ($row_member_count==0) { echo $notfound; exit;} 
	while ($ligne=mysqli_fetch_assoc($result)) {
	/* --- insert for each member */
		extract($ligne);
		if ($id_user==$iduser_req) {$status_req='Visible';} else {$status_req='Submitted';}
		$sql2=" INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) VALUES ('$id_user','$idrecipe_req','$status_req','','')";
		if ($debug==1) {
			$open=file_get_contents("debug.txt");
			$open .=" Insert in CB -> ".$sql2." ";
			file_put_contents("debug.txt",$open); 
		}
		mysqli_query($cxn,'SET NAMES UTF8');
		$result2 = mysqli_query($cxn,$sql2);
		if (!$result2) { echo $notfound; exit;}
	}
 }

echo $found;
mysqli_close($cxn);  
?>

