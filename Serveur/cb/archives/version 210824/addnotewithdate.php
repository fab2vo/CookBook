<?php
/* Program: addnote.php
 * Create new rating for a recipe 
 * input : idrecipe idfrom note
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
 $iduser_req=$_POST['idfrom'];
 $note_req=$_POST['note'];
 $date_req=$_POST['date'];

 $sql="INSERT INTO `notesdb` (`id_recipe`, `note`, `id_user`, `date_note`)
		VALUES ( '$idrecipe_req', '$note_req', '$iduser_req', '$date_req') ";		

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

