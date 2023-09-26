<?php
/* Program: getrecipestamps.php
 * Extract the stamps from the recipes in the  
 * cookbook of the user.
 * input : id_user
 * ouput JSON : id_recipe, lastupdate recipe and photo id_owner status message idfrom 
 */

 include ("idconnect.inc");

 if (! $cxn = mysqli_connect($host,$user,$password, $nombase))
 {
	$message=mysqli_error($cxn);
	echo $notfound;
 	exit;
 }

 $user_req=$_GET['id_user'];

 $sql="SELECT cookbooks.id_recipe,
				lastupdate_recipe,
				lastupdate_photo,
				recipedb.id_user AS id_owner,
				status, 
				message, 
				id_from 
				FROM cookbooks 
   LEFT JOIN recipedb ON cookbooks.id_recipe=recipedb.id_recipe 
   WHERE cookbooks.id_user='$user_req'";

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

