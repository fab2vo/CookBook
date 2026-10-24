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
	<tr class="headtable">
		<td class="headtablecol1">
			<img src="./image/cookbook_fg.svg" width="56" alt="logo">
		</td>
		<td class="headtablecol5">
			<h3 class="headtablecol5"><?php echo htmlspecialchars(($_SESSION['member'] ?? '') . "@" . ($_SESSION['family'] ?? ''), ENT_QUOTES, 'UTF-8'); ?></h3>
		</td>
		<td><p> </p></td>
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
<td colspan="4">
