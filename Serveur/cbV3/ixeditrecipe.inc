<?php
/* ixeditrecipe.inc - Edit recipe (PHP 8.1 compatible) */
if (!defined('IN_CB')) {
    define('IN_CB', true);
}

$sql = "SELECT id_recipe, id_user AS id_user_recipe, title, source, source_url, nb_pers,
        etape01, etape02, etape03, etape04, etape05, etape06, etape07, etape08, etape09,
        season, difficulty, type,
        ing01, ing02, ing03, ing04, ing05, ing06, ing07, ing08, ing09, ing10, ing11, ing12, ing13, ing14, ing15, photo
        FROM recipedb WHERE pknum = ?";

$rows = db_prepared_select_all($cxn, $sql, "i", array($recipenum));
if (!$rows || count($rows) !== 1) {
    echo "Recipe " . htmlspecialchars($recipenum, ENT_QUOTES, 'UTF-8') . " not found";
    exit;
}

$ligne = $rows[0];
extract($ligne);

date_default_timezone_set('Europe/Paris');
$date = date("Y-m-d H:i:s");
$src = 'data:image/png;base64,' . $photo;

echo '<center><h2 class="displaytitle">' . htmlspecialchars($title, ENT_QUOTES, 'UTF-8') . '</h2></center>
<table><tbody>
<tr>
	<td rowspan="8" class="disimage">
		<form action="ixuploadphotointorecipefromdisk.php" method="POST" enctype="multipart/form-data">
		<input type="hidden" value="' . htmlspecialchars($id_recipe, ENT_QUOTES, 'UTF-8') . '" name="idrecipe">
		<input type="hidden" value="' . htmlspecialchars($recipenum, ENT_QUOTES, 'UTF-8') . '" name="pknum">
		<label><input type="file" name="file" accept="image/jpeg"></label>
		<input type="submit" value="Upload photo"></form>
		<img class="disimage" src="' . $src . '" alt="photo"/>
	</td>
		<form action="ixupdateorcreaterecipe.php" method="POST">
		<input type="hidden" value="' . htmlspecialchars($id_recipe, ENT_QUOTES, 'UTF-8') . '" name="idrecipe">
		<input type="hidden" value="' . htmlspecialchars($recipenum, ENT_QUOTES, 'UTF-8') . '" name="pknum">
		<input type="hidden" value="' . htmlspecialchars($id_user_recipe, ENT_QUOTES, 'UTF-8') . '" name="iduser">
		<input type="hidden" value="' . htmlspecialchars($date, ENT_QUOTES, 'UTF-8') . '" name="date">
	<td class="editlbl"><label></label></td><td><br></td>
</tr><tr>
	<td class="editlbl"><label> Titre </label></td><td><input type="text" value="' . htmlspecialchars($title, ENT_QUOTES, 'UTF-8') . '" name="title" size="45"></td>
</tr><tr>
	<td class="editlbl"><label> Source</label></td><td><input type="text" value="' . htmlspecialchars($source, ENT_QUOTES, 'UTF-8') . '" name="source" size="45"></td>
</tr><tr>
	<td class="editlbl"><label> URL source</label></td><td><input type="text" value="' . htmlspecialchars($source_url, ENT_QUOTES, 'UTF-8') . '" name="sourceurl" size="45"></td>
</tr><tr>
	<td class="editlbl"><label> Nb personne</label></td><td><input type="text" value="' . htmlspecialchars($nb_pers, ENT_QUOTES, 'UTF-8') . '" name="nbpers" size="5"></td>
</tr><tr>
	<td class="editlbl"><label> Season </label></td><td><input list="seasonlist" value="' . htmlspecialchars($season, ENT_QUOTES, 'UTF-8') . '" name="season" size="45"></td>
</tr>
<datalist id="seasonlist">
  <option value="WINTER">
  <option value="SUMMER">
  <option value="ALLYEAR">
</datalist><tr>
	<td class="editlbl"><label> Difficulty</label> </td><td><input list="difficultylist" value="' . htmlspecialchars($difficulty, ENT_QUOTES, 'UTF-8') . '" name="difficulty" size="45"></td>
</tr><datalist id="difficultylist">
  <option value="QUICK">
  <option value="EASY">
  <option value="ELABORATE">
  <option value="SOPHISTICATED">
  <option value="UNDEFINED">
</datalist><tr>
	<td class="editlbl"><label>Type </label></td><td><input list="typelist" value="' . htmlspecialchars($type, ENT_QUOTES, 'UTF-8') . '" name="type" size="45"></td>
</tr><datalist id="typelist">
  <option value="APERITIF">
  <option value="STARTER">
  <option value="MAIN">
  <option value="DESSERT">
  <option value="SIDE">
  <option value="OTHER">
</datalist></tbody></table>';

echo '<table><tbody>
<tr>
	<td colspan="2"><h3 class="displaytitle">Ingrédients </h3></td>
</tr>';

for ($i = 1; $i <= 15; $i++) {
    $y = ($i < 10) ? "ing0" . $i : "ing" . $i;
    $val = $ligne[$y] ?? '';
    echo '<tr><td><label>Ingredient ' . $i . '</label></td><td colspan="2"><textarea name="' . $y . '" rows="1" cols="90">' . htmlspecialchars($val, ENT_QUOTES, 'UTF-8') . '</textarea></td></tr>';
}

echo '<tr><td colspan="2"><h3 class="displaytitle">Etapes </h3></td></tr>';

for ($i = 1; $i <= 9; $i++) {
    $y = ($i < 10) ? "etape0" . $i : "etape" . $i;
    $val = $ligne[$y] ?? '';
    echo '<tr><td><label>Etape ' . $i . '</label></td><td colspan="2"><textarea name="' . $y . '" rows="4" cols="90">' . htmlspecialchars($val, ENT_QUOTES, 'UTF-8') . '</textarea></td></tr>';
}

echo '<tr><td>  </td><td colspan="2"><input type="submit" value="Mettre à jour"></form></td></tr></tbody></table><br>';
?>
