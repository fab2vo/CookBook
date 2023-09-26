<?php
/* Program: Login.php 
*/
session_start();
$in_get=$_POST['family'];
include ("ikonexct007.inc");
debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
debugtrace(" field family : ".$in_get);

switch (@$_POST['family']) 
{
case 'nofamily': 
debugtrace("path nologin ");			
include('login_form.inc');
break;
default: 
	$family_req=$_POST['family'];
	$member_req=$_POST['member'];
	$password_req=$_POST['password'];
	// recherche ds bdD -----------------------
	$ok='nok';
	$cxn = mysqli_connect($host,$user,$password,$nombase,$port);
	if ($cxn)
	{
		$sql="SELECT id_user
			FROM userdb  
			WHERE family='$family_req' AND name='$member_req' AND pass='$password_req'";
		mysqli_query($cxn,'SET NAMES UTF8');
		$result = mysqli_query($cxn,$sql);
		if (!$result) {} else {
			$row_member_count=$result->num_rows;
			if ($row_member_count!=0) { 
				$ok='ok';
				$ligne=mysqli_fetch_assoc($result);
				extract($ligne);
			} 
		}
		debugtrace("ok : ".$ok);
	}
	if ($ok=='ok'){
		$_SESSION['auth']='yes'; 
		$_SESSION['family'] = $family_req;
		$expire = time()+60*60*24*30;
		setcookie("cb_family", $family_req, $expire);
		$_SESSION['member'] = $member_req;
		setcookie("cb_member", $member_req, $expire);
		$_SESSION['id_user'] = $id_user;
		debugtrace("path login ok, id_user= ".$id_user);
		ob_flush();
        header("Location: sitememberpage.php");
        exit();
	} else {
		unset($do); 
		$message='Utilisateur non trouv&#233 <br>';
		debugtrace($message);
		include("login_form.inc");
	}
}


?>