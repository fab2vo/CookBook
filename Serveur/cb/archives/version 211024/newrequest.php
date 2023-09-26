<?php
/* Program: newrequest.php
 * Make an entry into requestdb with status Issued at the present date
 * input : idrecipe  idfrom message 
 * output : false or true
 */
 header('Content-type: text/html; charset=UTF-8');
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

 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port))
 {
	$message=mysqli_error($cxn);
	echo $notfound;
 	exit;
 }

 $idrecipe_req=$_POST['idrecipe'];
 $idfrom_req=$_POST['idfrom'];
 $message_req=addslashes($_POST['message']); 
 
 // check idfrom 
 $sql="SELECT `id_user` FROM `userdb` WHERE id_user='$idfrom_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { debugtrace("No such user : ".$idfrom_req);echo $notfound; exit;}
 $row_count=$result->num_rows;
 if ($row_count!=1) { debugtrace("No or several users : ".$idfrom_req); echo $notfound; exit;}
  // check idrecipe & owner different idfrom
 $sql="SELECT `id_user` FROM `recipedb` WHERE id_recipe='$idrecipe_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { debugtrace("No such recipe : ".$idrecipe_req);echo $notfound; exit;}
 $row_count=$result->num_rows;
 if ($row_count!=1) { debugtrace("No or several recipes : ".$idrecipe_req); echo $notfound; exit;}
 $ligne=mysqli_fetch_assoc($result);
 $idowner=$ligne['id_user']; 
 if ($idowner==$idfrom_req) { debugtrace("requestor = recipe owner !"); echo $notfound; exit;}
 // insert new request
 $seta=utf8_encode($message_req);
 $sql=" INSERT INTO requestdb( `id_recipe`, `id_from`, `date_creation`, `message`) 
				VALUES ('$idrecipe_req','$idfrom_req',now(),'$seta')"; 
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result)
 {	
	$message=mysqli_error($cxn);
	debugtrace("Issue when INSERT request ".$message);
	echo $notfound;
 	exit;
 } 
echo $found;
mysqli_close($cxn);  
?>

