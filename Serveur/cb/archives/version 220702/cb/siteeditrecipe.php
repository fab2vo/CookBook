<?php
?>	
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<?php
session_start();
if (@$_SESSION['auth']!='yes'){
	header("Location: login.php");
	exit;
}
echo'
	<html>
	<head>
	<title>Cookbook family</title>
	</head>
	<center>
	<body style="background-color:MediumSeaGreen;">
	<h1 style="color:Orange";>'.$_SESSION['member'].'@'.
	$_SESSION['family'].'</h1></center>';
include ('idconnect.inc');
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
$cxn = mysqli_connect($host,$user,$password,$nombase,$port);
$id_user_session=$_SESSION['id_user'];
$id_recipe_req=$_POST['id_recipe'];
// extract recipe
if ($cxn){
	$sql="SELECT `id_recipe`, `id_user`,`title`, `source`, `source_url`, `nb_pers`,
		`etape01`, `etape02`, `etape03`, `etape04`, `etape05`, `etape06`, `etape07`, `etape08`, `etape09`,
		`season`, `difficulty`, `type`, 
		`ing01`, `ing02`, `ing03`, `ing04`, `ing05`, `ing06`, `ing07`, `ing08`, `ing09`, `ing10`, `ing11`, `ing12`, `ing13`, `ing14`, `ing15`
		FROM recipedb WHERE id_recipe='$id_recipe_req' AND id_user='$id_user_session'";
	debugtrace($sql);
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	debugtrace(var_export($result,true));
	if (!$result) {exit;} 
	if (mysqli_num_rows($result)==0) {debugtrace("Not found");exit;}
	$ligne=mysqli_fetch_assoc($result);
	extract($ligne);
	date_default_timezone_set('Europe/Paris');
	$date=date("Y-m-d H:i:s");
} else { exit;}
echo'<table>
    <thead>
        <tr>
            <th colspan="2">Edition de la recette</th>
        </tr>
    </thead>';
echo'<form action="http://82.66.37.73:8085/cb/siteupdateorcreaterecipe.php" method="POST">
<tbody>
<input type="hidden" value="'.$id_recipe.'" name="idrecipe">
<input type="hidden" value="'.$id_user.'" name="iduser">
<input type="hidden" value="'.$date.'" name="date">
<tr><td>Titre</td><td><input type="text" value="'.$title.'" name="title" size="45"></td></tr>
<tr><td>Source</td><td><input type="text" value="'.$source.'" name="source" size="45"></td></tr>
<tr><td>URL source</td><td><input type="text" value="'.$source_url.'" name="sourceurl" size="45"></td></tr>
<tr><td>Nb personne</td><td><input type="text" value="'.$nb_pers.'" name="nbpers" size="5"></td></tr>
<tr><td>Season </td><td><input list="seasonlist" value="'.$season.'" name="season" size="45"></td></tr>
<datalist id="seasonlist">
  <option value="WINTER">
  <option value="SUMMER">
  <option value="ALLYEAR">
</datalist>
<tr><td>Difficulty </td><td><input list="difficultylist" value="'.$difficulty.'" name="difficulty" size="45"></td></tr>
<datalist id="difficultylist">
  <option value="QUICK">
  <option value="EASY">
  <option value="ELABORATE">
    <option value="SOPHISTICATED">
  <option value="UNDEFINED">
</datalist>
<tr><td>Type </td><td><input list="typelist" value="'.$type.'" name="type" size="45"></td></tr>
<datalist id="typelist">
  <option value="APERITIF">
  <option value="STARTER">
  <option value="MAIN">
  <option value="DESSERT">
  <option value="SIDE">
  <option value="OTHER">
