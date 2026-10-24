<?php
/* idconnect.inc
 * Loads database credentials for CookBook API V3.
 * Credentials are NOT stored here: they live in cbconfig.php, outside the web root
 * (e.g. /home/<account>/domains/cookbookfamily.cloud/cb_private/cbconfig.php
 *  when the API is in .../cookbookfamily.cloud/public_html/cb/).
 * The CB_CONFIG environment variable can point to another location.
 */
if (!defined('IN_CB')) {
    define('IN_CB', true);
}

$cb_config_file = getenv('CB_CONFIG');
if (!$cb_config_file) {
    $cb_config_file = dirname(__DIR__, 2) . '/cb_private/cbconfig.php';
}

if (!is_readable($cb_config_file)) {
    error_log("CookBook: configuration file not found: " . $cb_config_file);
    // Same answer as any other failure, so the app handles it as usual
    echo "FAIL: Server configuration error";
    exit;
}

$cb_config = include($cb_config_file);

if (!defined('CB_PRIVATE_DIR')) {
    define('CB_PRIVATE_DIR', dirname($cb_config_file));
}

$host     = $cb_config['db_host'];
$user     = $cb_config['db_user'];
$password = $cb_config['db_password'];
$nombase  = $cb_config['db_name'];
$port     = $cb_config['db_port'] ?? null;
unset($cb_config);

$notfound = false;
$found    = true;

// Dynamic URL detection - 100% independent of directory name (/cb/, /cbV2/, etc.)
$protocol = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off') ? "https://" : "http://";
$host_header = $_SERVER['HTTP_HOST'] ?? 'cookbookfamily.cloud';
$dir_name = rtrim(dirname($_SERVER['SCRIPT_NAME'] ?? '/cb/'), '/\\');
$serverip = $protocol . $host_header . $dir_name . "/";
?>
