<?php
/* Program: createnewfamily.php
 * Create new user  in userdb and add fill the cookbook with the familyrecipe
 * input : id_user, name, family, pwd
 * output : false or true
 */

 $debug=1;
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 /// check input
 $family_req=$_POST['family'];
 $name_req=$_POST['name'];
 $pass_req=$_POST['pwd'];
 $iduser_req=$_POST['iduser'];
 if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id );user ".$iduser_req);
 if (! preg_match($pat_pwd, $pass_req)) exitfail("Error in password ".$pass_req);
 if (! preg_match($pat_family, $family_req)) exitfail("Error in family ".$family_req);
 if (! preg_match($pat_name, $name_req)) exitfail("Error in name ".$name_req);
 /// device type
 if (isset($_POST['device'])) debugtrace($_POST['device']);
 else debugtrace("No device mentionned");
 /// check pwd
 $sql="SELECT pass FROM userdb WHERE family='$family_req' GROUP BY pass";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $row_count=$result->num_rows;
 if ($row_count!=1) exitfail("Too many or no pwd in family");
 $ligne=mysqli_fetch_assoc($result);
 $pwd_tocheck=$ligne['pass'];
 if ($pwd_tocheck!=$pass_req) exitfail("Wrong pwd");
 /// insert new user
 $sql="INSERT INTO `userdb`(`id_user`, `name`, `family`, `last_sync`, `pass`)
				VALUES ('$iduser_req','$name_req','$family_req',now(),'$pass_req') ";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result"); 
 /// new cookbook
 $sql="SELECT cookbooks.id_user, id_recipe, status,message,id_from FROM `cookbooks` 
			LEFT JOIN userdb ON cookbooks.id_user=userdb.id_user 
			WHERE userdb.family='$family_req' AND cookbooks.status='Visible'
			GROUP BY id_recipe";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitok("No result"); 
 $row_count=$result->num_rows;
 if ($row_count==0) exitok("No recipe found in family");
 debugtrace("Nb recipe= ".$row_count);
 while ($ligne=mysqli_fetch_assoc($result)){
	$idus=$ligne["id_user"];
	$idrec=$ligne["id_recipe"];
	$st= $ligne["status"]; 
	$me= $ligne["message"]; 
	$idf=$ligne["id_from"];
		$sql="INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) 
					VALUES ('$iduser_req','$idrec','$st','$me','$idf')";
		debugtrace($sql);
		mysqli_query($cxn,'SET NAMES UTF8');
		$result2 = mysqli_query($cxn,$sql);
		if (!$result2) exitok("Failed to add recipe");
}
echo $found;
debugtrace("Success");
mysqli_close($cxn);
exit;
?>

