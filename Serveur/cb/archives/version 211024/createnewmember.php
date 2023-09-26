<?php
/* Program: createnewfamily.php
 * Create new user  in userdb and add fill the cookbook with the familyrecipe
 * input : id_user, name, family, pwd
 * output : false or true
 */

 include ("idconnect.inc");
//DEBUG
 $debug=1;
 function debugtrace($sin)
{
 global $debug;
 if ($debug==1) {
	$open=file_get_contents("memberlog.txt");
	$open .=$sin."\r\n";
	file_put_contents("memberlog.txt",$open); 
	}
 return;
}

 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port))
 {
	$message=mysqli_error($cxn);
	echo $notfound;
 	exit;
 }
 debugtrace("-----------New Member------------");
 $family_req=$_POST['family'];
 $name_req=$_POST['name'];
 $pass_req=$_POST['pwd'];
 $iduser_req=$_POST['iduser'];
 
 if (isset($_POST['device'])) debugtrace($_POST['device']);
 else debugtrace("No device mentionned");

 if (($family_req=="")||($name_req=="")||($pass_req=="")) {debugtrace("Empty field");echo $notfound;exit;}
 if ($iduser_req=="") {debugtrace("No ID user"); echo $notfound; exit;}
 
 $sql="INSERT INTO `userdb`(`id_user`, `name`, `family`, `last_sync`, `pass`)
				VALUES ('$iduser_req','$name_req','$family_req',now(),'$pass_req') ";
 
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);

 if (!$result)
{	
	echo $notfound;
 	exit;
} 

 $sql="SELECT cookbooks.id_user, id_recipe, status,message,id_from FROM `cookbooks` 
			LEFT JOIN userdb ON cookbooks.id_user=userdb.id_user 
			WHERE userdb.family='$family_req' AND cookbooks.status='Visible'
			GROUP BY id_recipe";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 $row_count=$result->num_rows;
 if ((!$result)||($row_count==0))
{	debugtrace("No recipe found");
	echo $found;
 	exit;
} 
debugtrace($row_count);
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
		if (!$result2) {debugtrace("Failed to add recipe"); echo $found; exit;}
}
echo $found;
mysqli_close($cxn);
exit;
?>

