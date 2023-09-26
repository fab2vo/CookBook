<?php
/* Program: newrequest.php
 * Make an entry into requestdb with status Issued at the present date
 * input : idrecipe  idfrom message 
 * output : false or true
 */
 header('Content-type: text/html; charset=UTF-8');
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 /// inputs
 $idrecipe_req=$_POST['idrecipe'];
 $idfrom_req=$_POST['idfrom'];
 $message_req=addslashes($_POST['message']); 
 $iduser_req=$idfrom_req;
 if (! preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe ".$idrecipe_req);
 if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user ".$iduser_req);
 if (! preg_match($pat_etape, $message_req)) exitfail("Error in message  ".$message_req);
 //input pwd from $id_user_req
 include ("itchekpwd.inc");
 // check idrecipe & owner different idfrom
 $sql="SELECT `id_user` FROM `recipedb` WHERE id_recipe='$idrecipe_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No such recipe : ".$idrecipe_req);
 $row_count=$result->num_rows;
 if ($row_count!=1) exitfail("No or several recipes : ".$idrecipe_req);
 $ligne=mysqli_fetch_assoc($result);
 $idowner=$ligne['id_user']; 
 if ($idowner==$idfrom_req) exitfail("requestor = recipe owner !");
 // insert new request
 $seta=utf8_encode($message_req);
 $sql=" INSERT INTO requestdb( `id_recipe`, `id_from`, `date_creation`, `message`) 
				VALUES ('$idrecipe_req','$idfrom_req',now(),'$seta')"; 
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 echo $found;
 exitsuccess($cxn);
 
?>

