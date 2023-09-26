<?php
/* Program: createnewfamily.php
 * Create new user  in userdb and add fill the cookbook with the familyrecipe
 * input : id_user, name, family, pwd
 * output : false or true
 */

 include ("idconnect.inc");

 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port))
 {
	$message=mysqli_error($cxn);
	echo $notfound;
 	exit;
 }

 $family_req=$_POST['family'];
 $name_req=$_POST['name'];
 $pass_req=$_POST['pwd'];
 $iduser_req=$_POST['iduser'];

 $sql="INSERT INTO `userdb`(`id_user`, `name`, `family`, `last_sync`, `pass`)
				VALUES ('$iduser_req','$name_req','$family_req',now(),'$pass_req') ";
 

 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);

 if (!$result)
{	
	echo $notfound;
 	exit;
} 

 $sql="SELECT cookbooks.id_user, id_recipe, status,message,id_from FROM `cookbooks` 
			LEFT JOIN userdb ON cookbooks.id_user=userdb.id_user 
			WHERE userdb.family='$family_req'
			GROUP BY id_recipe";
 
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);

 if (!$result)
{	echo $notfound;
 	exit;
} 
$test=false;
while ($ligne=mysqli_fetch_assoc($result)){
	$test=true;
	$idus=$ligne["id_user"];
	$idrec=$ligne["id_recipe"];
	$st= $ligne["status"]; 
	$me= $ligne["message"]; 
	$idf=$ligne["id_from"]; 
	if ($st=='Visible'){
		$sql="INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) 
					VALUES ('$iduser_req','$idrec','$st','$me','$idf')";
		mysqli_query($cxn,'SET NAMES UTF8');
		$result2 = mysqli_query($cxn,$sql);
		if (!$result2) {echo $notfound; exit;}
	}	
}
if ($test)	{
	echo $found;}
else { echo $notfound;}
 	exit;
	mysqli_close($cxn);
?>