</datalist>
<tr><td>Ingredient 01</td><td><textarea type="text" name="ing01" rows="1" cols="100">'.$ing01.'</textarea></td></tr>
<tr><td>Ingredient 02</td><td><textarea type="text" name="ing02" rows="1" cols="100">'.$ing02.'</textarea></td></tr>
<tr><td>Ingredient 03</td><td><textarea type="text" name="ing03" rows="1" cols="100">'.$ing03.'</textarea></td></tr>
<tr><td>Ingredient 04</td><td><textarea type="text" name="ing04" rows="1" cols="100">'.$ing04.'</textarea></td></tr>
<tr><td>Ingredient 05</td><td><textarea type="text" name="ing05" rows="1" cols="100">'.$ing05.'</textarea></td></tr>
<tr><td>Ingredient 06</td><td><textarea type="text" name="ing06" rows="1" cols="100">'.$ing06.'</textarea></td></tr>
<tr><td>Ingredient 07</td><td><textarea type="text" name="ing07" rows="1" cols="100">'.$ing07.'</textarea></td></tr>
<tr><td>Ingredient 08</td><td><textarea type="text" name="ing08" rows="1" cols="100">'.$ing08.'</textarea></td></tr>
<tr><td>Ingredient 09</td><td><textarea type="text" name="ing09" rows="1" cols="100">'.$ing09.'</textarea></td></tr>
<tr><td>Ingredient 10</td><td><textarea type="text" name="ing10" rows="1" cols="100">'.$ing10.'</textarea></td></tr>
<tr><td>Ingredient 11</td><td><textarea type="text" name="ing11" rows="1" cols="100">'.$ing11.'</textarea></td></tr>
<tr><td>Ingredient 12</td><td><textarea type="text" name="ing12" rows="1" cols="100">'.$ing12.'</textarea></td></tr>
<tr><td>Ingredient 13</td><td><textarea type="text" name="ing13" rows="1" cols="100">'.$ing13.'</textarea></td></tr>
<tr><td>Ingredient 14</td><td><textarea type="text" name="ing14" rows="1" cols="100">'.$ing14.'</textarea></td></tr>
<tr><td>Ingredient 15</td><td><textarea type="text" name="ing15" rows="1" cols="100">'.$ing15.'</textarea></td></tr>
<tr><td>Etape 1</td><td><textarea type="text" name="etape01" rows="4" cols="100">'.$etape01.'</textarea></td></tr>
<tr><td>Etape 2</td><td><textarea type="text" name="etape02" rows="4" cols="100">'.$etape02.'</textarea></td></tr>
<tr><td>Etape 3</td><td><textarea type="text" name="etape03" rows="4" cols="100">'.$etape03.'</textarea></td></tr>
<tr><td>Etape 4</td><td><textarea type="text" name="etape04" rows="4" cols="100">'.$etape04.'</textarea></td></tr>
<tr><td>Etape 5</td><td><textarea type="text" name="etape05" rows="4" cols="100">'.$etape05.'</textarea></td></tr>
<tr><td>Etape 6</td><td><textarea type="text" name="etape06" rows="4" cols="100">'.$etape06.'</textarea></td></tr>
<tr><td>Etape 7</td><td><textarea type="text" name="etape07" rows="4" cols="100">'.$etape07.'</textarea></td></tr>
<tr><td>Etape 8</td><td><textarea type="text" name="etape08" rows="4" cols="100">'.$etape08.'</textarea></td></tr>
<tr><td>Etape 9</td><td><textarea type="text" name="etape09" rows="4" cols="100">'.$etape09.'</textarea></td></tr>
<tr><td>  </td><td><input type="submit" value="Envoyez"></td></tr>	
</tbody>
</form><BR>';

echo'<table>
    <thead>
        <tr>
            <th colspan="2">Upload photo de C:/wamp/www/cb/</th>
    </thead>';
echo'<form action="http://82.66.37.73:8085/cb/siteuploadphotointorecipefromdisk.php" method="POST" enctype="multipart/form-data">
<tbody>
		<input type="hidden" value="'.$id_recipe.'" name="idrecipe">
		<tr><td>Fichier   </td><td><input type="file" name="file" accept="image/jpeg"></td></tr>
		<tr><td>  </td><td><input type="submit" value="Envoyez"></td></tr>	
 </tbody>
</form>
';
?>	
</table>
</body>
</html>