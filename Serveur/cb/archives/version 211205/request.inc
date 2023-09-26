mysqli_query($cxn,'SET NAMES UTF8');
$result = mysqli_query($cxn,$sql);
if (!$result) { echo $notfound; exit;}