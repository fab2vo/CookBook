<?php
/* Program: siteuploadphotointorecipefromdisk.php
 * Update in recipedb the photo
 * input : id_recipe, file
 * output : image false or true
 */

 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 $target_dir = "uploads/";
 $target_file = $target_dir . basename($_FILES["file"]["name"]);
 $imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));
 
 if ($_FILES["file"]["size"] > 500000) {
  debugtrace( "Sorry, the file is too large.");
  header("Location: sitememberpage.php");
  exit;
 }

  if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file)) {
    debugtrace( "The file ". htmlspecialchars( basename( $_FILES["file"]["name"])). " has been uploaded.");
  } else {
    debugtrace( "Sorry, there was an error uploading the file ".htmlspecialchars( basename( $_FILES["file"]["name"])));
	header("Location: sitememberpage.php");
	exit;
  }


 $idrecipe_req=$_POST['idrecipe'];
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
 
 //$src='data: '.mime_content_type($file_req).';base64,'.$image_req;
 //echo '<img src="'.$src.'"/>';
 // echo "<BR>";
 
  mysqli_close($cxn);
  debugtrace("Success");
  header("Location: sitememberpage.php");
  exit;
?>

