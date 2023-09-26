<?php
/* Program: getrequests.php
 * Download the requests related to the user 
 * input : iduser
 * output : requests in JSON 
 */

 include ("idconnect.inc");
 ///////////////// DEBUG
  $debug=0;
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
 if ($debug==1) {
	file_put_contents("debug.txt","Debut newrequest \r\n ");  
 }
 ///////////////////
 $cxn = mysqli_connect($host,$user,$password,$nombase,$port);
 if (!$cxn)
 {
	$message=mysqli_connect_error($cxn);
	debugtrace($message);
	echo $notfound;
 	exit;
 }

 $iduser_req=$_POST['iduser'];

 $sql="SELECT rq.pknum, rq.id_recipe, rq.id_from, u.family, u.name, date_creation, message, rq.status, re.title
		FROM requestdb rq 
		LEFT JOIN recipedb re ON re.id_recipe=rq.id_recipe
		LEFT JOIN userdb u ON u.id_user=rq.id_from
		WHERE rq.status='ISSUED' AND re.id_user='$iduser_req'"; 

 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);

 if (!$result)
{	
	debugtrace("Is with select ...");
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

