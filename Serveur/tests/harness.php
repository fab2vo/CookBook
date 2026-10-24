<?php
/* Test harness: runs one cbV3 endpoint against an in-memory fake of mysqli.
 * Usage: php harness.php <cbV3 dir> <endpoint.php> <json POST data> [json SESSION data]
 */
define('MYSQLI_REPORT_OFF', 0);
define('MYSQLI_ASSOC', 1);

const A1 = '11111111-1111-1111-1111-111111111111'; // family A
const A2 = '22222222-2222-2222-2222-222222222222'; // family A
const B1 = '33333333-3333-3333-3333-333333333333'; // family B
const C1 = '44444444-4444-4444-4444-444444444444'; // closed account, family A
const R1 = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'; // owner A1, in A1, A2, B1 cookbooks
const R2 = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'; // owner B1, in B1 cookbook only

$GLOBALS['FAKE'] = array(
    'users' => array(
        A1 => array('family' => 'FamA', 'name' => 'Alice', 'pass' => 'pwdA'),
        A2 => array('family' => 'FamA', 'name' => 'Arthur', 'pass' => 'pwdA'),
        B1 => array('family' => 'FamB', 'name' => 'Bob', 'pass' => 'pwdB'),
        C1 => array('family' => 'FamA', 'name' => 'Closed', 'pass' => 'compteferme'),
    ),
    'recipes' => array(R1 => A1, R2 => B1),
    'cookbooks' => array(array(A1, R1), array(A2, R1), array(B1, R1), array(B1, R2)),
    'requests' => array(1 => array('id_recipe' => R2, 'id_from' => A1, 'status' => 'ISSUED')),
    'writes' => array(),
);

class FakeStmt { public $sql; public $params = array(); public $rows = array(); public $affected = 0; }
class FakeResult { public $rows; public $i = 0; public $num_rows; function __construct($r) { $this->rows = $r; $this->num_rows = count($r); } }

function fake_select($sql, $p) {
    $F = $GLOBALS['FAKE'];
    $s = preg_replace('/\s+/', ' ', str_replace('`', '', $sql));
    $inCb = function ($u, $r) use ($F) { foreach ($F['cookbooks'] as $c) if ($c[0] === $u && $c[1] === $r) return true; return false; };
    if (preg_match('/^SELECT pass FROM userdb WHERE id_user = \?/', $s))
        return isset($F['users'][$p[0]]) ? array(array('pass' => $F['users'][$p[0]]['pass'])) : array();
    if (strpos($s, 'SELECT u.pass FROM cookbooks c') === 0) {
        $out = array(); foreach ($F['cookbooks'] as $c) if ($c[1] === $p[0]) $out[] = array('pass' => $F['users'][$c[0]]['pass']); return $out;
    }
    if (preg_match('/^SELECT (id_recipe, )?id_user FROM recipedb WHERE id_recipe = \?/', $s))
        return isset($F['recipes'][$p[0]]) ? array(array('id_recipe' => $p[0], 'id_user' => $F['recipes'][$p[0]])) : array();
    if (preg_match('/^SELECT id_recipe FROM recipedb WHERE id_recipe = \?/', $s))
        return isset($F['recipes'][$p[0]]) ? array(array('id_recipe' => $p[0])) : array();
    if (preg_match('/^SELECT (pknum|id_user) FROM cookbooks WHERE id_user = \? AND id_recipe = \?/', $s))
        return $inCb($p[0], $p[1]) ? array(array('pknum' => 1, 'id_user' => $p[0])) : array();
    if (preg_match('/^SELECT pknum FROM cookbooks WHERE id_recipe = \? AND id_user = \?/', $s))
        return $inCb($p[1], $p[0]) ? array(array('pknum' => 1)) : array();
    if (preg_match('/^SELECT id_user FROM userdb WHERE name = \? AND family = \?/', $s)) {
        foreach ($F['users'] as $id => $u) if ($u['name'] === $p[0] && $u['family'] === $p[1]) return array(array('id_user' => $id)); return array();
    }
    if (preg_match('/^SELECT id_user FROM userdb WHERE id_user = \?/', $s))
        return isset($F['users'][$p[0]]) ? array(array('id_user' => $p[0])) : array();
    if (preg_match('/^SELECT family FROM userdb WHERE id_user = \?/', $s))
        return isset($F['users'][$p[0]]) ? array(array('family' => $F['users'][$p[0]]['family'])) : array();
    if (preg_match('/^SELECT id_user FROM userdb WHERE family = \?/', $s)) {
        $out = array(); foreach ($F['users'] as $id => $u) if ($u['family'] === $p[0]) $out[] = array('id_user' => $id); return $out;
    }
    if (preg_match('/^SELECT id_recipe, id_from, status FROM requestdb WHERE pknum = \?/', $s))
        return isset($F['requests'][(int)$p[0]]) ? array($F['requests'][(int)$p[0]]) : array();
    if (preg_match('/^SELECT re.id_user FROM requestdb rq/', $s)) {
        $rq = $F['requests'][(int)$p[0]] ?? null; return $rq ? array(array('id_user' => $F['recipes'][$rq['id_recipe']])) : array();
    }
    if (preg_match('/^SELECT pass FROM userdb WHERE family = \?/', $s)) {
        $out = array(); foreach ($F['users'] as $u) if ($u['family'] === $p[0]) $out[] = array('pass' => $u['pass']); return $out;
    }
    if (preg_match('/^SELECT id_user, pass(, family)? FROM userdb WHERE family = \? AND name = \?/', $s)) {
        foreach ($F['users'] as $id => $u) if ($u['family'] === $p[0] && $u['name'] === $p[1]) return array(array('id_user' => $id, 'pass' => $u['pass'], 'family' => $u['family'])); return array();
    }
    if (preg_match('/^SELECT cookbooks.id_user, id_recipe, status/', $s)) return array();
    if (preg_match('/^SELECT rq.pknum FROM requestdb rq/', $s)) return array();
    // Existing note: A1 rated R1 on 2026-10-01 12:00:00
    if (preg_match('/^SELECT pknum FROM notesdb WHERE id_user = \? AND id_recipe = \? AND date_note = \?/', $s))
        return ($p[0] === A1 && $p[1] === R1 && $p[2] === '2026-10-01 12:00:00') ? array(array('pknum' => 7)) : array();
    if (preg_match('/^SELECT pass FROM userdb WHERE pass = \?/', $s)) {
        $out = array(); foreach ($F['users'] as $u) if ($u['pass'] === $p[0]) $out[] = array('pass' => $u['pass']); return $out;
    }
    fwrite(STDERR, "UNKNOWN SQL: $s\n");
    return array();
}

