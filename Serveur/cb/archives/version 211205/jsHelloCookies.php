<html>
  <head>
    <title>let us see cookies</title>
  </head>

  <body id="monBody"></body>
<?php 
if (!isset($_POST['family'])){
	?>
	<script type="text/javascript" >
		var monH1=document.createElement("h1");
		monH1.innerHTML=" Display input form";
		document.getElementById("monBody").appendChild(monH1);
	</script>
	<form action="http://82.66.37.73:8085/cb/jsHelloCookies.php" method="post">
	<h3><label class="logtitle" for="familyl">Nom du livre de famille:</label><br>
	<input type="text" id="familyl" name="family" pattern="[\_\s \-\?\!\(\)A-Za-z ]{8,45}" size="45" required autofocus><br>
	</h3><p><input type="submit" value="Login"></p></tr></form>
	<?php 
	exit;
}	
echo "<h1>".$_POST['family']."</h1>";
echo "Affichage de deux colonnes pour liste et visu";
?>
 

</html>


