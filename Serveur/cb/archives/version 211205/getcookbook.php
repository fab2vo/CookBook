<?php
/* Program: getcoobkook.php
 * Download the full recipes from user  
 * input : iduser, pwd
 * output : format recipe in JSON 
 */

 $debug=0;
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 /// check input
 $iduser_req=$_POST['iduser'];
 $pwd_req=$_POST['pwd'];
 if (! preg_match("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})", $iduser_req)) exitfail("Error in Id user ".$iduser_req);
 if (! preg_match("([!\?-_()a-zA-Z0-9]{3,25})", $pwd_req)) exitfail("Error in password ".$pwd_req);
 /// check if user exists
 $sql="SELECT * FROM userdb WHERE id_user='$iduser_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $row_count=$result->num_rows;
 if ($row_count!=1) exitfail("No / too many user found :".$iduser_req);	
 /// get recipes
 $sql="SELECT r.*, u2.id_user AS id_owner, 
		u2.family AS owner_family, u2.name AS owner_name,
		message, status, id_from,u3.family AS from_family, u3.name AS from_name
		FROM cookbooks c
		INNER JOIN userdb u1 ON c.id_user=u1.id_user
		INNER JOIN recipedb r ON c.id_recipe=r.id_recipe
		INNER JOIN userdb u2 ON r.id_user=u2.id_user 
		LEFT JOIN userdb u3 ON c.id_from=u3.id_user 
		WHERE c.id_user='$iduser_req' AND u1.pass='$pwd_req' ";
		
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
  debugtrace("Number of recipes :".$nbr);
  mysqli_close($cxn);
?>

