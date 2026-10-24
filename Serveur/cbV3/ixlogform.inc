<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="utf-8">
<title>CookBook Family</title>
<link href="ixsitestyle.css" rel="stylesheet" type="text/css">
</head>
<body>
<table>
<tr id="header"><!-- Header -->
<td>
	<table class="headtable">
	<tr><form action="ix.php?status=login" method="post">
		<td class="headtablecol1">
			<img src="./image/cookbook_fg.svg" width="56" alt="logo">
		</td>
		<td class="headtablecol2" >
			<label class="logtitle" for="familyl">Nom du livre de famille:</label><br>
			<input type="text" id="familyl" name="family" size="45" required autofocus>
		</td>
		<td class="headtablecol3">
			<label class="logtitle" for="memberl">Prenom:</label><br>
			<input type="text" id="memberl" name="member" size="25" required>
		</td>
		<td class="headtablecol4">
			<label class="logtitle" for="passwordl">Mot de passe:</label><br>
			<input type="password" id="pwd" name="password" size="25" required>
		</td>
	</tr>
	</table>
</td>
</tr>
<tr id="Main">
<td colspan="4">
<center><br><br><br>
	<input type="submit" value="Login"></form>
	<br>
	<?php
	if (isset($message))
		echo '<p class="errormessage"><br>Erreur : '.htmlspecialchars($message, ENT_QUOTES, 'UTF-8').'</p>';
	?>
</center>
</td>
</tr>
</table>
</body>
</html>
