<?php
/* Program: getrecipefromCB.php
 * Download a recipes witout photo  
 * input : idrecipe
 * output : format recipe in JSON 
 */

 include ("idconnect.inc");
$debug=0;
if ($debug==1) { file_put_contents("debug.txt","Debut getrecipefromCB \r\n"); }
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

 $cxn = mysqli_connect($host,$user,$password,$nombase,$port);
 if (!$cxn)
 {
	$message=mysqli_connect_error($cxn);
	echo $notfound;
 	exit;
 }

  $idrecipe_req=$_POST['idrecipe'];
  $iduser_req=$_POST['iduser'];

 $sql="SELECT r.*, u2.id_user AS id_owner, 
		u2.family AS owner_family, u2.name AS owner_name,
		message, status, id_from,u3.family AS from_family, u3.name AS from_name
		FROM cookbooks c
		INNER JOIN userdb u1 ON c.id_user=u1.id_user
		INNER JOIN recipedb r ON c.id_recipe=r.id_recipe
		INNER JOIN userdb u2 ON r.id_user=u2.id_user 
		LEFT JOIN userdb u3 ON c.id_from=u3.id_user 
		WHERE c.id_user='$iduser_req' AND r.id_recipe='$idrecipe_req' ";
 debugtrace($sql);
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

