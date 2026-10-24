<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="utf-8">
<title>Login Deletion</title>
<link href="sitestyle.css" rel="stylesheet" type="text/css">
</head>
<body>
<center><table style="width:100%">
<form action="login_del.php" method="post">
<tr><td>
	<label class="logtitle" for="familyl">Nom du livre de famille:</label><br>
	<input type="text" id="familyl" name="family" size="45" required autofocus><br>
</td></tr>
<tr><td>
	<label class="logtitle" for="memberl">Prenom:</label><br>
	<input type="text" id="memberl" name="member" size="25" required><br>
</td></tr>
<tr><td>
	<label class="logtitle" for="passwordl">Mot de passe:</label><br>
	<input type="password" id="pwd" name="password" size="25" required><br>
</td></tr>
<tr><td><input type="submit" value="Login"></td></tr>
</form>
<?php
if (isset($message)) {
    echo '<tr><td><h4>' . htmlspecialchars($message, ENT_QUOTES, 'UTF-8') . '</h4></td></tr>';
}
?>
</table></center>
</body>
</html>
