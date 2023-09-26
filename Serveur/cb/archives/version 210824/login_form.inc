<?php
/* File: login_form.inc
*/
?>

<html>
<head>
<title>Login</title>
</head>
<body style="background-color:MediumSeaGreen;">
<!-- This is a comment -->
<center><table style="width:100%">
<form action=”Login.php?do=login” method=”POST”>

<?php 
if (isset($message))
echo “<tr><td>$message </td></tr>”;
?>

<tr><h3>

	<label for="family">Nom de famille:</label><br>
	<input type="text" id="family" name="family" pattern="[A-Za-z]{20}" size="25" required autofocus><br>
</h3></tr>
<tr><h3>
	<label for="member">Prenom:</label><br>
	<input type="text" id="member" name="member" pattern="[A-Za-z]{20}" size="25" required autofocus><br>
</h3></tr>
<tr><h3>
	<label for="password">Mot de passe:</label><br>
	<input type="password" id="pwd" name="password" pattern="[A-Za-z]{20}" size="25" required><br>
</h3></tr>
<tr><p><input type="submit" value="Login"></p></tr>
</form>
</table></center>
</body>
</html>