<?php
/* Program: moverecipetocb.php
 * Create new entry into cookbooksdb
 * input : iduser, idrecipe, status, message, idfrom
 * output : false or true
 */

 include ("idconnect.inc");

 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port))
 {
	$message=mysqli_error($cxn);
	echo $notfound;
 	exit;
 }
 $iduser_req=$_POST['iduser'];
 $idrecipe_req=$_POST['idrecipe'];
 $status_req=$_POST['status'];
 $message_req=$_POST['message'];
 $idfrom_req=$_POST['idfrom'];

 $sql="INSERT INTO `cookbooks` (`id_user`, `id_recipe`, `status`, `message`, `id_from`)
				VALUES ('$iduser_req','$idrecipe_req','$status_req','$message_req','$idfrom_req') ";

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

