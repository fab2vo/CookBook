
<html>
<head>
<title>Login</title>
</head>
<body style="background-color:MediumSeaGreen;">
<!-- This is a comment -->
<center><table style="width:100%">

<form action="http://82.66.37.73:8085/cb/login.php" method="post">
<tr><h3>
	<label for="familyl">Nom du livre de famille:</label><br>
	<input type="text" id="familyl" name="family" pattern="[A-Za-z]{5-25}" size="25" required autofocus><br>
</h3></tr>
<tr><h3>
	<label for="memberl">Prenom:</label><br>
	<input type="text" id="memberl" name="member" pattern="[A-Za-z]{5-25}" size="25" required autofocus><br>
</h3></tr>
<tr><h3>
	<label for="passwordl">Mot de passe:</label><br>
	<input type="password" id="pwd" name="password" pattern="[A-Za-z]{4-20}" size="25" required><br>
</h3></tr>
<tr><p><input type="submit" value="Login"></p></tr>
</form>
<?php 
if (isset($message))
echo '<tr><h4>'.$message.'</h4></tr>';
?>

</table></center>
</body>
</html>

