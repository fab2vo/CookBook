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
$debug=1;
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
	$sql="SELECT *	FROM recipedb WHERE id_recipe='$id_recipe_req' AND id_user='$id_user_session'";
	debugtrace($sql);
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	debugtrace(var_export($result,true));
	if (!$result) {exit;} 
	if (mysqli_num_rows($result)==0) {debugtrace("Not found");exit;}
	$ligne=mysqli_fetch_assoc($result);
	extract($ligne);
} else { exit;}
echo'<table>
    <thead>
        <tr>
            <th colspan="2">Edition de la recette</th>
        </tr>
    </thead>';
echo'<form action="http://82.66.37.73:8085/cb/createnewrecipe.php" method="POST">
<tbody>
<tr><td>Titre</td><td><input type="text" value="'.$title.'" name="title" size="45"></td></tr>
<tr><td>Source</td><td><input type="text" value="'.$source.'" name="source" size="45"></td></tr>
<tr><td>URL source</td><td><input type="text" value="'.$source_url.'" name="sourceurl" size="45"></td></tr>
<tr><td>Nb personne</td><td><input type="text" value="'.$nb_pers.'" name="nbpers" size="5"></td></tr>
<tr><td>Etape 1</td><td><textarea type="text" name="etape01" rows="4" cols="100">'.$etape01.'</textarea></td></tr>
<tr><td>  </td><td><input type="submit" value="Envoyez"></td></tr>	
</tbody>
</form>';


// FUNCTION
 function displayrow($li)
{
	extract($li);
	$serverip="";
	echo'<TR><TD>'.$title.'</TD>';
	echo '<TD><form action="'.$serverip.'siteeditrecipe.php" method="post">';
	echo '<input type="hidden" name="id_recipe" value="'.$id_recipe.'">
	<input type="submit" value="Edit"></TD>';
	echo '<TD><form action="'.$serverip.'sitedisplayrecipe.php" method="post">';
	echo '<input type="hidden" name="id_recipe" value="'.$id_recipe.'">
	<input type="submit" value="Display"></TD>';
	echo '</TR>';
 return;
}
?>	
</table>
</body>
</html>