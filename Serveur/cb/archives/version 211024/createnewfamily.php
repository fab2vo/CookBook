<?php
/* Program: createnewfamily.php
 * Create new user  in userdb and add a welcome recipe in cookbooksdb
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
 debugtrace("-----------New Family------------");
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
	debugtrace("Family creation failed");
	echo $notfound;
 	exit;
} 

 $sql="INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) 
			VALUES ('$iduser_req','f819bcc2-ab09-4ed8-8a7c-08fade172da2','Submitted','Bienvenue !','c81d4e2e-bcf2-11e6-869b-7df92533d2db')";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);

 if (!$result)
{	
	debugtrace("Inserting first recipe failed");
} 
	echo $found;
 	exit;
  mysqli_close($cxn);
?>

