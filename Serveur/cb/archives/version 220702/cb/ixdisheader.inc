<html>
<head>
<title>CookBook Family</title>
<link href="ixsitestyle.css" rel="stylesheet" type="text/css" >
</head>
<table>
<tr id="header"><!-- Header -->
<td>
	<table class="headtable">
	<tr class="headtable">
		<td class="headtablecol1">
			<img src="./image/cookbook_fg.svg" width="56">
		</td>
		<td class="headtablecol5"> 
			<h3 class="headtablecol5"> <?php echo $_SESSION['member']."@".$_SESSION['family'];?></h3>
		</td>
		<td > 
			<p> </p>
		</td>
		<td class="headtablecol6"> 
			<form action="ix.php" method="GET">
			<input type="hidden" id="logoutid" value="logout" name="status">
			<input type="submit" value="Logout"></form>
		</td>
	</tr>
	</table>
</td> 
</tr>
<tr id="Main">
<td colspan=4>

<!--
	<input type="submit" value="Login"></form>
	<br>
</td>
</tr>
</table>
<body >
</html-->

