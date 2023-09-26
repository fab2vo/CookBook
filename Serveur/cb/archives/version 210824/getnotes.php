<?php
/* Program: getnotes.php
 * Download the notes related to the user's cookbook  
 * input : iduser
 * output : notes  in JSON 
 */

 include ("idconnect.inc");

 $cxn = mysqli_connect($host,$user,$password,$nombase);
 if (!$cxn)
 {
	$message=mysqli_connect_error($cxn);
	echo $notfound;
 	exit;
 }

 $iduser_req=$_POST['iduser'];

  $sql="SELECT notesdb.id_recipe,
				note,
				notesdb.id_user,
				date_note,
				family,
				name
				FROM notesdb 
   INNER JOIN cookbooks ON cookbooks.id_recipe=notesdb.id_recipe
   INNER JOIN userdb ON userdb.id_user=notesdb.id_user
   WHERE cookbooks.id_user='$iduser_req'"; 

 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);

 if (!$result)
{	
	echo $notfound;
 	exit;
} 
 $emparray=array();
 while ($ligne=mysqli_fetch_assoc($result)){
	$emparray[]=$ligne;
 }
  $str=json_encode($emparray);
  echo $str;
  mysqli_close($cxn);
?>

