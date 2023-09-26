<?php
/* Program: getnotes.php
 * Download the notes related to the user's cookbook  
 * input : iduser (pwd)
 * output : notes  in JSON 
 */

 $debug=0;
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 /// check input
 $iduser_req=$_POST['iduser'];
 if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user ".$iduser_req);
 //input pwd from $id_user_req
 include ("itchekpwd.inc");
 /// check if user exists
 $sql="SELECT * FROM userdb WHERE id_user='$iduser_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $row_count=$result->num_rows;
 if ($row_count!=1) exitfail("No / too many user found :".$iduser_req);	
 /// get notes
  $sql="SELECT notesdb.id_recipe,
				note,
				notesdb.id_user,
				date_note,
				family,
				name
				FROM notesdb 
   INNER JOIN cookbooks ON cookbooks.id_recipe=notesdb.id_recipe
   INNER JOIN userdb ON userdb.id_user=notesdb.id_user
   WHERE cookbooks.id_user='$iduser_req'"; 
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 /// success
 $nbr=0;
 $emparray=array();
 while ($ligne=mysqli_fetch_assoc($result)){
	$emparray[]=$ligne;
	$nbr++;
 }
  $str=json_encode($emparray);
  echo $str;
  debugtrace("Number of notes :".$nbr);
  mysqli_close($cxn);
?>

