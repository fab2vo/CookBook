<?php
/* Program: getrecipestampstiers.php
 * Extract the stamps from the recipes not by the user in the  
 * cookbook of the user.
 * input : id_user (pwd)
 * ouput JSON : id_recipe, lastupdate recipe and photo id_owner status message idfrom 
 */


 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 // check inputs
 $iduser_req=$_POST['iduser'];
 if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user ".$iduser_req);
 //input pwd from $id_user_req
 include ("itchekpwd.inc");
 //main
 $sql="SELECT cookbooks.id_recipe, lastupdate_recipe, lastupdate_photo, recipedb.id_user AS id_owner, status, message, id_from 
				FROM cookbooks 
				LEFT JOIN recipedb ON cookbooks.id_recipe=recipedb.id_recipe 
				WHERE cookbooks.id_user='$iduser_req' AND recipedb.id_user !='$iduser_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $emparray=array();
 while ($ligne=mysqli_fetch_assoc($result)){
	$emparray[]=$ligne;
 }
 $str=json_encode($emparray);
 echo $str;
 exitsuccess($cxn);
?>

