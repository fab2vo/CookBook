<?php
/* Program: sendrecipetosb.php
 * Make an entry into cookbook with status Submitted
 * input : idrecipe iduser idfrom
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
 $idfrom_req=$_POST['idfrom'];
 $message_req=addslashes($_POST['message']); 

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

