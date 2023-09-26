<?php
/* Program: sendmail.php
 * Make an entry into cookbook with status Submitted
 * input : idrecipe  message idfrom (pwd) family and membre from user to
 * output : false or true
 */

 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));

 $idrecipe_req=$_POST['idrecipe'];
 $idfrom_req=$_POST['idfrom'];
 $family_req=addslashes($_POST['family']);
 $membre_req=addslashes($_POST['membre']);
 $message_req=addslashes($_POST['message']); 
 // check inputs
 if (! preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe ".$idrecipe_req);
 if (! preg_match($pat_uuid, $idfrom_req)) exitfail("Error in Id user from ".$idfrom_req);
 if (! preg_match($pat_family, $family_req)) exitfail("Error in family ".$family_req);
 if (! preg_match($pat_name, $membre_req)) exitfail("Error in member ".$membre_req);
 if (! preg_match($pat_etape, $message_req)) exitfail("Error in message  ".$message_req);
 //input pwd from $idfrom_req
 $iduser_req=$idfrom_req;
 include ("itchekpwd.inc");
 // recherche du user
 $sql="SELECT `id_user` FROM `userdb` WHERE name='$membre_req' AND family='$family_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $row_count=$result->num_rows;
 if ($row_count!=1) exitfail("No or too many users");
 $ligne=mysqli_fetch_assoc($result);
 $iduser_req=$ligne['id_user'];
 // recherche si existe déjà
 $sql="SELECT id_user FROM `cookbooks` WHERE id_user='$iduser_req' AND id_recipe='$idrecipe_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $row_member_count=$result->num_rows;
 if ($row_member_count>0) exitok("Request already exists");
 // creat entry
 $sql=" INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) VALUES ('$iduser_req','$idrecipe_req','Submitted','$message_req','$idfrom_req')";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result)
 if (!$result) exitfail("No result");
 echo $found;
 exitsuccess($cxn);  
?>

