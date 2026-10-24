<?php
/* Program: clearnet.php
 * Nettoyage et supervision BdD complète
 */

session_start();
include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

if (!$cxn = db_connect($host, $user, $password, $nombase, $port)) {
    exitfail("Database connection error");
}

$iduser_req = $_POST['keyuser'] ?? $_POST['iduser'] ?? '';
if ($iduser_req !== "FDx" && (!isset($_SESSION['auth']) || $_SESSION['auth'] !== 'yes')) {
    exitfail("Not admin");
}

echo "<h2>Supervision de la base de données CookBook</h2>";

/* A. Recettes */
$sql = "SELECT r.pknum, r.id_recipe, r.title, x.recipecount, r.id_user
        FROM recipedb AS r
        LEFT JOIN (SELECT id_recipe, COUNT(id_user) AS recipecount FROM cookbooks GROUP BY id_recipe) AS x
            ON x.id_recipe = r.id_recipe";
$result = mysqli_query($cxn, $sql);

echo "<h3>A. Recipe list :</h3>";
if ($result && $result->num_rows > 0) {
    echo "<table border='1'><thead><tr><td>Recipe Id</td><td>pknum</td><td>Nb users</td><td>Title and Owner</td></tr></thead><tbody>";
    while ($ligne = mysqli_fetch_assoc($result)) {
        extract($ligne);
        $author = "No author";
        $sql2 = "SELECT pknum, name, family FROM userdb WHERE id_user = ?";
        $stmt2 = mysqli_prepare($cxn, $sql2);
        if ($stmt2) {
            mysqli_stmt_bind_param($stmt2, "s", $id_user);
            mysqli_stmt_execute($stmt2);
            $res2 = mysqli_stmt_get_result($stmt2);
            if ($res2 && mysqli_num_rows($res2) == 1) {
                $ligne2 = mysqli_fetch_assoc($res2);
                $author = $ligne2['name'] . "@" . $ligne2['family'];
            }
            mysqli_stmt_close($stmt2);
        }

        $sql3 = "SELECT id_recipe FROM cookbooks WHERE id_user = ? AND id_recipe = ?";
        $stmt3 = mysqli_prepare($cxn, $sql3);
        $atz = " (Not in owner cookbook)";
        if ($stmt3) {
            mysqli_stmt_bind_param($stmt3, "ss", $id_user, $id_recipe);
            mysqli_stmt_execute($stmt3);
            $res3 = mysqli_stmt_get_result($stmt3);
            if ($res3 && mysqli_num_rows($res3) == 1) {
                $atz = "";
            }
            mysqli_stmt_close($stmt3);
        }

        $att = ($recipecount == 0) ? "Orphan" : "";
        echo "<tr><td>" . htmlspecialchars($id_recipe) . "</td><td> " . $pknum . " </td><td> " . $recipecount . " </td><td> " . htmlspecialchars($title) . " --> " . htmlspecialchars($author) . " " . $att . " " . $atz . "</td></tr>";
    }
    echo "</tbody></table><BR>";
} else {
    echo "<p>Aucune recette trouvée.</p>";
}

/* B. Utilisateurs */
$sql = "SELECT u.pknum, u.name, u.family, u.id_user, x.recipecount FROM userdb u
        LEFT JOIN (SELECT id_user, COUNT(id_recipe) AS recipecount FROM cookbooks GROUP BY id_user) as x ON u.id_user = x.id_user
        ORDER BY u.pknum";
$result = mysqli_query($cxn, $sql);

echo "<h3>B. User list </h3>";
if ($result && $result->num_rows > 0) {
    echo "<table border='1'><thead><tr><td>User Id</td><td>pknum</td><td>Owner</td><td>Nb recipe</td><td>Status</td></tr></thead><tbody>";
    while ($ligne = mysqli_fetch_assoc($result)) {
        extract($ligne);
        $att = ($recipecount == 0) ? "STERILE" : "";
        echo "<tr><td>" . htmlspecialchars($id_user) . "</td><td> " . $pknum . " </td><td>" . htmlspecialchars($name) . "@" . htmlspecialchars($family) . " : </td><td> " . $recipecount . "</td><td> " . $att . "</td></tr>";
    }
    echo "</tbody></table>";
} else {
    echo "<p>Aucun utilisateur trouvé.</p>";
}

echo "<h3>C. Cookbook </h3>";

