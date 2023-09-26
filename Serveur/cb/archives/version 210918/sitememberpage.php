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
	$sql="SELECT cookbooks.id_recipe, title, recipedb.id_user AS id_owner,status
			FROM cookbooks 
			LEFT JOIN recipedb ON cookbooks.id_recipe=recipedb.id_recipe			
			WHERE cookbooks.id_user='$id_user_session'";
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) {exit;} 
} else {}
echo'<table style="color:white;" ><th style="width:250px">Recette</th>
    <th style="width:75px">Edit</th><th style="width:75px">Display</th>';
while ($ligne=mysqli_fetch_assoc($result)){
	displayrow($ligne);
}
echo'</table>';

 function displayrow($li)
{
	extract($li);
	global $serverip;
	global $id_user_session;
	echo'<TR><TD>'.$title.'</TD>';
	if ($status=='Visible') {
		if ($id_owner==$id_user_session){
			echo '<TD><form action="'.$serverip.'siteeditrecipe.php" method="post">';
			echo '<input type="hidden" name="id_recipe" value="'.$id_recipe.'">
			<input type="submit" value="Edit"></form></TD>';
		} else {
			echo '<TD></TD>';
		}
		echo '<TD><form action="'.$serverip.'sitedisplayrecipe.php" method="post">';
		echo '<input type="hidden" name="id_recipe" value="'.$id_recipe.'">
		<input type="submit" value="Display"></form></TD>';
		echo '</TR>';
	} else {
		$mess="Dans boîte mail";
		echo '<TD COLSPAN="2" > <p >'.$mess.'</p></TD>';
	}
 return;
}
?>	
</table>
</body>
</html>