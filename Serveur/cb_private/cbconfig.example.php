<?php
/* cbconfig.php — CookBook API private configuration
 *
 * Copy this file as cbconfig.php into a "cb_private" folder placed OUTSIDE the web root,
 * next to public_html (not inside it):
 *     .../cookbookfamily.cloud/public_html/cb/        <- API files (cbV3)
 *     .../cookbookfamily.cloud/cb_private/cbconfig.php <- this file
 * The debug log (debug.log) is written in the same folder.
 *
 * Never commit cbconfig.php: only this example file belongs in Git.
 */
if (!defined('IN_CB')) {
    exit;
}

return array(
    'db_host'     => 'localhost',
    'db_user'     => 'CHANGE_ME',
    'db_password' => 'CHANGE_ME',
    'db_name'     => 'CHANGE_ME',
    'db_port'     => null,
);