/* C.1 Utilisateurs orphelins dans cookbooks */
$sql = "SELECT id_user, c.pknum FROM cookbooks c LEFT JOIN userdb USING (id_user) WHERE userdb.id_user IS NULL";
echo "<b>C.1 Détection des utilisateurs orphelins dans cookbooks:</b><BR>";
$result = mysqli_query($cxn, $sql);
if ($result && $result->num_rows > 0) {
    while ($ligne = mysqli_fetch_assoc($result)) {
        echo htmlspecialchars($ligne['id_user']) . " pknum=" . $ligne['pknum'] . " <BR>";
    }
} else {
    echo "Pas d'utilisateur orphelin ds cookbooks<BR>";
}

/* C.2 Recettes orphelines dans cookbooks */
$sql = "SELECT id_recipe, c.pknum FROM cookbooks c LEFT JOIN recipedb USING (id_recipe) WHERE recipedb.id_recipe IS NULL";
echo "<BR><b>C.2 Détection des recettes orphelines dans cookbooks:</b><BR>";
$result = mysqli_query($cxn, $sql);
if ($result && $result->num_rows > 0) {
    while ($ligne = mysqli_fetch_assoc($result)) {
        echo htmlspecialchars($ligne['id_recipe']) . " pknum=" . $ligne['pknum'] . " <BR>";
    }
} else {
    echo "Pas de recette orpheline ds cookbooks<BR>";
}

/* C.3 Recettes sans etapes */
$sql = "SELECT pknum, title, id_recipe FROM recipedb WHERE etape01 = '' OR etape01 IS NULL";
echo "<BR><b>C.3 Détection des recettes sans description (étape 1 vide):</b><BR>";
$result = mysqli_query($cxn, $sql);
if ($result && $result->num_rows > 0) {
    while ($ligne = mysqli_fetch_assoc($result)) {
        echo "pknum=" . $ligne['pknum'] . " title=" . htmlspecialchars($ligne['title']) . " (Non complète)<BR>";
    }
} else {
    echo "Toutes les recettes ont des étapes renseignées.<BR>";
}

/* C.4 Recettes sans ingrédients */
$sql = "SELECT pknum, title, id_recipe FROM recipedb WHERE ing01 = '' OR ing01 IS NULL";
echo "<BR><b>C.4 Détection des recettes sans ingrédients (ingrédient 1 vide):</b><BR>";
$result = mysqli_query($cxn, $sql);
if ($result && $result->num_rows > 0) {
    while ($ligne = mysqli_fetch_assoc($result)) {
        echo "pknum=" . $ligne['pknum'] . " title=" . htmlspecialchars($ligne['title']) . " (Incomplète)<BR>";
    }
} else {
    echo "Toutes les recettes ont des ingrédients renseignés.<BR>";
}

/* C.5 Recettes avec photos trop grandes (> 380 000 octets) */
$sql = "SELECT pknum, title, id_recipe FROM recipedb WHERE LENGTH(photo) > 380000";
echo "<BR><b>C.5 Détection des recettes avec une photo trop grande (>380 KB):</b><BR>";
$result = mysqli_query($cxn, $sql);
if ($result && $result->num_rows > 0) {
    while ($ligne = mysqli_fetch_assoc($result)) {
        echo "pknum=" . $ligne['pknum'] . " title=" . htmlspecialchars($ligne['title']) . " (Photo trop grande)<BR>";
    }
} else {
    echo "Aucune photo volumineuse détectée.<BR>";
}

echo "<h3>D. Comments </h3>";

/* D.1 Doublons dans commentsdb */
$sql = "SELECT * FROM `commentsdb` ORDER BY id_recipe, comment";
$result = mysqli_query($cxn, $sql);
echo "<b>D.1 Doublons dans commentsdb :</b><BR>";
if ($result && $result->num_rows > 0) {
    $nextrecipe = "";
    $nextcomment = "";
    $doublon_count = 0;
    while ($ligne = mysqli_fetch_assoc($result)) {
        extract($ligne);
        if (($nextrecipe == $id_recipe) && ($nextcomment == $comment)) {
            echo htmlspecialchars($id_recipe) . " pknum=" . $pknum . " : " . htmlspecialchars($comment) . " (" . $date_comment . ")<BR>";
            $doublon_count++;
        }
        $nextrecipe = $id_recipe;
        $nextcomment = $comment;
    }
    if ($doublon_count == 0) {
        echo "Pas de doublons dans les commentaires.<BR>";
    }
} else {
    echo "Pas de commentaires enregistrés.<BR>";
}

