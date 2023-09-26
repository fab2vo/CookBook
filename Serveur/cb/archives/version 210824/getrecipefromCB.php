<?php
/* Program: getrecipefromCB.php
 * Download a recipes witout photo  
 * input : idrecipe
 * output : format recipe in JSON 
 */

 include ("idconnect.inc");

 $cxn = mysqli_connect($host,$user,$password,$nombase);
 if (!$cxn)
 {
	$message=mysqli_connect_error($cxn);
	echo $notfound;
 	exit;
 }

 $idrecipe_req=$_POST['idrecipe'];
 $withphototest=$_POST['withphoto'];

 if ($withphototest==1) {
	$sql="SELECT `id_recipe`, `id_user` AS id_owner, `lastupdate_recipe`, `lastupdate_photo`, 
			`title`, `source`, `source_url`, `nb_pers`, 
			`etape01`, `etape02`, `etape03`, `etape04`, `etape05`, `etape06`, `etape07`, `etape08`, `etape09`, 
			`season`, `difficulty`, 
			`ing01`, `ing02`, `ing03`, `ing04`, `ing05`, `ing06`, `ing07`, `ing08`, `ing09`, `ing10`,
			`ing11`, `ing12`, `ing13`, `ing14`, `ing15`,`photo` 
			FROM `recipedb`  
		WHERE id_recipe='$idrecipe_req'";
 } else {
	  $sql="SELECT `id_recipe`, `id_user` AS id_owner, `lastupdate_recipe`, `lastupdate_photo`, 
			`title`, `source`, `source_url`, `nb_pers`, 
			`etape01`, `etape02`, `etape03`, `etape04`, `etape05`, `etape06`, `etape07`, `etape08`, `etape09`, 
			`season`, `difficulty`, 
			`ing01`, `ing02`, `ing03`, `ing04`, `ing05`, `ing06`, `ing07`, `ing08`, `ing09`, `ing10`,
			`ing11`, `ing12`, `ing13`, `ing14`, `ing15` 
			FROM `recipedb`  
		WHERE id_recipe='$idrecipe_req'";
 }
		
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);

 if (!$result)
{	
	echo $notfound;
 	exit;
} 
 $emparray=array();
 while ($ligne=mysqli_fetch_assoc($result)){
	$emparray[]=$ligne;
 }
  $str=json_encode($emparray);
  echo $str;
  mysqli_close($cxn);
?>

