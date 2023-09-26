<?php
/* Program: createnewfamily.php
 * Create new user  in userdb and add a welcome recipe in cookbooksdb
 * input : id_user, name, family, pwd
 * output : false or true
 */

 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 /// check input
 $family_req=$_POST['family'];
 $name_req=$_POST['name'];
 $pass_req=$_POST['pwd'];
 $iduser_req=$_POST['iduser'];
 if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id );user ".$iduser_req);
 if (! preg_match($pat_pwd, $pass_req)) exitfail("Error in password ".$pass_req);
 if (! preg_match($pat_family, $family_req)) exitfail("Error in family -".$family_req."-");
 if (! preg_match($pat_name, $name_req)) exitfail("Error in name ".$name_req); 
 /// device type
 if (isset($_POST['device'])) debugtrace($_POST['device']);
 else debugtrace("No device mentionned");
 /// insert new user
 $sql="INSERT INTO `userdb`(`id_user`, `name`, `family`, `last_sync`, `pass`)
				VALUES ('$iduser_req','$name_req','$family_req',now(),'$pass_req') ";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 /// new cookbook with welcome recipe
 $sql="INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) 
			VALUES ('$iduser_req','f819bcc2-ab09-4ed8-8a7c-08fade172da2','Submitted','Bienvenue !','c81d4e2e-bcf2-11e6-869b-7df92533d2db')";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitok("Inserting first recipe failed"); 
 echo $found;
 exitsuccess($cxn);
?>

