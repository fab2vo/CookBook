<?php
/* Program: getnotesofrecipe.php
 * Download the notes related to a recipe 
 * input : idrecipe (user pwd)
 * output : notes  in JSON 
 */

 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 // check inputs
 $idrecipe_req=$_POST['idrecipe'];
 //check pwd
 include ("itchekuser.inc");
 // main
  $sql="SELECT 	note, notesdb.id_user, date_note, family, name
				FROM notesdb 
				INNER JOIN userdb ON userdb.id_user=notesdb.id_user
				WHERE notesdb.id_recipe='$idrecipe_req'"; 
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
