<?php
/* Program: acceptrecipe.php
 * Update cookbooks to change submitted into visible
 * input : iduser and idrecipe
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

 $sql="UPDATE `cookbooks` SET `status`='Visible' WHERE id_recipe='$idrecipe_req' AND id_user='$iduser_req' ";
 
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

