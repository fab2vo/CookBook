<?php
/* Program: ixuploadphotointorecipefromdisk.php
 * Update in recipedb the photo
 * input : id_recipe, file
 * output : image false or true
 */

 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));

session_start();
 if ($_SESSION['auth']!="yes") exiterror("Not authorized");
 
 $target_dir = "uploads/";
 $target_file = $target_dir . basename($_FILES["file"]["name"]);
 $imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));

 $recipenum=$_POST['pknum'];

 if ($_FILES["file"]["size"] > 500000) {
  debugtrace( "Sorry, the file is too large.");
  $message="Sorry, the file is too large.";
  header("Location: ix.php?recipenum=".$recipenum."&edit=yes&logmessage=".urlencode($message));
  exit;
 }

  if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file)) {
    debugtrace( "The file ". htmlspecialchars( basename( $_FILES["file"]["name"])). " has been uploaded.");
  } else {
    debugtrace( "Sorry, there was an error uploading the file ".htmlspecialchars( basename( $_FILES["file"]["name"])));
	$message="Sorry, there was an error uploading the file";
	header("Location: ix.php?recipenum=".$recipenum."&edit=yes&logmessage=".urlencode($message));
	exit;
  }


 $idrecipe_req=$_POST['idrecipe'];
 $recipenum=$_POST['pknum'];
 debugtrace("recipe is ".$idrecipe_req);

 $img = file_get_contents($target_file);
 if (!$img) {
	 debugtrace("file ".$target_file." get content return false");
 }
 $image_req=base64_encode($img);
 $sql="UPDATE `recipedb` SET `photo`= '$image_req',`lastupdate_photo`=now()  
				WHERE id_recipe='$idrecipe_req'";

 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result mysqli"); 
  
  mysqli_close($cxn);
  debugtrace("Success");
  $message="Reload cookbook on your smartphone";
  header("Location: ix.php?recipenum=".$recipenum."&edit=yes&logmessage=".urlencode($message));
  exit;
?>

