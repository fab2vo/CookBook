<?php
/* superindexGhW78k.php
 * Protected Administrative Dashboard requiring key "FDx"
 */
session_start();

if (isset($_GET['logout'])) {
    unset($_SESSION['admin_auth']);
    header("Location: superindexGhW78k.php");
    exit;
}

if (isset($_POST['keyuser'])) {
    if ($_POST['keyuser'] === "FDx") {
        $_SESSION['admin_auth'] = "yes";
    } else {
        $error = "Clé d'accès incorrecte";
    }
}

if (!isset($_SESSION['admin_auth']) || $_SESSION['admin_auth'] !== "yes") {
    ?>
    <!DOCTYPE html>
    <html lang="fr">
    <head>
        <meta charset="UTF-8">
        <title>Accès Administrateur CookBook</title>
        <style>
            body { background-color: #2e8b57; color: white; font-family: sans-serif; text-align: center; padding-top: 50px; }
            .login-box { background: rgba(0,0,0,0.3); display: inline-block; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.3); }
            input[type="password"] { padding: 8px; font-size: 16px; border-radius: 4px; border: 1px solid #ccc; margin-bottom: 10px; }
            input[type="submit"] { padding: 8px 16px; font-size: 16px; border-radius: 4px; border: none; background-color: orange; color: white; cursor: pointer; font-weight: bold; }
            .error { color: #ff6b6b; font-weight: bold; }
        </style>
    </head>
    <body>
        <div class="login-box">
            <h2>Accès Administrateur SuperIndex</h2>
            <?php if (isset($error)) echo '<p class="error">' . htmlspecialchars($error, ENT_QUOTES, 'UTF-8') . '</p>'; ?>
            <form action="superindexGhW78k.php" method="POST">
                <p><label for="keyuser">Entrez la clé d'accès admin :</label></p>
                <p><input type="password" id="keyuser" name="keyuser" required autofocus></p>
                <p><input type="submit" value="Valider"></p>
            </form>
        </div>
    </body>
    </html>
    <?php
    exit;
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>SuperIndex CookBook</title>
</head>
<body style="background-color:MediumSeaGreen; font-family: sans-serif; color: white;">
<div style="float: right; padding: 10px;">
    <a href="superindexGhW78k.php?logout=1" style="color: yellow; font-weight: bold; text-decoration: none; background: rgba(0,0,0,0.2); padding: 8px 12px; border-radius: 4px;">Déconnexion Admin</a>
</div>
<h1>Operations on cookbook database</h1>

<table style="width:100%; color: white;">
<tr>
<td style="vertical-align: top;">
<ul>
  <p><b>Divers</b></p>
  <li><a href="moverecipetocbGhW78k.html" style="color: white;">Move recipe to a cookbook</a></li>
  <li><a href="createnewrecipeGhW78k.html" style="color: white;">Create a recipe</a></li>
  <li><a href="addnoteGhW78k.html" style="color: white;">Add a rating to a recipe </a></li>
  <li><a href="addcommentGhW78k.html" style="color: white;">Add a comment to a recipe </a></li>
  <li><a href="getstampsGhW78k.html" style="color: white;">Get Stamps (JSON) </a></li>
  <li><a href="uploadphotointorecipefromdiskGhW78k.html" style="color: white;">Add a photo from disk to a recipe </a></li>
  <li><a href="sendrecipetosbGhW78k.html" style="color: white;">Send a recipe to somebody </a></li>
  <p><b>Pour Activité Splash</b></p>
  <li><a href="createnewfamilyGhW78k.html" style="color: white;"> New family </a></li>
  <li><a href="createnewmemberGhW78k.html" style="color: white;"> New member </a></li>
  <li><a href="getfamilycompositionGhW78k.html" style="color: white;">getfamilycomposition </a></li>
  <li><a href="getcookbookGhW78k.html" style="color: white;">getcookbook (PWD proof) </a></li>
  <li><a href="getnotesGhW78k.html" style="color: white;">getnotes(PWD proof)</a></li>
  <li><a href="getcommentsGhW78k.html" style="color: white;">getcomments (PWD proof)</a></li>
</ul>
</td>
<td style="vertical-align: top;">
<ul>
  <p><b>Pour activite AsyncSync UP</b></p>
  <li><a href="updateorcreaterecipeGhW78k.html" style="color: white;">updateorcreaterecipe if does not exist (PWD proof) </a></li>
  <li><a href="addnotewithdateGhW78k.html" style="color: white;">addnotewithdate - Add a rating to a recipe (PWD proof) </a></li>
  <li><a href="addcommentwithdateGhW78k.html" style="color: white;">addcommentwithdate - Add a comment to a comment (PWD proof)</a></li>
  <li><a href="uploadphotointorecipeGhW78k.html" style="color: white;">uploadphotointorecipe - Upload image string blob (PWD proof) </a></li>
  <li><a href="deleterecipeGhW78k.html" style="color: white;">deleterecipe - Remove recipe from cookbook (PWD proof)</a></li>
  <li><a href="getcommentsofrecipeGhW78k.html" style="color: white;">Get comments of a recipe (PWD proof)</a></li>
  <li><a href="getnotesofrecipeGhW78k.html" style="color: white;">Get notes of a recipe (PWD proof)</a></li>
  <li><a href="getrecipefromCBGhW78k.html" style="color: white;">Get a recipe wo/without photo (PWD proof)</a></li>
  <li><a href="getrecipefromCBwithphotoGhW78k.html" style="color: white;">Get a recipe with photo - deprecated</a></li>
  <li><a href="getrecipestampstiersGhW78k.html" style="color: white;">Get stamps from tiers in cookbook (PWD proof)</a></li>
  <li><a href="acceptrecipeGhW78k.html" style="color: white;">Accept recipe in a cookbook (PWD proof)</a></li>
  <li><a href="checkrequestsGhW78k.html" style="color: white;">Check requests (1 if yes) (PWD proof)</a></li>
  <p><b>Autre activité</b></p>
  <li><a href="sendmailGhW78k.html" style="color: white;">Send a recipe to member@family DISPLAY(PWD proof) </a></li>
  <li><a href="newrequestGhW78k.html" style="color: white;">Send a request for a recipe COMMUNITY (PWD proof) </a></li>
  <li><a href="getcommunitynewsGhW78k.html" style="color: white;">Get news from community COMMUNITY (PWD proof) </a></li>
  <li><a href="updatestatusrequestGhW78k.html" style="color: white;">update request status - deprecated</a></li>
  <li><a href="acceptorrefuserequestGhW78k.html" style="color: white;">accept or deny request MAIL (PWD proof)</a></li>
  <li><a href="getrequestsGhW78k.html" style="color: white;">Get requests (JSON) MAIL </a></li>
</ul>
</td>
<td style="vertical-align: top;">
<ul>
  <li><a href="clearnetGhW78k.html" style="color: yellow; font-weight: bold;">Supervision</a></li>
</ul>
</td>
</tr>
</table>
</body>
</html>
