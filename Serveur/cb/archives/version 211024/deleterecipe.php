<?php
/* Program: deleterecipe.php
 * Remove from cookbook a recipe
 * input : idrecipe iduser
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

 $sql="DELETE FROM `cookbooks` WHERE id_user='$iduser_req' AND id_recipe='$idrecipe_req'";		

 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 $count=mysqli_affected_rows($cxn);
 if (!$result)
{	
	echo $notfound;
 	exit;
} 
 if ($count==0)
{	
	echo $notfound;
 	exit;
} 
echo $found;
  mysqli_close($cxn);
?>

