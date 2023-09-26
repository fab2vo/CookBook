<?php
/* Program: getfamilycomposition.php
 * Get the family members from the family  
 * input : family,
 * output : id_user, name, pass, last_sync 
 */

 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 
 //// input and test
 $family_req=$_POST['family'];
 if (! preg_match($pat_family, $family_req)) exitfail("Error in family ".$family_req);
 /// find family members
 $sql="SELECT id_user,name, last_sync, pass FROM userdb  
       WHERE family='$family_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $row_count=$result->num_rows;
 if ($row_count==0) exitfail("Nobody in family ".$family_req);	
 /// success
 $emparray=array();
 while ($ligne=mysqli_fetch_assoc($result)){
	$emparray[]=$ligne;
 }
  $str=json_encode($emparray);
  echo $str;
  exitsuccess($cxn);
?>

