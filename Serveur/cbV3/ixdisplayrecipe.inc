<?php
/* ixdisplayrecipe.inc - Display recipe details (PHP 8.1 compatible) */
if (!defined('IN_CB')) {
    define('IN_CB', true);
}

$rows = db_prepared_select_all($cxn, "SELECT * FROM recipedb WHERE pknum = ?", "i", array($recipenum));
if (!$rows || count($rows) !== 1) {
    echo "Recipe " . htmlspecialchars($recipenum, ENT_QUOTES, 'UTF-8') . " not found";
    exit;
}

$ligne = $rows[0];
extract($ligne);

$src = 'data:image/png;base64,' . $photo;
echo '<center><h2 class="displaytitle">' . htmlspecialchars($title, ENT_QUOTES, 'UTF-8') . '</h2></center>';
echo '<table>
    <tbody>
        <tr><td rowspan="6" class="disimage"><img class="disimage" src="' . $src . '" alt="photo"/></td>
            <td class="disside"> <label>Nombre de personnes : </label>' . htmlspecialchars($nb_pers, ENT_QUOTES, 'UTF-8') . '</td></tr>
		<tr><td class="disside"> <label> Source : </label>' . htmlspecialchars($source, ENT_QUOTES, 'UTF-8') . '</td></tr>
        <tr><td class="disside"> <label> Source url : </label>' . htmlspecialchars($source_url, ENT_QUOTES, 'UTF-8') . '</td></tr>
		<tr><td class="disside"> <label> Saison : </label>' . htmlspecialchars($season, ENT_QUOTES, 'UTF-8') . '</td></tr>
		<tr><td class="disside"> <label> Difficulté : </label>' . htmlspecialchars($difficulty, ENT_QUOTES, 'UTF-8') . '</td></tr>
		<tr><td class="disside"> <label> Type : </label>' . htmlspecialchars($type, ENT_QUOTES, 'UTF-8') . '</td></tr>
		<tr><td colspan="2"><h3 class="displaytitle">Ingrédients </h3></td></tr>';

for ($i = 1; $i <= 15; $i++) {
    dising_v2($i, $ligne);
}

echo '<tr><td colspan="2"><h3 class="displaytitle">Etapes </h3></td></tr>';

for ($i = 1; $i <= 9; $i++) {
    disetape_v2($i, $ligne);
}

echo '</tbody></table>';

function dising_v2($z, $data) {
    $y = ($z < 10) ? "ing0" . $z : "ing" . $z;
    if (!empty($data[$y])) {
        echo '<tr><td colspan="2" class="dising"><label> Ingredient ' . $z . ' : </label>' . htmlspecialchars($data[$y], ENT_QUOTES, 'UTF-8') . '</td></tr>';
    }
}

function disetape_v2($z, $data) {
    $y = ($z < 10) ? "etape0" . $z : "etape" . $z;
    if (!empty($data[$y])) {
        echo '<tr><td colspan="2" class="disetape"><label> Etape ' . $z . ' : </label>' . htmlspecialchars($data[$y], ENT_QUOTES, 'UTF-8') . '</td></tr>';
    }
}
?>
