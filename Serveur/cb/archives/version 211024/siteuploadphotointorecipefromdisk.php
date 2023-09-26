<?php
/* Program: uploadphotointorecipefromdisk.php
 * Update in recipedb the photo
 * input : id_recipe, file
 * output : image false or true
 */

 include ("idconnect.inc");
//DEBUG
$debug=0;
 function debugtrace($sin)
{
 global $debug;
 if ($debug==1) {
	$open=file_get_contents("debug.txt");
	$open .=$sin."\r\n";
	file_put_contents("debug.txt",$open); 
	}
 return;
}
//DEBUG
 if ($debug==1) {
 file_put_contents("debug.txt","Debut siteeditrecipe \r\n ");  
 }
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port))
 {
	$message=mysqli_error($cxn);
	debugtrace($message);
 	exit;
 }

 $idrecipe_req=$_POST['idrecipe'];
 $file_req=$_POST['file']; 

 $file_req="C:/wamp/www/cb/".$file_req;

 $img = file_get_contents($file_req);
 if (!$img) {
	 echo "file get content return false";
 }
 $image_req=base64_encode($img);

$sql="UPDATE `recipedb` SET `photo`= '$image_req',`lastupdate_photo`=now()  
				WHERE id_recipe='$idrecipe_req'";

 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
  if (!$result)
{	
	echo $notfound;
 	exit;
} 

 //$src='data: '.mime_content_type($file_req).';base64,'.$image_req;
 //echo '<img src="'.$src.'"/>';
 // echo "<BR>";
 
  mysqli_close($cxn);
  header("Location: sitememberpage.php");
exit;
?>

