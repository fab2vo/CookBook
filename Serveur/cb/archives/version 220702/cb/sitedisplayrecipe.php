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
	</head>';
include ('idconnect.inc');
$cxn = mysqli_connect($host,$user,$password,$nombase,$port);
$id_user_session=$_SESSION['id_user'];
$idrecipe_req=$_POST['id_recipe'];
/* Download recipe */
if (!$cxn) {echo 'Failed';exit;}
$sql="SELECT * FROM recipedb WHERE id_recipe='$idrecipe_req'"; 
mysqli_query($cxn,'SET NAMES UTF8');
$result = mysqli_query($cxn,$sql);
if (!$result) {echo 'Failed 2';exit;}
$row_count=$result->num_rows;
if ($row_count!=1) {echo 'Failed 3';exit;}
$ligne=mysqli_fetch_assoc($result);
extract($ligne);
$src='data:image/png;base64,'.$photo;
echo'<center>
	<body style="background-color:MediumSeaGreen;">
	<h2 style="color:Orange";>'.$title.'</h2></center>';
echo '<table>
    <tbody>
        <tr><td rowspan="6" width="40%"><img src="'.$src.' " width="200"/></td>
            <td> Nombre de personnes : '.$nb_pers.'</td></tr>
		<tr><td width="60%"> Source : '.$source.'</td></tr>
        <tr><td> Source url :'.$source_url.'</td></tr>
		<tr><td> Saison :'.$season.'</td></tr>
		<tr><td> Diffciluté :'.$difficulty.'</td></tr>
		<tr><td> Type :'.$type.'</td></tr>
		<tr><td colspan="2"> Ingredient 01 : '.$ing01.'</td></tr>
		<tr><td colspan="2"> Ingredient 02 : '.$ing02.'</td></tr>
		<tr><td colspan="2"> Ingredient 03 : '.$ing03.'</td></tr>
		<tr><td colspan="2"> Ingredient 04 : '.$ing04.'</td></tr>
		<tr><td colspan="2"> Ingredient 05 : '.$ing05.'</td></tr>
		<tr><td colspan="2"> Etape 01 : '.$etape01.'</td></tr>
		<tr><td colspan="2"> Etape 02 : '.$etape02.'</td></tr>
		<tr><td colspan="2"> Etape 03 : '.$etape03.'</td></tr>
		<tr><td colspan="2"> Etape 04 : '.$etape04.'</td></tr>
		<tr><td colspan="2"> Etape 05 : '.$etape05.'</td></tr>
    </tbody>
</table>';


?>	
</body>
</html>