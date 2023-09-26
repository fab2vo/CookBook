<?php
/* Program: Login.php 
*/
session_start();
$debug=0;
$in_get=$_POST['family'];
include ('idconnect.inc');
			if ($debug==1) {
				$open=file_get_contents("debug.txt");
				$open .=" family : ";
				$open.=$in_get;
				$open .=" -> ";
				file_put_contents("debug.txt",$open); 
			}
switch (@$_POST['family']) 
{
case 'nofamily': 
			if ($debug==1) {
				$open=file_get_contents("debug.txt");
				$open .=" path nologin ";
				$open .=" -> ";
				file_put_contents("debug.txt",$open); 
			}
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
	}
	if ($ok=='ok'){
		$_SESSION['auth']='yes'; 
		$_SESSION['family'] = $family_req;
		$expire = time()+60*60*24*30;
		setcookie("cb_family", $family_req, $expire);
		$_SESSION['member'] = $member_req;
		setcookie("cb_member", $member_req, $expire);
		$_SESSION['id_user'] = $id_user;
			if ($debug==1) {
				$open=file_get_contents("debug.txt");
				$open .=" path login ok, id_user=  ";
				$open.=$id_user;
				$open .=" -> ";
				file_put_contents("debug.txt",$open); 
		}
	header("Location: sitememberpage.php");
	} else {
		unset($do); 
		$message='Utilisateur non trouv&#233 <br>';
			if ($debug==1) {
				$open=file_get_contents("debug.txt");
				$open .=" path login nok, message=  ";
				$open.=$message;
				$open .=" -> ";
				file_put_contents("debug.txt",$open); 
			}
		include("login_form.inc");
	}
}


?>