<?php
/* Program: addnote.php
 * Create new rating for a recipe 
 * input : idrecipe idfrom note
 * output : false or true
 */

 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 
 $idrecipe_req=$_POST['idrecipe'];
 $iduser_req=$_POST['idfrom'];
 $note_req=trim($_POST['note']);
 /// check inputs
 if (! preg_match("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})", $idrecipe_req)) exitfail("Error in Id recipe ".$idrecipe_req);
 if (! preg_match("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})", $iduser_req)) exitfail("Error in Id user ".$iduser_req);
 if (! preg_match("([1-5]{1})", $note_req)) exitfail("Error in note ".$note_req);
 /// check if recipe exists
 $sql="SELECT * FROM `recipedb` WHERE id_recipe='$idrecipe_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No recipe found : ".$idrecipe_req);
 $row_count=$result->num_rows;
 if ($row_count!=1) exitfail("No / too many recipe found : ".$idrecipe_req);
 /// check if user exists
 $sql="SELECT * FROM userdb WHERE id_user='$iduser_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No user found : ".$iduser_req);
 $row_count=$result->num_rows;
 if ($row_count!=1) exitfail("No / too many user found :".$iduser_req);	
 /// add a note	 
 $sql="INSERT INTO notesdb (`id_recipe`, `note`, `id_user`, `date_note`)
		VALUES ( '$idrecipe_req', '$note_req', '$iduser_req', now()) ";		
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No note added");
 /// success
 debugtrace("success");
 echo $found;
 mysqli_close($cxn);
?>

