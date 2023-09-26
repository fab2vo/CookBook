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
$cxn = mysqli_connect($host,$user,$password,$nombase,$port);
$id_user_session=$_SESSION['id_user'];
if ($cxn){
	$sql="SELECT cookbooks.id_recipe, title
			FROM cookbooks 
			LEFT JOIN recipedb ON cookbooks.id_recipe=recipedb.id_recipe			
			WHERE cookbooks.id_user='$id_user_session'";
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) {exit;} 
} else {}
echo'<table style="color:white;" ><th style="width:250px">Recette</th>
    <th style="width:50px">Edit</th><th style="width:50px">Display</th>';
while ($ligne=mysqli_fetch_assoc($result)){
	displayrow($ligne);
}
echo'</table>';

 function displayrow($li)
{
	extract($li);
	global $serverip;
	echo'<TR><TD>'.$title.'</TD>';
	echo '<TD><form action=">'.$serverip.'siteeditrecipe.php" method="post">';
	echo '<input type="hidden" name="id_recipe" value="'.$id_recipe.'">
	<input type="submit" value="Edit"></TD>';
	echo '<TD><form action=">'.$serverip.'sitedisplayrecipe.php" method="post">';
	echo '<input type="hidden" name="id_recipe" value="'.$id_recipe.'">
	<input type="submit" value="Display"></TD>';
	echo '</TR>';
 return;
}
?>	
</table>
</body>
</html>