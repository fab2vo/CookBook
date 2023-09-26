<?php
/* Program: acceptrecipe.php
 * Update cookbooks to change submitted into visible
 * input : iduser and idrecipe (pwd)
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
 //main
 $sql="UPDATE `cookbooks` SET `status`='Visible' WHERE id_recipe='$idrecipe_req' AND id_user='$iduser_req' ";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 echo $found;
 exitsuccess($cxn);  
?>

