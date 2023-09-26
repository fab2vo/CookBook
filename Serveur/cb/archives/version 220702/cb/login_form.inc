
<html>
<head>
<title>Login</title>
<link href="sitestyle.css" rel="stylesheet" type="text/css" >
</head>
<body >
<!-- This is a comment -->
<center><table style="width:100%">

<form action="http://cookbookfamily.000webhostapp.com/cb/login.php" method="post">
<tr><h3>
	<label class="logtitle" for="familyl">Nom du livre de famille:</label><br>
	<input type="text" id="familyl" name="family" pattern="[\_\s \-\?\!\(\)A-Za-z ]{8,45}" size="45" required autofocus><br>
</h3></tr>
<tr><h3>
	<label class="logtitle" for="memberl">Prenom:</label><br>
	<input type="text" id="memberl" name="member" pattern="[\_\-A-Za-z]{1,25}" size="25" required autofocus><br>
</h3></tr>
<tr><h3>
	<label class="logtitle" for="passwordl">Mot de passe:</label><br>
	<input type="password" id="pwd" name="password" pattern="[\_\-\?\!\(\)A-Za-z]{3,25}" size="25" required><br>
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

