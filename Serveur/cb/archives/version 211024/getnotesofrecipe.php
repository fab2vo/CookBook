<?php
/* Program: getnotes.php
 * Download the notes related to a recipe 
 * input : idrecipe
 * output : notes  in JSON 
 */

 include ("idconnect.inc");

 $cxn = mysqli_connect($host,$user,$password,$nombase,$port);
 if (!$cxn)
 {
	$message=mysqli_connect_error($cxn);
	echo $notfound;
 	exit;
 }

$idrecipe_req=$_POST['idrecipe'];

  $sql="SELECT 	note,
				notesdb.id_user,
				date_note,
				family,
				name
				FROM notesdb 
				INNER JOIN userdb ON userdb.id_user=notesdb.id_user
				WHERE notesdb.id_recipe='$idrecipe_req'"; 

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

