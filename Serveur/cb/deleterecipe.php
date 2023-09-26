<?php
/* Program: deleterecipe.php
 * Remove from cookbook a recipe
 * input : idrecipe iduser (pwd)
 * output : false or true
 */

 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 // check inputs
 $idrecipe_req=$_POST['idrecipe'];
 $iduser_req=$_POST['iduser'];
 if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user ".$iduser_req);
 if (! preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe ".$idrecipe_req);
 //input pwd from $id_user_req
 include ("itchekpwd.inc");
 // delete
 $sql="DELETE FROM `cookbooks` WHERE id_user='$iduser_req' AND id_recipe='$idrecipe_req'";		
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $count=mysqli_affected_rows($cxn);
 if ($count==0) exitfail("No delete");
 // success
 echo $found;
 exitsuccess($cxn);
?>

