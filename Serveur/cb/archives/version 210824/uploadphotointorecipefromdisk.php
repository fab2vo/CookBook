<?php
/* Program: uploadphotointorecipefromdisk.php
 * Update in recipedb the photo
 * input : id_recipe, file
 * output : image false or true
 */

 include ("idconnect.inc");

 if (! $cxn = mysqli_connect($host,$user,$password, $nombase))
 {
	$message=mysqli_error($cxn);
	echo $notfound;
 	exit;
 }

 $idrecipe_req=$_POST['idrecipe'];
 $file_req=$_POST['file']; 
 
 /*$file_req = 'image/location.jpg';
if ( !is_readable($file_req) ) {
    header('file not found', 404);
    die;
}
else {
    header('Content-Type: image/jpeg');
    $loadimg = imagecreatefromjpeg($file_req);
    if ( !$loadimg ) {
        header('unable to process resource', 404); // 404 is not the correct error code for this....
        die;
    }
    imagejpeg($loadimg); //affichage de l'image
} */
 echo "Le fichier est ";
 $file_req="C:/wamp/www/cb/".$file_req;
 echo $file_req."<BR>";
 echo "<BR>";
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

 $src='data: '.mime_content_type($file_req).';base64,'.$image_req;
 echo '<img src="'.$src.'"/>';
  echo "<BR>";
  echo $found;
  mysqli_close($cxn);
  
?>

