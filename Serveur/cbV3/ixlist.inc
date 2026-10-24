<?php
/* ixlist.inc */
if (!defined('IN_CB')) {
    define('IN_CB', true);
}

$sql = "SELECT recipedb.pknum, cookbooks.id_recipe, title, recipedb.id_user AS id_owner, status
        FROM cookbooks
        LEFT JOIN recipedb ON cookbooks.id_recipe = recipedb.id_recipe
        WHERE cookbooks.id_user = ? AND status = 'Visible'";

$rows1 = db_prepared_select_all($cxn, $sql, "s", array($id_user));

echo '<table><tr><th class="listheader">Recette</th><th class="listheader">Edit</th><th class="listheader">Display</th></tr>';

if ($rows1) {
    foreach ($rows1 as $ligne) {
        displayrow($ligne);
    }
}

$sql2 = "SELECT recipedb.pknum, cookbooks.id_recipe, title, recipedb.id_user AS id_owner, status
         FROM cookbooks
         LEFT JOIN recipedb ON cookbooks.id_recipe = recipedb.id_recipe
         WHERE cookbooks.id_user = ? AND status = 'Submitted'";

$rows2 = db_prepared_select_all($cxn, $sql2, "s", array($id_user));
if ($rows2) {
    foreach ($rows2 as $ligne) {
        displayrow($ligne);
    }
}
echo '</table>';
echo '<p>Apres modification, sortir et recharger le cookbook dans l\'application pour mettre a jour</p>';

function displayrow($li) {
    extract($li);
    global $id_user_session;
    echo '<tr class="celllist"><td>' . htmlspecialchars($title, ENT_QUOTES, 'UTF-8') . '</td>';
    if ($status == 'Visible') {
        if ($id_owner == $id_user_session) {
            echo '<td class="cellist"><center><form action="ix.php" method="get">';
            echo '<input type="hidden" name="recipenum" value="' . $pknum . '">
                  <input type="hidden" name="edit" value="yes">
                  <input type="submit" value="Edit"></form></center></td>';
        } else {
            echo '<td></td>';
        }
        echo '<td><center><form action="ix.php" method="get">';
        echo '<input type="hidden" name="recipenum" value="' . $pknum . '">
              <input type="submit" value="Display"></form></center></td>';
        echo '</tr>';
    } else {
        $mess = "Dans boîte mail";
        echo '<td colspan="2"><p>' . $mess . '</p></td>';
    }
    return;
}
?>
