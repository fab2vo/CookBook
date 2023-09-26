<?php
/* Program: getcommunitynews.php
 * Collect recipe stamps from CookBook community
 * input : 	id_user (pwd)
 *			comtype RECENT default BESTNOTE POPULAR 
 *			comparam WP WOP default (w/wo photo)
 *			start, count (0,3 => 3 items) 			
 * 			comscope ALL (default) CLOSE FOLLOWER 
 * output : JSON title, season, type, difficulty, (photo), name, family, lastupdate_recipe, noteavg, notecount (notescore) 
 */
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 /// inputs
 $iduser_req=$_POST['iduser'];
 if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user ".$iduser_req);
 include ("itchekpwd.inc");
 $numstart_req=$_POST['start'];
 $numcount_req=$_POST['count'];
 if (! preg_match($pat_num, $numstart_req)) exitfail("Indaquate numstart ".$numstart_req);
 if (! preg_match($pat_num, $numcount_req)) exitfail("Indaquate numcount ".$numcount_req);
 $comtype_req='RECENT';
 if (isset($_POST['comtype'])) $comtype_req=addslashes($_POST['comtype']);
 if (!in_array($comtype_req, array('RECENT','BESTNOTE','POPULAR'))) exitfail("Error in comtype ".$comtype_req); 
 $comparam_req='WOP'; 
 if (isset($_POST['comparam'])) $comparam_req=addslashes($_POST['comparam']);
 if (!in_array($comparam_req, array('WP','WOP'))) exitfail("Error in comparam ".$comparam_req);
 $comscope_req='ALL'; 
 if (isset($_POST['comscope'])) $comscope_req=addslashes($_POST['comscope']);
 if (!in_array($comscope_req, array('ALL','CLOSE','FOLLOWER'))) exitfail("Error in comscope ".$comscope_req);
 
 $wp="";
 if	($comparam_req=="WP") $wp=", r.photo ";
 $sql="SELECT r.id_recipe, r.title, r.season, r.type,r.difficulty".$wp.", u.id_user, u.name, u.family, r.lastupdate_recipe, x.noteavg, x.notecount
			FROM recipedb r
			LEFT JOIN userdb u ON u.id_user=r.id_user 
			LEFT JOIN (SELECT id_recipe FROM cookbooks  WHERE id_user='$iduser_req') AS c
				ON c.id_recipe=r.id_recipe
			LEFT JOIN (SELECT id_recipe,AVG(note) AS noteavg,COUNT(note) AS notecount, ROUND(AVG(note),2)+(AVG(note)>3.8)*COUNT(note)/(1+COUNT(note)/20)/20 AS notescore  
				FROM notesdb GROUP BY id_recipe ORDER BY notescore DESC ) AS x 
				ON x.id_recipe=r.id_recipe
			LEFT JOIN (SELECT id_recipe, COUNT(id_user) AS recipecount FROM cookbooks GROUP BY id_recipe) AS y 
				ON r.id_recipe=y.id_recipe
			";
 debugtrace("-".$comscope_req."-");
 switch($comscope_req){
	 case 'ALL':
		$sql =$sql."WHERE c.id_recipe IS NULL
			";break;
	 case 'CLOSE':
		$sql=$sql."LEFT JOIN (SELECT rz.id_user FROM recipedb rz LEFT JOIN cookbooks cz ON cz.id_user='$iduser_req' AND cz.status='Visible' WHERE cz.id_recipe=rz.id_recipe)AS z ON z.id_user=r.id_user
			WHERE c.id_recipe IS NULL AND z.id_user IS NOT NULL
			";break;
	 case 'FOLLOWER':
	 $sql=$sql."LEFT JOIN (SELECT rz.id_user FROM recipedb rz LEFT JOIN cookbooks cz ON cz.id_user='$iduser_req' AND cz.status='Visible' WHERE cz.id_recipe=rz.id_recipe)AS z ON z.id_user=r.id_user
			LEFT JOIN (SELECT n.id_user FROM notesdb n LEFT JOIN cookbooks cw ON cw.id_user='$iduser_req' AND cw.status='Visible' WHERE n.id_recipe=cw.id_recipe GROUP BY n.id_user) AS w ON w.id_user=r.id_user
			WHERE c.id_recipe IS NULL AND (z.id_user IS NOT NULL OR w.id_user IS NOT NULL)
			";
 }

 switch($comtype_req){
	 case 'RECENT':
		$sql=$sql."GROUP BY r.id_recipe ORDER BY r.lastupdate_recipe DESC LIMIT ".$numstart_req.",".$numcount_req.""; break;
	 case 'BESTNOTE':
		$sql=$sql."GROUP BY r.id_recipe ORDER BY x.notescore DESC LIMIT ".$numstart_req.",".$numcount_req.""; break;
	 case 'POPULAR':
		$sql=$sql."GROUP BY r.id_recipe ORDER BY y.recipecount DESC, x.notescore DESC LIMIT ".$numstart_req.",".$numcount_req.""; 
 }
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
?>

