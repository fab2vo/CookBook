<?php
/* Program: acceptorrefuserequest.php
 * Manege the acceptance or denial of a recipe request 
 * input : pknum of request, status (ACCEPT/DENIED) iduser (pwd) message
 * output : false or true
 */
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 /// inputs
 $iduser_req=$_POST['iduser'];
 $pknum_req=$_POST['pknum'];
 $status_req=$_POST['status'];
 if (isset($_POST['message'])) $message_req=addslashes($_POST['message']);
 else  $message_req="Message par defaut";
 $accepted="ACCEPTED";
 $refused="DENIED";
 if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user ".$iduser_req);
 if (! preg_match($pat_num, $pknum_req)) exitfail("Pknum inadequate ".$pknum_req);
 if (!in_array($status_req, array($accepted,$refused))) exitfail("Error in status request ".$status_req);
 if (! preg_match($pat_etape, $message_req)) exitfail("Error in message  ".$message_req);
 //input pwd from $id_user_req
 include ("itchekpwd.inc"); 
 /* Trouver la requete a partir de son pknum => status, idfrom, idrecipe */
 $sql="SELECT * FROM `requestdb` WHERE pknum='$pknum_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result"); 
 $row_count=$result->num_rows;
 if ($row_count==0) exitfail("Requete non trouvee "); 
 $ligne=mysqli_fetch_assoc($result);
 $idrecipe_req=$ligne['id_recipe'];
 $idfrom_req=$ligne['id_from'];
 $status_db=$ligne['status'];
 if ($status_db==$accepted) {
	 if ($status_req==$accepted) exitok("Already accepted ! "); 
	 if ($status_req==$refused) exitok("Can not accept denied request "); 
	 }
 if ($status_db==$refused) {
	 if ($status_req==$accepted) exitok("Can not deny accepted recipe"); 
	 if ($status_req==$refused) exitok("Already denied "); 
	 }
 if (($status_req!='ACCEPTED')&&($status_req!='DENIED')) exitfail("Status non identified ".$status_req); 
 debugtrace("Status checked");
 
 /* Case Denied requested */
 if ($status_req==$refused) {
	$sql="UPDATE `requestdb` SET `status`='$status_req' WHERE pknum='$pknum_req'";
	debugtrace($sql);
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) exitfail("No result");
	else exitok("Request DENIED executed "); 
 } 
 
	 
 /* La recette est-elle deja ds cookbook ? */
 $sql="SELECT pknum FROM `cookbooks` WHERE id_recipe='$idrecipe_req' AND id_user='$idfrom_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result"); 
 $row_count=$result->num_rows;
 if ($row_count!=0) exitok("Recette deja presente ! "); // il faudrait effacer la requete
 
 /* Celui qui demande l'acceptation est-il bien le propriétaire de la recette ?*/
 $sql="SELECT id_user FROM `recipedb` WHERE id_recipe='$idrecipe_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result"); 
 $row_count=$result->num_rows;
 if ($row_count!=1) exitok("Recette non unique "); 
 $ligne=mysqli_fetch_assoc($result);
 $iduser_db=$ligne['id_user'];
 if ( $iduser_db!= $iduser_req) exitok("Request from owner is not recipe owner ");
 
 /* Ajouter la recette à soumettre */
 $status_recipe="Submitted";
 $sql="INSERT INTO `cookbooks`(`id_user`, `id_recipe`, `status`, `message`, `id_from`) 
	VALUES ('$idfrom_req' ,'$idrecipe_req','$status_recipe',' $message_req','$iduser_req')";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result)  exitfail("Recette non ajoutee ");
 
 /* Mettre à jour accepted*/
 $sql="UPDATE `requestdb` SET `status`='$status_req' WHERE pknum='$pknum_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result)  exitfail("Request non update "); 
 echo $found;
 exitsuccess($cxn);
?>

