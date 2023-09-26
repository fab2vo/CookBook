<?php
?>	
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<?php
/* Passe 1 Visible */
$sql="SELECT recipedb.pknum,cookbooks.id_recipe, title, recipedb.id_user AS id_owner,status
			FROM cookbooks 
			LEFT JOIN recipedb ON cookbooks.id_recipe=recipedb.id_recipe			
			WHERE cookbooks.id_user='$id_user' AND status='Visible'";
mysqli_query($cxn,'SET NAMES UTF8');
$result = mysqli_query($cxn,$sql);
if (!$result) {echo 'Failed 2';exit;}
echo'<table ><th class="listheader" >Recette</th>
    <th class="listheader">Edit</th><th class="listheader">Display</th>';
while ($ligne=mysqli_fetch_assoc($result)){
	displayrow($ligne);
}
/* Passe 2 Submitted */
$sql="SELECT recipedb.pknum,cookbooks.id_recipe, title, recipedb.id_user AS id_owner,status
			FROM cookbooks 
			LEFT JOIN recipedb ON cookbooks.id_recipe=recipedb.id_recipe			
			WHERE cookbooks.id_user='$id_user' AND status='Submitted'";
mysqli_query($cxn,'SET NAMES UTF8');
$result = mysqli_query($cxn,$sql);
if (!$result) exit; 
while ($ligne=mysqli_fetch_assoc($result)){
	displayrow($ligne);
}
echo'</table>';
echo '<p>Apres modification, sortir et recharger le cookbook dans l\'application pour mettre a jour</p>';

 function displayrow($li)
{
	extract($li);
	global $id_user_session;
	echo'<TR class="celllist"><TD>'.$title.'</TD>';
	if ($status=='Visible') {
		if ($id_owner==$id_user_session){
			echo '<TD class="cellist"><center><form action="ix.php" method="get">';
			echo '<input type="hidden" name="recipenum" value="'.$pknum.'">
			<input type="hidden" name="edit" value="yes">
			<input type="submit" value="Edit"></form><center></TD>';
		} else {
			echo '<TD></TD>';
		}
		echo '<TD><center><form action="ix.php" method="get">';
		echo '<input type="hidden" name="recipenum" value="'.$pknum.'">
			  <input type="submit" value="Display"></form></center></TD>';
		echo '</TR>';
	} else {
		$mess="Dans boîte mail";
		echo '<TD COLSPAN="2" > <p >'.$mess.'</p></TD>';
	}
 return;
}
?>	
