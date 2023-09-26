<?php
// name crashedreport.php 
include ("ikonexct007.inc");
debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
$ver=file_get_contents('php://input');
$file_received=file_get_contents('php://input');
//debugtrace($file_received);
$json = json_decode($file_received, true);
$from="";
foreach($json as $key => $value) { 
	if (($key=='BRAND')||($key=='PHONE_MODEL')||($key=='PRODUCT'))
		$from=$from."{$key} = {$value}  ";
}
debugtrace( $from);
$dt = new DateTime('now', new DateTimeZone('Europe/Paris'));
// save the log to a filename with MMDDHHMMSS such as: "1123132456.txt"
$orderday = substr($dt->format('Y-m-d H:i:s'),8,2);
$ordermon = substr($dt->format('Y-m-d H:i:s'),5,2);
$ordertim = substr($dt->format('Y-m-d H:i:s'),11,2) . substr($dt->format('Y-m-d H:i:s'),14,2)
. substr($dt->format('Y-m-d H:i:s'),17,2);
$fname = $ordermon . $orderday . $ordertim . ".txt";
$FileLog = $_SERVER['DOCUMENT_ROOT'] . "/cb/crashlogs/" . $fname;
debugtrace($FileLog);
$HandleLog = fopen($FileLog, 'a');
	foreach($json as $key => $value) { 
	if (is_array($value)) {
			fwrite($HandleLog, "{$key} =\n");
			foreach($value as $key2 => $value2) { 
				fwrite($HandleLog, "---{$key2} = {$value2}\n");
			}		
		} else
		fwrite($HandleLog,"{$key} = {$value}\n");
	}
fwrite($HandleLog, "JSON_RAW=\n");
fwrite($HandleLog, $file_received);
fwrite($HandleLog, "END");
fclose($HandleLog);
debugtrace("Success");
exit();
?>