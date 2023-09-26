<?php
/* Program: sendmail.php
 * Make an entry into cookbook with status Submitted
 * input : idrecipe  message idfrom family and membre from user
 * output : false or true
 */

 include ("idconnect.inc");
 
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase))
 {
	$message=mysqli_error($cxn);
	echo $notfound;
 	exit;
 }

 $idrecipe_req=$_POST['idrecipe'];
 $idfrom_req=$_POST['idfrom'];
 $family_req=addslashes($_POST['family']);
 $membre_req=addslashes($_POST['membre']);
 $message_req=addslashes($_POST['message']); 
 $sql="SELECT `id_user` FROM `userdb` WHERE name='$membre_req' AND family='$family_req'";
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { echo $notfound; exit;}
 $row_count=$result->num_rows;
 if ($row_count!=1) { echo $notfound; exit;}
 $ligne=mysqli_fetch_assoc($result);
 $iduser_req=$ligne['id_user'];
 
 $sql=" INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) VALUES ('$iduser_req','$idrecipe_req','Submitted','$message_req','$idfrom_req')";
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

