<?php
/* Program: Login.php page 348
*/
session_start(); 
include ("idconnect.inc");
switch (@$_GET[‘do’]) 
{
case “login”: 
	$family_req=$_POST['family'];
	$member_req=$_POST['member'];
	$password_req=$_POST['password'];
	// recherche ds bdD
	$ok="nok";
	if ($ok=="ok"){
		$_SESSION[‘auth’]=”yes”; 
		$_SESSION[‘family’] = $family_req;
		$_SESSION[‘member’] = $member_req;
		header(“Location: Member_page.php”);
	} else {
		unset($do); 
		$message=”Le nom de famille , ‘$_POST[family]’
				existe, mais le mot de passe est incorrect. Rectifier.<br>”;
		include(“login_form.inc”);
	}
	break;
default:
include(“login_form.inc”);
}
include(“login_form.inc”);
?>