<?php
/* Program: uploadphotointorecipe.php
 * Update in recipedb the photo
 * input : id_recipe, image,date (pwd)
 * output : false or true
 */

 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));

 $idrecipe_req=$_POST['idrecipe'];
 $image_req=$_POST['image'];
 $date_req=$_POST['date'];
 /// check inputs
 if (! preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe ".$idrecipe_req);
 if (! preg_match($pat_date, $date_req)) exitfail("Error in date format ".$date_req);
 // find user and check pwd
 $iduser_req=finduserofrecipe($cxn, $idrecipe_req );
 include ("itchekpwd.inc"); 
   // update recipe
 $sql="UPDATE `recipedb` SET `photo`= '$image_req',`lastupdate_photo`='$date_req'  
				WHERE id_recipe='$idrecipe_req'";
 debugtrace("UPDATE ...");
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 /// success !
 echo $found;
 exitsuccess($cxn);
?>

