<?php
/* Program: sendmail.php
 * Make an entry into cookbook with status Submitted
 * input : idrecipe  message idfrom family and membre from user
 * output : false or true
 */

 include ("idconnect.inc");
 $debug=0;
if ($debug==1) { file_put_contents("debug.txt","Debut \r\n "); }
function debugtrace($sin)
{
 global $debug;
 if ($debug==1) {
	$open=file_get_contents("debug.txt");
	$open .=$sin."\r\n";
	file_put_contents("debug.txt",$open); 
	}
 return;
}

 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port))
 {
	$message=mysqli_error($cxn);
	echo $notfound;
 	exit;
 }

 $idrecipe_req=$_POST['idrecipe'];
 $idfrom_req=$_POST['idfrom'];
 $family_req=addslashes($_POST['family']);
 $membre_req=addslashes($_POST['membre']);
 $message_req=addslashes($_POST['message']); 
 // recherche du user
 $sql="SELECT `id_user` FROM `userdb` WHERE name='$membre_req' AND family='$family_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { debugtrace("C ");echo $notfound; exit;}
 $row_count=$result->num_rows;
 if ($row_count!=1) { debugtrace("D "); echo $notfound; exit;}
 $ligne=mysqli_fetch_assoc($result);
 $iduser_req=$ligne['id_user'];
 // recherche si existe déjà
 $sql="SELECT id_user FROM `cookbooks` WHERE id_user='$iduser_req' AND id_recipe='$idrecipe_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { debugtrace("E "); echo $notfound; exit;}
 $row_member_count=$result->num_rows;
 if ($row_member_count>0) { debugtrace("Existe deja "); echo $notfound; exit;} 
 $sql=" INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) VALUES ('$iduser_req','$idrecipe_req','Submitted','$message_req','$idfrom_req')";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result)
 {	
	debugtrace("G ");
	echo $notfound;
 	exit;
 } 


echo $found;
mysqli_close($cxn);  
?>