function mysqli_report($f) { return true; }
function mysqli_connect(...$a) { return new stdClass(); }
function mysqli_connect_error() { return ''; }
function mysqli_set_charset($c, $s) { return true; }
function mysqli_close($c) { return true; }
function mysqli_prepare($c, $sql) { $st = new FakeStmt(); $st->sql = $sql; return $st; }
function mysqli_stmt_bind_param($st, $types, ...$p) {
    if (strlen($types) !== count($p)) throw new ArgumentCountError("bind_param: " . strlen($types) . " types for " . count($p) . " params");
    $st->params = $p; return true;
}
function mysqli_stmt_execute($st) {
    if (preg_match('/^\s*SELECT/i', $st->sql)) { $st->rows = fake_select($st->sql, $st->params); }
    else {
        $w = trim(preg_replace('/\s+/', ' ', str_replace('`', '', $st->sql)));
        $GLOBALS['FAKE']['writes'][] = implode(' ', array_slice(explode(' ', strtok($w, '(')), 0, 3));
        $st->affected = 1;
    }
    return true;
}
function mysqli_stmt_get_result($st) { return new FakeResult($st->rows); }
function mysqli_fetch_assoc($r) { return $r->rows[$r->i++] ?? null; }
function mysqli_num_rows($r) { return $r->num_rows; }
function mysqli_stmt_close($st) { return true; }
function mysqli_stmt_affected_rows($st) { return $st->affected; }

// ---- run the endpoint
list(, $dir, $endpoint, $post) = $argv;
$_POST = json_decode($post, true);
$GLOBALS['FAKE_SESSION'] = isset($argv[4]) ? json_decode($argv[4], true) : array();
$_SERVER['SCRIPT_FILENAME'] = $dir . '/' . $endpoint;
$_SERVER['SCRIPT_NAME'] = '/cb/' . $endpoint;
$_SERVER['REMOTE_ADDR'] = '127.0.0.1';
// Session content given on the command line is served by a custom save handler
class FakeSessionHandler implements SessionHandlerInterface {
    function open($p, $n): bool { return true; }
    function close(): bool { return true; }
    function read($id): string|false {
        $s = ''; foreach ($GLOBALS['FAKE_SESSION'] as $k => $v) $s .= $k . '|' . serialize($v); return $s;
    }
    function write($id, $d): bool { return true; }
    function destroy($id): bool { return true; }
    function gc($m): int|false { return 0; }
}
session_set_save_handler(new FakeSessionHandler(), true);
ini_set('session.use_cookies', '0');
session_id('test');
register_shutdown_function(function () {
    $w = $GLOBALS['FAKE']['writes'];
    echo "\n#WRITES=" . implode(' | ', $w);
});
// error_log() traces go to a file, not into the endpoint answer
// (cbV3 redirects them to CB_PRIVATE_DIR/php_errors.log, i.e. tests/private/)
ini_set('error_log', __DIR__ . '/private/php_errors.log');
chdir($dir);
include($dir . '/' . $endpoint);
