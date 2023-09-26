<?php
?>	
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<?php
	$sql="SELECT `id_recipe`, id_user AS id_user_recipe,`title`, `source`, `source_url`, `nb_pers`,
		`etape01`, `etape02`, `etape03`, `etape04`, `etape05`, `etape06`, `etape07`, `etape08`, `etape09`,
		`season`, `difficulty`, `type`, 
		`ing01`, `ing02`, `ing03`, `ing04`, `ing05`, `ing06`, `ing07`, `ing08`, `ing09`, `ing10`, `ing11`, `ing12`, `ing13`, `ing14`, `ing15`, photo
		FROM recipedb WHERE pknum='$recipenum'";
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) {exit;} 
	if (mysqli_num_rows($result)==0) {debugtrace("Not found");exit;}
	$ligne=mysqli_fetch_assoc($result);
	extract($ligne);
	date_default_timezone_set('Europe/Paris');
	$date=date("Y-m-d H:i:s");
	$src='data:image/png;base64,'.$photo;
echo'<center><h2 class="displaytitle">'.$title.'</h2></center>
<table>';
echo'				
<tbody>
<tr>
	<td rowspan="8" class="disimage">
		<form action="ixuploadphotointorecipefromdisk.php" method="POST" enctype="multipart/form-data">
		<input type="hidden" value="'.$id_recipe.'" name="idrecipe">
		<input type="hidden" value="'.$recipenum.'" name="pknum">
		<label><input type="file" name="file" accept="image/jpeg"></label>
		<input type="submit" value="Upload photo"></form>
		<img class="disimage" src="'.$src.' "/>
	</td>
		<form action="ixupdateorcreaterecipe.php" method="POST">
		<input type="hidden" value="'.$id_recipe.'" name="idrecipe">
		<input type="hidden" value="'.$recipenum.'" name="pknum">
		<input type="hidden" value="'.$id_user_recipe.'" name="iduser">
		<input type="hidden" value="'.$date.'" name="date">
	<td class="editlbl"><label></label></td><td><br></td>
</tr><tr>
	<td class="editlbl"><label> Titre </label></td><td><input type="text" value="'.$title.'" name="title" size="45"></td>
</tr><tr>
	<td class="editlbl"><label> Source</label></td><td><input type="text" value="'.$source.'" name="source" size="45"></td>
</tr><tr>
	<td class="editlbl"><label> URL source</label></td><td><input type="text" value="'.$source_url.'" name="sourceurl" size="45"></td>
</tr><tr>
	<td class="editlbl"><label> Nb personne</label></td><td><input type="text" value="'.$nb_pers.'" name="nbpers" size="5"></td>
</tr><tr>
	<td class="editlbl"><label> Season </label></td><td><input list="seasonlist" value="'.$season.'" name="season" size="45"></td>
</tr>
<datalist id="seasonlist">
  <option value="WINTER">
  <option value="SUMMER">
  <option value="ALLYEAR">
</datalist><tr>
	<td class="editlbl"><label> Difficulty</label> </td><td><input list="difficultylist" value="'.$difficulty.'" name="difficulty" size="45"></td>
</tr><datalist id="difficultylist">
  <option value="QUICK">
  <option value="EASY">
  <option value="ELABORATE">
  <option value="SOPHISTICATED">
  <option value="UNDEFINED">
</datalist><tr>
	<td class="editlbl"><label>Type </label></td><td><input list="typelist" value="'.$type.'" name="type" size="45"></td>
</tr><datalist id="typelist">
  <option value="APERITIF">
  <option value="STARTER">
  <option value="MAIN">
  <option value="DESSERT">
  <option value="SIDE">
  <option value="OTHER">
</datalist><tr></table>';


echo '<table><tbody>
<tr>
	<td colspan="2"><h3 class="displaytitle">Ingrédients </h3></td>
</tr><tr>
	<td ><label>Ingredient 1</label></td><td colspan="2"  ><textarea type="text" name="ing01" rows="1" cols="90">'.$ing01.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 2</label></td><td colspan="2"  ><textarea type="text" name="ing02" rows="1" cols="90">'.$ing02.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 3</label></td><td colspan="2"  ><textarea type="text" name="ing03" rows="1" cols="90">'.$ing03.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 4</label></td><td colspan="2"  ><textarea type="text" name="ing04" rows="1" cols="90">'.$ing04.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 5</label></td><td colspan="2"  ><textarea type="text" name="ing05" rows="1" cols="90">'.$ing05.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 6</label></td><td colspan="2"  ><textarea type="text" name="ing06" rows="1" cols="90">'.$ing06.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 7</label></td><td colspan="2"  ><textarea type="text" name="ing07" rows="1" cols="90">'.$ing07.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 8</label></td><td colspan="2"  ><textarea type="text" name="ing08" rows="1" cols="90">'.$ing08.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 9</label></td><td colspan="2"  ><textarea type="text" name="ing09" rows="1" cols="90">'.$ing09.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 10</label></td><td colspan="2"  ><textarea type="text" name="ing10" rows="1" cols="90">'.$ing10.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 11</label></td><td colspan="2"  ><textarea type="text" name="ing11" rows="1" cols="90">'.$ing11.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 12</label></td><td colspan="2"  ><textarea type="text" name="ing12" rows="1" cols="90">'.$ing12.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 13</label></td><td colspan="2"  ><textarea type="text" name="ing13" rows="1" cols="90">'.$ing13.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 14</label></td><td colspan="2"  ><textarea type="text" name="ing14" rows="1" cols="90">'.$ing14.'</textarea></td>
</tr><tr>
	<td ><label>Ingredient 15</label></td><td colspan="2"  ><textarea type="text" name="ing15" rows="1" cols="90">'.$ing15.'</textarea></td>
</tr>';
echo'
<tr>
	<td colspan="2"><h3 class="displaytitle">Etapes </h3></td>
</tr><tr>
	<td><label>Etape 1</label></td><td colspan="2"><textarea type="text" name="etape01" rows="4" cols="90">'.$etape01.'</textarea></td>
</tr><tr>
	<td><label>Etape 2</label></td><td colspan="2"><textarea type="text" name="etape02" rows="4" cols="90">'.$etape02.'</textarea></td>
</tr><tr>
	<td><label>Etape 3</label></td><td colspan="2"><textarea type="text" name="etape03" rows="4" cols="90">'.$etape03.'</textarea></td>
</tr><tr>
	<td><label>Etape 4</label></td><td colspan="2"><textarea type="text" name="etape04" rows="4" cols="90">'.$etape04.'</textarea></td>
</tr><tr>
	<td><label>Etape 5</label></td><td colspan="2"><textarea type="text" name="etape05" rows="4" cols="90">'.$etape05.'</textarea></td>
</tr><tr>
	<td><label>Etape 6</label></td><td colspan="2"><textarea type="text" name="etape06" rows="4" cols="90">'.$etape06.'</textarea></td>
</tr><tr>
	<td><label>Etape 7</label></td><td colspan="2"><textarea type="text" name="etape07" rows="4" cols="90">'.$etape07.'</textarea></td>
</tr><tr>
	<td><label>Etape 8</label></td><td colspan="2"><textarea type="text" name="etape08" rows="4" cols="90">'.$etape08.'</textarea></td>
</tr><tr>
	<td><label>Etape 9</label></td><td colspan="2"><textarea type="text" name="etape09" rows="4" cols="90">'.$etape09.'</textarea></td>
</tr><tr>';
echo '
<tr>
	<td>  </td><td colspan="2"><input type="submit" value="Mettre à jour"></form></td>
</tr>	
</tbody></table>
</form><BR>	';
?>	
