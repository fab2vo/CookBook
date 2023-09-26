<?php
/* Program: getrecipefromCB.php
 * Download a recipe w/wo photo  
 * input : idrecipe (withphoto default no) (id_user et pwd)
 * output : format recipe in JSON 
 */

 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));

 $idrecipe_req=$_POST['idrecipe'];
 if (isset($_POST['withphoto'])){$withphototest=trim($_POST['withphoto']);}
 else {$withphototest="0";}
 /// check inputs
 if (! preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe ".$idrecipe_req);
 if (($withphototest!="0")&&($withphototest!="1")) exitfail("Error of withphoto ".$withphototest);
 /// check access if id_user and pwd
 include ("itchekuser.inc");
 /// Main
 $wp="";
 if ($withphototest==1) $wp="photo, ";
$sql="SELECT c.pknum, r.id_recipe, c.id_user, r.lastupdate_recipe, r.lastupdate_photo, 
			r.title, r.source, r.source_url, r.nb_pers, 
			etape01, etape02, etape03, etape04, etape05, etape06, etape07, etape08, etape09, 
			season, difficulty, type,
			ing01, ing02, ing03, ing04, ing05, ing06, ing07, ing08, ing09, ing10,
			ing11, ing12, ing13, ing14, ing15, ".$wp." 
			u2.family AS owner_family, u2.name AS owner_name,
			c.message, c.status,c.id_from,
			r.id_user AS id_owner, u3.family AS from_family, u3.name AS from_name
			FROM recipedb  r
			INNER JOIN userdb u2 ON r.id_user=u2.id_user 
			LEFT JOIN cookbooks c ON c.id_recipe=r.id_recipe AND c.id_user='$iduser_req' 
			LEFT JOIN userdb u3 ON c.id_from=u3.id_user
		WHERE r.id_recipe='$idrecipe_req'";

 debugtrace($sql);	
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $emparray=array();
 while ($ligne=mysqli_fetch_assoc($result)){
	$emparray[]=$ligne;
 }
  $str=json_encode($emparray);
  echo $str;
  exitsuccess($cxn);
?>

