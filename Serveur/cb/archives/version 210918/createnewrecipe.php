<?php
/* Program: createnewrecipe.php
 * Create new entry into recipedb
 * input : all fields
 * output : false or true
 */

 include ("idconnect.inc");

 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port))
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
 $type_req=$_POST['type'];
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

 $sql=" INSERT INTO `recipedb`(`id_recipe`,`id_user`,`lastupdate_recipe`, `lastupdate_photo`, `title`, `source`, `source_url`, `nb_pers`, 
								`etape01`, `etape02`, `etape03`, `etape04`, `etape05`, `etape06`, `etape07`, `etape08`, `etape09`,
								`season`, `difficulty`,`type`,
								`ing01`, `ing02`, `ing03`, `ing04`, `ing05`, `ing06`, `ing07`, `ing08`, `ing09`, `ing10`,
								`ing11`, `ing12`, `ing13`, `ing14`, `ing15`)
		VALUES ( '$idrecipe_req','$iduser_req',now(),'','$title_req', '$source_req', '$sourceurl_req','$nbpers_req',
			'$etape01_req','$etape02_req','$etape03_req','$etape04_req','$etape05_req', 
			'$etape06_req','$etape07_req','$etape08_req','$etape09_req',
			'$season_req','$difficulty_req','$type_req',
			'$ing01_req','$ing02_req','$ing03_req','$ing04_req','$ing05_req',
			'$ing06_req','$ing07_req','$ing08_req','$ing09_req','$ing10_req',
			'$ing11_req','$ing12_req','$ing13_req','$ing14_req','$ing15_req') ";

 //echo $sql."<BR>";
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);

 if (!$result)
{	
	echo $notfound;
 	exit;
} 
echo $found;
  mysqli_close($cxn);
?>

