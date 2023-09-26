<?php
/* Program: getrequests.php
 * Download the requests related to the user 
 * input : iduser (pwd)
 * output : requests in JSON 
 */
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 /// inputs
 $iduser_req=$_POST['iduser'];
 if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user ".$iduser_req);
 //input pwd from $id_user_req
 include ("itchekpwd.inc"); 
 // Main
 $sql="SELECT rq.pknum, rq.id_recipe, rq.id_from, u.family, u.name, date_creation, message, rq.status, re.title
		FROM requestdb rq 
		LEFT JOIN recipedb re ON re.id_recipe=rq.id_recipe
		LEFT JOIN userdb u ON u.id_user=rq.id_from
		WHERE rq.status='ISSUED' AND re.id_user='$iduser_req'"; 
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result"); 
 $emparray=array();
 while ($ligne=mysqli_fetch_assoc($result)){
	$emparray[]=$ligne;
 }
  $str=json_encode($emparray);
  echo $str;
  exitsuccess($cxn);
?>

