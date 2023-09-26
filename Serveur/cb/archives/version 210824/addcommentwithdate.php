<?php
/* Program: addcomment.php
 * Create new comment for a recipe 
 * input : idrecipe comment idfrom 
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
 $comment_req=addslashes($_POST['comment']);
 $date_req=$_POST['date'];

 $sql="INSERT INTO `commentsdb` (`id_recipe`, `comment`, `id_user`, `date_comment`)
		VALUES ( '$idrecipe_req', '$comment_req', '$iduser_req', '$date_req') ";

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