/* D.2 Commentaires sans recette */
$sql = "SELECT n.id_user, n.pknum, n.id_recipe FROM commentsdb n LEFT JOIN recipedb r USING (id_recipe) WHERE r.id_recipe IS NULL";
echo "<BR><b>D.2 Détection des commentaires sans recette:</b><BR>";
$result = mysqli_query($cxn, $sql);
if ($result && $result->num_rows > 0) {
    while ($ligne = mysqli_fetch_assoc($result)) {
        echo htmlspecialchars($ligne['id_user']) . " pknum=" . $ligne['pknum'] . " id_recipe=" . htmlspecialchars($ligne['id_recipe']) . " <BR>";
    }
} else {
    echo "Pas de commentaire sans recette<BR>";
}

/* D.3 Commentaires sans utilisateur */
$sql = "SELECT n.id_user, n.pknum, n.id_recipe FROM commentsdb n LEFT JOIN userdb r USING (id_user) WHERE r.id_user IS NULL";
echo "<BR><b>D.3 Détection des commentaires sans utilisateur:</b><BR>";
$result = mysqli_query($cxn, $sql);
if ($result && $result->num_rows > 0) {
    while ($ligne = mysqli_fetch_assoc($result)) {
        echo htmlspecialchars($ligne['id_user']) . " pknum=" . $ligne['pknum'] . " id_recipe=" . htmlspecialchars($ligne['id_recipe']) . " <BR>";
    }
} else {
    echo "Pas de commentaire sans utilisateur<BR>";
}

echo "<h3>E. Notes </h3>";

/* E.1 Doublons dans notesdb */
$sql = "SELECT * FROM `notesdb` ORDER BY id_recipe, date_note, id_user";
$result = mysqli_query($cxn, $sql);
echo "<b>E.1 Doublons dans notesdb :</b><BR>";
if ($result && $result->num_rows > 0) {
    $nextrecipe = "";
    $nextnote = "";
    $nextiduser = "";
    $nextdate = "";
    $doublon_notes_count = 0;
    while ($ligne = mysqli_fetch_assoc($result)) {
        extract($ligne);
        if (($nextrecipe == $id_recipe) && ($nextnote == $note) && ($nextdate == $date_note) && ($nextiduser == $id_user)) {
            echo "Recette " . htmlspecialchars($id_recipe) . " (pknum=" . $pknum . ") note " . $note . " le " . $date_note . " par " . htmlspecialchars($id_user) . "<BR>";
            $doublon_notes_count++;
        }
        $nextrecipe = $id_recipe;
        $nextnote = $note;
        $nextdate = $date_note;
        $nextiduser = $id_user;
    }
    if ($doublon_notes_count == 0) {
        echo "Pas de doublons dans les notes.<BR>";
    }
} else {
    echo "Pas de notes enregistrées.<BR>";
}

/* E.2 Notes sans recette */
$sql = "SELECT n.id_user, n.pknum, n.id_recipe FROM notesdb n LEFT JOIN recipedb r USING (id_recipe) WHERE r.id_recipe IS NULL";
echo "<BR><b>E.2 Détection des notes sans recette:</b><BR>";
$result = mysqli_query($cxn, $sql);
if ($result && $result->num_rows > 0) {
    while ($ligne = mysqli_fetch_assoc($result)) {
        echo htmlspecialchars($ligne['id_user']) . " pknum=" . $ligne['pknum'] . " id_recipe=" . htmlspecialchars($ligne['id_recipe']) . " <BR>";
    }
} else {
    echo "Pas de note sans recette<BR>";
}

/* E.3 Notes sans utilisateur */
$sql = "SELECT n.id_user, n.pknum, n.id_recipe FROM notesdb n LEFT JOIN userdb r USING (id_user) WHERE r.id_user IS NULL";
echo "<BR><b>E.3 Détection des notes sans utilisateur:</b><BR>";
$result = mysqli_query($cxn, $sql);
if ($result && $result->num_rows > 0) {
    while ($ligne = mysqli_fetch_assoc($result)) {
        echo htmlspecialchars($ligne['id_user']) . " pknum=" . $ligne['pknum'] . " id_recipe=" . htmlspecialchars($ligne['id_recipe']) . " <BR>";
    }
} else {
    echo "Pas de note sans utilisateur<BR>";
}

mysqli_close($cxn);
?>
