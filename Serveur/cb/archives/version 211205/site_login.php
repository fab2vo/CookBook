<!DOCTYPE html>
<html>
<head>
<title>Login</title>
</head>
<body style="background-color:MediumSeaGreen;">
<!-- This is a comment -->
<center><table style="width:100%">
<form action="login.php" method="post">
<tr><p>
	<label for="family">Nom de famille:</label><br>
	<input type="text" id="family" name="family" pattern="[A-Za-z]{20}" size="25" required autofocus><br>
</p></tr>
<tr><p>
	<label for="passwd">Mot de passe:</label><br>
	<input type="password" id="pwd" name="passwd" pattern="[A-Za-z]{20}" size="15" required><br>
</p></tr>
<tr><p><input type="submit" value="Login"></p></tr>
</form>
</table></center>
</body>
</html>