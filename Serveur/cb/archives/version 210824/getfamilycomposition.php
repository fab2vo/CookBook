<?php
/* Program: getfamilycomposition.php
 * Get the family members from the family  
 * input : family,
 * output : id_user, name, pass, last_sync 
 */

 include ("idconnect.inc");

 $cxn = mysqli_connect($host,$user,$password,$nombase);
 if (!$cxn)
 {
	$message=mysqli_connect_error($cxn);
	echo $notfound;;
 	exit;
 }

 $family_req=$_POST['family'];

 $sql="SELECT id_user,
				name,
				last_sync,
				pass
				FROM userdb  
   WHERE family='$family_req'";

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

