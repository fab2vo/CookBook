<?php
/* Program: clearnet.php
 * Nettoyage BdD 
 *
 * input : mot de passe
 * output : none
 */

 include ("idconnect.inc");
 // debug
 $debug=0;
 if ($debug==1) {
 file_put_contents("debug.txt","Debut getcommunitynews");  
 }
 //DEBUG
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
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port))
 {
	$mess=mysqli_error($cxn);
	debugtrace($mess);
	echo $notfound;
 	exit;
 }
 $iduser_req=$_POST['iduser'];
 if ($iduser_req!="FDx") { debugtrace("Not admin"); echo $notfound; exit;} 
 /* Doublons dans commentsdb */
 $sql="SELECT * FROM `commentsdb` ORDER BY id_recipe,comment";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { debugtrace("Fail A"); echo $notfound; exit;} 
	$row_count=$result->num_rows;
	if ($row_count==0) {debugtrace("No comments !? "); echo $notfound;exit; }
	echo "Doublons dans commentsdb :<BR>";
	$emparray=array();
	$nextrecipe="";
	$nextcomment="";
	while ($ligne=mysqli_fetch_assoc($result)){
		extract($ligne);
		if (($nextrecipe==$id_recipe)&&($nextcomment==$comment)) {
		echo $id_recipe." pknum=".$pknum." : ".$comment."  (".$date_comment.")<BR>";}
		$nextrecipe=$id_recipe;
		$nextcomment=$comment;
	}
	echo "<BR>";
	
  /* Doublons dans notesdb */
 $sql="SELECT * FROM `notesdb` ORDER BY id_recipe,date_note, id_user";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { debugtrace("Fail B"); echo $notfound; exit;} 
	$row_count=$result->num_rows;
	if ($row_count==0) {debugtrace("No notes !? "); echo $notfound;exit; }
	echo "Doublons dans notesdb :<BR>";
	$emparray=array();
	$nextrecipe="";
	$nextnote="";
	$nextiduser="";
	$nextdate="";
	while ($ligne=mysqli_fetch_assoc($result)){
		extract($ligne);
		if (($nextrecipe==$id_recipe)&&($nextnote==$note)&&($nextdate==$date_note)&&($nextiduser==$id_user)) {
			echo $id_recipe." pknum=".$pknum." : ".$note."  le ".$date_note." par".$id_user."<BR>";
		}
		$nextrecipe=$id_recipe;
		$nextnote=$note;
		$nextdate=$date_note;
		$nextiduser=$id_user;
		
	}
	echo "<BR>";
	
	  /* Recettes orphelines */
 $sql="SELECT r.pknum, r.id_recipe, r.title, x.recipecount
	FROM recipedb AS r
	LEFT JOIN (SELECT id_recipe, COUNT(id_user) AS recipecount FROM cookbooks GROUP BY id_recipe) AS x 
		ON x.id_recipe=r.id_recipe";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { debugtrace("Fail C"); echo $notfound; exit;} 
	$row_count=$result->num_rows;
	if ($row_count==0) {debugtrace("No notes !? "); echo $notfound;exit; }
	echo "Recette orpheline si nb=0 :<BR>";
	echo "<table><tbody>";
	$emparray=array();
	while ($ligne=mysqli_fetch_assoc($result)){
		extract($ligne);
		$att="";
		echo "<tr>";
		if ($recipecount==0) $att="Orpheline";
			echo "<td>".$id_recipe."</td><td> pkum=".$pknum." </td><td> nb= ".$recipecount." </td><td> ( ".$title." ) ".$att."</td>";
		echo "</tr>";
	}
	echo "</tbody></table>";
	echo "<BR>";
	
		  /* Utilisateurs sans recette */
 $sql="SELECT u.pknum, u.name, u.family, u.id_user,x.recipecount FROM userdb u
	LEFT JOIN (SELECT id_user, COUNT(id_recipe) AS recipecount FROM cookbooks GROUP BY id_user) as x
		ON u.id_user=x.id_user
	ORDER BY u.family";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { debugtrace("Fail D"); echo $notfound; exit;} 
	$row_count=$result->num_rows;
	if ($row_count==0) {debugtrace("No user !? "); echo $notfound;exit; }
	echo "Utilsateur stérile si nb=0 :<BR>";
	$emparray=array();
	echo "<table><tbody>";
	while ($ligne=mysqli_fetch_assoc($result)){
		extract($ligne);
		$att="";
		echo "<tr>";
		if ($recipecount==0) $att=" STERILE";
			echo "<td>".$id_user."</td><td> pkum=".$pknum." </td><td>".$name."@".$family." : </td><td> nb= ".$recipecount."</td><td>   ".$att."</td>";
		echo "</tr>";
	}
	echo "</tbody></table>";
	echo "<BR>";
	
/* Utilisateurs orphelins dans cookbooks */
 $sql="SELECT id_user, c.pknum FROM cookbooks c LEFT JOIN userdb USING (id_user) WHERE userdb.id_user IS NULL";
 debugtrace($sql);
 echo "Détection des utilisateurs orphelins dans cookbooks:<BR>";
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { debugtrace("Fail E"); echo $notfound; exit;} 
	$row_count=$result->num_rows;
	if ($row_count==0) {echo "Pas d'utilisateur orphelin ds cookbooks"; }
	else {
		$emparray=array();
		while ($ligne=mysqli_fetch_assoc($result)){
			extract($ligne);
			echo $id_user." pkum=".$pknum." <BR>";
		}
	}
	echo "<BR>";
	echo "<BR>";
	
/* Recettes orphelines dans cookbooks */
 $sql="SELECT id_recipe, c.pknum FROM cookbooks c LEFT JOIN recipedb USING (id_recipe) WHERE recipedb.id_recipe IS NULL";
 debugtrace($sql);
 echo "Détection des recettes orphelines dans cookbooks:<BR>";
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) { debugtrace("Fail F"); echo $notfound; exit;} 
	$row_count=$result->num_rows;
	if ($row_count==0) {echo "Pas de recette orpheline ds cookbooks"; }
	else {
		$emparray=array();
		while ($ligne=mysqli_fetch_assoc($result)){
			extract($ligne);
			echo $id_recipe." pkum=".$pknum." <BR>";
		}
	}
	echo "<BR>";
	echo "<BR>";
mysqli_close($cxn); 
?>

