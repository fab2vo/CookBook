<?php
/* Program: uploadphotointorecipe.php
 * Update in recipedb the photo
 * input : id_recipe, image,date
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
 $image_req=$_POST['image'];
 $date_req=$_POST['date'];

 $sql="UPDATE `recipedb` SET `photo`= '$image_req',`lastupdate_photo`='$date_req'  
				WHERE id_recipe='$idrecipe_req'";

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

