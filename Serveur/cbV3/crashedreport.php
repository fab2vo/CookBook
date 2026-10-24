<?php
/* crashedreport.php
 * ACRA crash reports receiver
 */
include("ikonexct007.inc");
debugtrace("---> " . date('Y-m-d H:i:s') . " -> " . __FILE__);

$file_received = file_get_contents('php://input');
$json = json_decode($file_received, true);

$from = "";
if (is_array($json)) {
    foreach ($json as $key => $value) {
        if (($key == 'BRAND') || ($key == 'PHONE_MODEL') || ($key == 'PRODUCT')) {
            $from .= "{$key} = {$value}  ";
        }
    }
}
debugtrace($from);

$dt = new DateTime('now', new DateTimeZone('Europe/Paris'));
$orderday = substr($dt->format('Y-m-d H:i:s'), 8, 2);
$ordermon = substr($dt->format('Y-m-d H:i:s'), 5, 2);
$ordertim = substr($dt->format('Y-m-d H:i:s'), 11, 2) . substr($dt->format('Y-m-d H:i:s'), 14, 2) . substr($dt->format('Y-m-d H:i:s'), 17, 2);

$fname = $ordermon . $orderday . $ordertim . ".txt";

$crash_dir = __DIR__ . "/crashlogs";
if (!is_dir($crash_dir)) {
    @mkdir($crash_dir, 0755, true);
}
$FileLog = $crash_dir . "/" . $fname;

$HandleLog = @fopen($FileLog, 'a');
if ($HandleLog) {
    if (is_array($json)) {
        foreach ($json as $key => $value) {
            if (is_array($value)) {
                fwrite($HandleLog, "{$key} =\n");
                foreach ($value as $key2 => $value2) {
                    fwrite($HandleLog, "---{$key2} = {$value2}\n");
                }
            } else {
                fwrite($HandleLog, "{$key} = {$value}\n");
            }
        }
    }
    fwrite($HandleLog, "\nJSON_RAW=\n");
    fwrite($HandleLog, $file_received);
    fwrite($HandleLog, "\nEND");
    fclose($HandleLog);
}

debugtrace("Success");
exit();
?>
