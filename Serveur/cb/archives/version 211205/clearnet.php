<?php
/* Program: clearnet.php
 * Nettoyage BdD 
 *
 * input : mot de passe
 * output : none
 */

 $debug=0;
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 // check
 $iduser_req=$_POST['keyuser'];
 if ($iduser_req!="FDx")  exitfail("Not admin"); 
	
	  /* Recettes orphelines */
 $sql="SELECT r.pknum, r.id_recipe, r.title, x.recipecount, r.id_user
	FROM recipedb AS r
	LEFT JOIN (SELECT id_recipe, COUNT(id_user) AS recipecount FROM cookbooks GROUP BY id_recipe) AS x 
		ON x.id_recipe=r.id_recipe";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result mysqli"); 
	$row_count=$result->num_rows;
	if ($row_count==0) exitfail("No recipes !?"); 
	echo "<h3>Recipe list :</h3><BR>";
	echo "<table><thead><td>Recipe Id</td><td>pkum</td><td> Nb users</td><td> Title and Owner</td></thead><tbody>";
	$emparray=array();
	while ($ligne=mysqli_fetch_assoc($result)){
		extract($ligne);
			debugtrace($title);
			$author="No author";
			$sql="SELECT pknum, name, family FROM userdb WHERE id_user='$id_user'";
			debugtrace($sql);
			mysqli_query($cxn,'SET NAMES UTF8');
			$result2 = mysqli_query($cxn,$sql);
			if (!$result2) exitfail("No result mysqli"); 
			$row_count2=$result2->num_rows;
			if ($row_count2==1) {
				$ligne2=mysqli_fetch_assoc($result2);
				$author=$ligne2['name']."@".$ligne2['family'];
				}
		$sql="SELECT id_recipe FROM cookbooks WHERE id_user='$id_user' AND id_recipe='$id_recipe'";
		debugtrace($sql);
		$result3 = mysqli_query($cxn,$sql);
		if (!$result3) exitfail("No result mysqli");
		$row_count3=$result3->num_rows;
		$atz=" (Not in owner cookbook)";
		if ($row_count3==1) $atz=""; 
		$att="";
		echo "<tr>";
		if ($recipecount==0) $att="Orphan";
		echo "<td>".$id_recipe."</td><td> ".$pknum." </td><td>  ".$recipecount." </td><td>  ".$title." --> ".$author." ".$att."  ".$atz."</td>";
		echo "</tr>";
	}
	echo "</tbody></table>";
	echo "<BR>";
	
		  /* Utilisateurs sans recette */
 $sql="SELECT u.pknum, u.name, u.family, u.id_user,x.recipecount FROM userdb u
	LEFT JOIN (SELECT id_user, COUNT(id_recipe) AS recipecount FROM cookbooks GROUP BY id_user) as x
		ON u.id_user=x.id_user
	ORDER BY u.pknum";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result mysqli");
	$row_count=$result->num_rows;
	if ($row_count==0) exitfail("No user !? "); 
	echo "<h3>User list </h3><BR>";
	$emparray=array();
	echo "<table><thead><td>User Id</td><td>pknum</td><td>Owner</td><td> Nb recipe</td><td> </td></thead><tbody>";
	while ($ligne=mysqli_fetch_assoc($result)){
		extract($ligne);
		$att="";
		echo "<tr>";
		if ($recipecount==0) $att=" STERILE";
		echo "<td>".$id_user."</td><td>  ".$pknum." </td><td>".$name."@".$family." : </td><td>  ".$recipecount."</td><td>   ".$att."</td>";
		echo "</tr>";
	}
	echo "</tbody></table>";
	echo "<BR>";
 /* Doublons dans commentsdb */
 $sql="SELECT * FROM `commentsdb` ORDER BY id_recipe,comment";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result mysqli"); 
	$row_count=$result->num_rows;
	if ($row_count==0) exitfail("No comments !? "); 
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
 if (!$result) exitfail("No result mysqli"); 
	$row_count=$result->num_rows;
	if ($row_count==0) exitfail("No notes !? ");
	echo "Doublons dans notesdb :<BR>";
	$emparray=array();
	$nextrecipe="";
	$nextnote="";
	$nextiduser="";
	$nextdate="";
	while ($ligne=mysqli_fetch_assoc($result)){
		extract($ligne);
		if (($nextrecipe==$id_recipe)&&($nextnote==$note)&&($nextdate==$date_note)&&($nextiduser==$id_user)) {
			echo "Recette ".$id_recipe." (pknum=".$pknum.") note ".$note."  le ".$date_note." par ".$id_user."<BR>";
		}
		$nextrecipe=$id_recipe;
		$nextnote=$note;
		$nextdate=$date_note;
		$nextiduser=$id_user;
		
	}
	echo "<BR>";
	
/* Utilisateurs orphelins dans cookbooks */
 $sql="SELECT id_user, c.pknum FROM cookbooks c LEFT JOIN userdb USING (id_user) WHERE userdb.id_user IS NULL";
 debugtrace($sql);
 echo "Détection des utilisateurs orphelins dans cookbooks:<BR>";
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result mysqli");
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
 if (!$result) exitfail("No result mysqli");
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

