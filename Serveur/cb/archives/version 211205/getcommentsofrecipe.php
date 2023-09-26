<?php
/* Program: getcommentsofrecipe.php
 * Download the comments related to a recipe 
 * input : idrecipe (user pwd)
 * output : comments  in JSON 
 */

 $debug=0;
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 // check inputs
 $idrecipe_req=$_POST['idrecipe'];
 if (! preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe ".$idrecipe_req);
 //check pwd
 include ("itchekuser.inc");
 // main
 $sql="SELECT comment, date_comment, commentsdb.id_user, family, name
				FROM commentsdb 
				INNER JOIN userdb ON userdb.id_user=commentsdb.id_user
				WHERE commentsdb.id_recipe='$idrecipe_req'"; 
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

