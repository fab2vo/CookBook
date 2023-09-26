<?php
/* Program: getcommunitynews.php
 * Collect latest recipe stamps 
 *
 * input : id_user (pwd), com_type, com_param, start, count (0,3 => 3 élements) 
 * 		 com_type=RECENT BESTNOTE POPULAR com_param WP ou WOP (with photo)
 * output : JSON title, season, type, difficulty, (photo), name, family, lastupdate_recipe, noteavg, notecount (notescore) 
 */
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 /// inputs
 $iduser_req=$_POST['iduser'];
 $comtype_req=addslashes($_POST['comtype']);  
 $comparam_req=addslashes($_POST['comparam']);
 $numstart_req=$_POST['start'];
 $numcount_req=$_POST['count'];
 if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user ".$iduser_req);
 if (!in_array($comtype_req, array('RECENT','BESTNOTE','POPULAR'))) exitfail("Error in comtype ".$comtype_req);
 if (!in_array($comparam_req, array('WP','','WOP'))) exitfail("Error in comparam ".$comparam_req);
 if (! preg_match($pat_num, $numstart_req)) exitfail("Indaquate numstart ".$numstart_req);
 if (! preg_match($pat_num, $numcount_req)) exitfail("Indaquate numcount ".$numcount_req);
  //input pwd from $id_user_req
 include ("itchekpwd.inc");
 /* Trouver la famille du user */
 $sql="SELECT family FROM `userdb` WHERE id_user='$iduser_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result"); 
 $ligne=mysqli_fetch_assoc($result);
 $family_req=$ligne['family'];
 if ($family_req=="") exitfail("No Family"); 
 $wp="";
 if	($comparam_req=="WP") $wp=", r.photo ";
	 
 if ($comtype_req=="RECENT"){
	 /* Trouver la recette */
	$sql="SELECT r.id_recipe, r.title, r.season, r.type,r.difficulty".$wp.", u.id_user, u.name, u.family, r.lastupdate_recipe, x.noteavg, x.notecount 
			FROM recipedb r
			LEFT JOIN userdb u ON u.id_user=r.id_user 
			LEFT JOIN (SELECT id_recipe, AVG(note) AS noteavg, COUNT(note) AS notecount FROM notesdb GROUP BY id_recipe) AS x 
				ON x.id_recipe=r.id_recipe 
			LEFT JOIN (SELECT id_recipe FROM cookbooks  WHERE id_user='$iduser_req') AS c
				ON c.id_recipe=r.id_recipe
			WHERE u.family!='$family_req' AND c.id_recipe IS NULL
			ORDER BY r.lastupdate_recipe DESC LIMIT ".$numstart_req.",".$numcount_req."";
	debugtrace($sql);
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
    if (!$result) exitfail("No result");  
	$row_count=$result->num_rows;
	if ($row_count==0) exitfail("No recipes found "); 
	$emparray=array();
	while ($ligne=mysqli_fetch_assoc($result)){
		$emparray[]=$ligne;
	}
	$str=json_encode($emparray);
	echo $str;
	exitsuccess($cxn);
 }
 if ($comtype_req=="BESTNOTE"){
	$sql="SELECT r.id_recipe,r.title, r.season, r.type, r.difficulty".$wp.", u.id_user, u.name, u.family, r.lastupdate_recipe, x.noteavg, x.notecount 
			FROM recipedb r
			LEFT JOIN (SELECT id_recipe,AVG(note) AS noteavg,COUNT(note) AS notecount, ROUND(AVG(note),2)+(AVG(note)>3.8)*COUNT(note)/(1+COUNT(note)/20)/20 AS notescore  
					FROM notesdb GROUP BY id_recipe ORDER BY notescore DESC ) AS x 
					ON x.id_recipe=r.id_recipe
			LEFT JOIN (SELECT id_recipe FROM cookbooks  WHERE id_user='$iduser_req') AS c
				ON c.id_recipe=r.id_recipe
			LEFT JOIN userdb u ON u.id_user=r.id_user
			WHERE u.family!='$family_req' AND c.id_recipe IS NULL
			ORDER BY x.notescore DESC LIMIT ".$numstart_req.",".$numcount_req."";
	debugtrace($sql);
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) exitfail("No result"); 
	$row_count=$result->num_rows;
	if ($row_count==0) exitfail("No recipes found ");
	$emparray=array();
	while ($ligne=mysqli_fetch_assoc($result)){
		$emparray[]=$ligne;
	}
	$str=json_encode($emparray);
	echo $str;
	exitsuccess($cxn);
 }
  if ($comtype_req=="POPULAR"){
	$sql="SELECT r.id_recipe,r.title, r.season, r.type, r.difficulty".$wp.", u.id_user, u.name, u.family, r.lastupdate_recipe, x.noteavg, x.notecount 
		FROM recipedb r
		LEFT JOIN (SELECT id_recipe, COUNT(id_user) AS recipecount FROM cookbooks GROUP BY id_recipe) AS y 
			ON r.id_recipe=y.id_recipe
		LEFT JOIN userdb u ON u.id_user=r.id_user
		LEFT JOIN (SELECT id_recipe,AVG(note) AS noteavg,COUNT(note) AS notecount, ROUND(AVG(note),2)+(AVG(note)>3.8)*COUNT(note)/(1+COUNT(note)/20)/20 AS notescore  
			FROM notesdb GROUP BY id_recipe ORDER BY notescore DESC ) AS x 
			ON x.id_recipe=r.id_recipe
		LEFT JOIN (SELECT id_recipe FROM cookbooks  WHERE id_user='$iduser_req') AS c
			ON c.id_recipe=r.id_recipe
		WHERE u.family!='$family_req' AND c.id_recipe IS NULL
		ORDER BY y.recipecount DESC, x.notescore DESC LIMIT ".$numstart_req.",".$numcount_req."";
	debugtrace($sql);
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) exitfail("No result"); 
	$row_count=$result->num_rows;
	if ($row_count==0) exitfail("No recipes found ");
	$emparray=array();
	while ($ligne=mysqli_fetch_assoc($result)){
		$emparray[]=$ligne;
	}
	$str=json_encode($emparray);
	echo $str;
	exitsuccess($cxn);
 }
 
 echo $notfound;
 mysqli_close($cxn); 
 exit; 
?>

