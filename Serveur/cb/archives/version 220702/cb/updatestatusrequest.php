<?php
/* Program: updatestatusrequest.php
 * Update status of request pknum
 * input : pknum status 
 * output : false or true
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
	file_put_contents("debug.txt","Debut updatestatusrequest \r\n ");  
 }
 ///////////////////

 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port))
 {
	$message=mysqli_error($cxn);
	echo $notfound;
 	exit;
 }

 $pknum_req=$_POST['pknum'];
 $status_req=$_POST['status'];

 // check pknum
 $sql="SELECT * FROM `requestdb` WHERE pknum='$pknum_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { debugtrace("No such pknum : ".$pknum_req);echo $notfound; exit;}
 $row_count=$result->num_rows;
 if ($row_count!=1) { debugtrace("No or several pknum : ".$pknum_req); echo $notfound; exit;}
 //UPDATE
 $sql="UPDATE `requestdb` SET `status`='$status_req' WHERE pknum='$pknum_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result)
 {	
	debugtrace("no UPDATE");
	echo $notfound;
 	exit;
 } 
echo $found;
mysqli_close($cxn);  
?>

