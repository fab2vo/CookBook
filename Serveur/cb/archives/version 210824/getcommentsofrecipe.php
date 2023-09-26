<?php
/* Program: getcommentsofrecipe.php
 * Download the comments related to a recipe 
 * input : idrecipe
 * output : comments  in JSON 
 */

 include ("idconnect.inc");

 $cxn = mysqli_connect($host,$user,$password,$nombase);
 if (!$cxn)
 {
	$message=mysqli_connect_error($cxn);
	echo $notfound;
 	exit;
 }
 $idrecipe_req=$_POST['idrecipe'];

  $sql="SELECT 	comment,
				date_comment,
				commentsdb.id_user,
				family,
				name
				FROM commentsdb 
				INNER JOIN userdb ON userdb.id_user=commentsdb.id_user
				WHERE commentsdb.id_recipe='$idrecipe_req'"; 

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

