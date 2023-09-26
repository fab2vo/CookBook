<?php
/*  
 * debuggage
 *          
 */
 
 $host = 'localhost';
 $user = 'id18724387_fdxcb';
 $password = 'HelloDolly06!';
 $port = 0; //Default must be NULL to use default port
 $nombase='id18724387_cookbook';
 $serverip="http://".$_SERVER['HTTP_HOST']."/cb/";
 echo $serverip."<BR>";
 echo "Ouverture <BR>";
 $cxn = new mysqli($host,$user,$password, $nombase,$port);
// Check connection
if ($cxn -> connect_errno) {
  echo "Failed to connect to MySQL: " . $cxn -> connect_error;
  exit();
} 
 $iduser_req="c81d4e2e-bcf2-11e6-869b-7df92533d2db";
 $sql="SELECT name, family, last_sync, pass FROM userdb WHERE id_user='$iduser_req'";
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) {
	 echo "Request failed <BR>"; exit;
 }
 $ligne=mysqli_fetch_assoc($result);
 $pwd_req=$ligne['family'];
 echo $pwd_req."<BR>";
 echo "Essai redirection <BR>";
 ob_start();
 Header("Location: aaac.php");
 ob_end_flush();
 exit();
 echo "Après redirection";
 
?>