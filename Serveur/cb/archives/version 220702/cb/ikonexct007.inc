<?php
/* ikonexct007.php
 * for main identifiers 
 *          
 */
 
 
 $host = 'localhost';
 $user = 'id18724387_fdxcb';
 $password = 'HelloDolly06!';
 $port = 0; //Default must be NULL to use default port
 $nombase='id18724387_cookbook';
 $notfound=false;
 $found=true;
 $serverip="http://".$_SERVER['HTTP_HOST']."/cb/";
 $debugfilename='debug.txt';
 date_default_timezone_set('Europe/Paris');
 
 $pat_family="([ !\?-_()a-zA-Z0-9]{6,45})";
 $pat_pwd="([!\?-_()a-zA-Z0-9]{3,25})";
 $pat_name="([ -_()a-zA-Z0-9]{1,25})";
 $pat_uuid="([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})";
 $pat_etape="/^[A-Za-zÀ-ÿœ #-_!?.,:()\$&£€%*+0-9]{0,499}$/"; 
 $pat_ing="/^[A-Za-zÀ-ÿœ #-_!?.,:()&\$£€%*+0-9]{0,254}$/";
 $pat_date="/^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/";
 $pat_num="([0-9]{1,9})"; 
 /// php file to debug
 $debug=0;
 $fcaller=basename($_SERVER['SCRIPT_FILENAME']);
 debugtrace($fcaller);
 //parse_url($url, PHP_URL_PATH)
 $debugphpfiles=array('updateorcreaterecipe.php','createnewmember.php','createnewfamily.php');
 $debugphpfiles[] = 'crashedreport.php';
 $debugphpfiles[] = 'addcommentwithdate.php';
 $debugphpfiles[] = 'login.php';
 //$debugphpfiles[] = 'ixupdateorcreaterecipe.php';
 $debugphpfiles[] = 'ix.php';
 if (in_array($fcaller, $debugphpfiles)) $debug=1;
 
 function debugtrace($sin)
	{
	 global $debug;
	 global $debugfilename;
	 if ($debug==1) {
		$open=file_get_contents($debugfilename);
		$open .=$sin."\r\n";
		file_put_contents($debugfilename,$open); 
		}
	 return;
	}
function debugever($sin)
	{
	 global $debug;
	 global $debugfilename;
		$open=file_get_contents($debugfilename);
		$open .=$sin."\r\n";
		file_put_contents($debugfilename,$open); 
	 return;
	}
 function exitfail($m)
	{
	 global $notfound;
	 debugtrace($m);
	 echo $notfound;
	 exit;
	}
 function exitok($m)
	{
	 global $found;
	 debugtrace("Ok,".$m);
	 echo $found;
	 exit;
	}
function finduserofrecipe($cxn, $idrecipe_req )
	{
	$sql="SELECT id_user FROM recipedb WHERE id_recipe='$idrecipe_req'";
	debugtrace($sql);
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) exitfail("No result");
	$row_count=$result->num_rows;
	if ($row_count!=1) exitfail("No or too manu users for recipe");
	$ligne=mysqli_fetch_assoc($result);
	return $ligne['id_user'];
	}
function checkrecipeincb($cxn, $idrecipe_req,$iduser_req)
	{
	$sql="SELECT pknum FROM cookbooks WHERE id_user='$iduser_req' AND id_recipe='$idrecipe_req' ";
	debugtrace($sql);
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) exitfail("Error in function checkrecipeincb");
	$row_count=$result->num_rows;
	if ($row_count=0) return false;
	return true;
	}
function checkpwd($cxn, $iduser_req, $pwd_req)
	{
	$sql="SELECT `pass` FROM `userdb` WHERE id_user='$iduser_req'";
	mysqli_query($cxn,'SET NAMES UTF8');
	$result = mysqli_query($cxn,$sql);
	if (!$result) exitfail("No result");
	$ligne=mysqli_fetch_assoc($result);
	$pwd_ofuser=$ligne['pass'];
	if ($pwd_ofuser==$pwd_req) return true;
	return false;
	}
function exitsuccess($cxn)
	{
	mysqli_close($cxn);
	debugtrace("Success");
	exit;
	}
?>

