<?php
/* Program: acceptorrefuserequest.php
 * Manege the acceptance or denial of a recipe request 
 * input : pknum of request, status (ACCEPT/DENIED) iduser message
 * output : false or true
 */

 include ("idconnect.inc");
 /* Prepare debug */
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
 
 if ($debug==1) {
 file_put_contents("debug.txt","Debut acceptorefuserequest \r\n ");  
 }
 /* Opening DB and data */
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port))
 {
	$message=mysqli_error($cxn);
	debugtrace("A ");
	echo $notfound;
 	exit;
 }
 $iduser_req=$_POST['iduser'];
 $pknum_req=$_POST['pknum'];
 $status_req=$_POST['status'];
 if (isset($_POST['message'])) $message_req=addslashes($_POST['message']);
 else  $message_req="Message par defaut";
 $accepted="ACCEPTED";
 $refused="DENIED";
 
 /* Trouver la requete */
 $sql="SELECT * FROM `requestdb` WHERE pknum='$pknum_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result)  {debugtrace("B "); echo $notfound; exit; } 
 $row_count=$result->num_rows;
 if ($row_count==0) {debugtrace("Requete non trouvee "); echo $notfound; exit;}
 $ligne=mysqli_fetch_assoc($result);
 $idrecipe_req=$ligne['id_recipe'];
 $idfrom_req=$ligne['id_from'];
 $status_db=$ligne['status'];
 if ($status_db==$accepted) {
	 if ($status_req==$accepted) {debugtrace("Already accepted ! "); echo $found; exit;}
	 if ($status_req==$refused) {debugtrace("Can not accept denied request "); echo $found; exit;}
	 }
 if ($status_db==$refused) {
	 if ($status_req==$accepted) {debugtrace("Can not deny accepted recipe"); echo $found; exit;}
	 if ($status_req==$refused) {debugtrace("ALready denied "); echo $found; exit;}
	 }
 if (($status_req!='ACCEPTED')&&($status_req!='DENIED')) {debugtrace("Status non identified ".$status_req); echo $notfound; exit; }
 debugtrace("Status checked");
 
 /* Case Denied*/
 if ($status_req==$refused) {
	$sql="UPDATE `requestdb` SET `status`='$status_req' WHERE pknum='$pknum_req'";
	debugtrace($sql);
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result)  {debugtrace("Request DENIED not done "); echo $notfound; exit; }
	else {debugtrace("Request DENIED executed "); echo $found; exit;}
 } 
 
	 
 /* La recette est-elle deja ds cookbook ? */
 $sql="SELECT pknum FROM `cookbooks` WHERE id_recipe='$idrecipe_req' AND id_user='$idfrom_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result)  {debugtrace("C "); echo $notfound; exit; } 
 $row_count=$result->num_rows;
 if ($row_count!=0) {debugtrace("Recette deja presente ! "); echo $found; exit;}
 
  /* Trouver utilisateur et test */
 $sql="SELECT id_user FROM `recipedb` WHERE id_recipe='$idrecipe_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result)  {debugtrace("D "); echo $notfound; exit; } 
 $row_count=$result->num_rows;
 if ($row_count!=1) {debugtrace("Recette non unique "); echo $found; exit;}
 $ligne=mysqli_fetch_assoc($result);
 $iduser_db=$ligne['id_user'];
 if ( $iduser_db!= $iduser_req) {debugtrace("Request from owner is not recipe owner "); echo $found; exit;}
 
 /* Ajouter la recette à soumettre */
 $status_recipe="Submitted";
 $sql="INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) 
	VALUES ('$idfrom_req' ,'$idrecipe_req','$status_recipe',' $message_req','$iduser_req')";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result)  {debugtrace("Recette non ajoutee "); echo $notfound; exit; } 
 
 /* Mettre à jour accepted*/
 $sql="UPDATE `requestdb` SET `status`='$status_req' WHERE pknum='$pknum_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result)  {debugtrace("Request non update "); echo $found; exit; } 
 
 echo $found;
 mysqli_close($cxn);
  

?>

