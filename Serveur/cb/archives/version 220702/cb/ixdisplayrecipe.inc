<?php
?>	
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<?php
/* Download recipe */
$sql="SELECT * FROM recipedb WHERE pknum='$recipenum'"; 
mysqli_query($cxn,'SET NAMES UTF8');
$result = mysqli_query($cxn,$sql);
if (!$result) {echo "Recipe ".$recipenum." not found";exit;}
$row_count=$result->num_rows;
if ($row_count!=1) {echo "Several recipe ".$recipenum;exit;}
$ligne=mysqli_fetch_assoc($result);
extract($ligne);
$src='data:image/png;base64,'.$photo;
echo'<center>
	<h2 class="displaytitle">'.$title.'</h2></center>';
echo '<table>
    <tbody>
        <tr><td rowspan="6" class="disimage"><img class="disimage" src="'.$src.' "/></td>
            <td class="disside"> <label>Nombre de personnes : </label>'.$nb_pers.'</td></tr>
		<tr><td class="disside"> <label> Source : </label>'.$source.'</td></tr>
        <tr><td class="disside"> <label> Source url : </label>'.$source_url.'</td></tr>
		<tr><td class="disside"> <label> Saison : </label>'.$season.'</td></tr>
		<tr><td class="disside"> <label> Difficulté : </label>'.$difficulty.'</td></tr>
		<tr><td class="disside"> <label> Type : </label>'.$type.'</td></tr>
		<tr><td colspan="2"><h3 class="displaytitle">Ingrédients </h3></td></tr>';
for ($i = 1; $i <= 15; $i++) {
	dising($i); }
echo '<tr><td colspan="2"><h3 class="displaytitle">Etapes </h3></td></tr>';
for ($i = 1; $i <= 9; $i++) {
	disetape($i); }
echo'</tbody></table>';

function dising($z){
	if ($z<10) $y="ing0".$z;
	else $y="ing".$z;
	global $$y;
	if ($$y!="")
		echo '<tr><td colspan="2" class="dising"><label> Ingredient '.$z.' : </label>'.$$y.'</td></tr>';
}
function disetape($z){
	if ($z<10) $y="etape0".$z;
	else $y="etape".$z;
	global $$y;
	if ($$y!="")
		echo '<tr><td colspan="2" class="disetape"><label> Etape '.$z.' : </label>'.$$y.'</td></tr>';
}
?>	