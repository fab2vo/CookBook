<?php
/* Program: ix.php
 * Interface web pour accès des recettes
 * paramètres GET : 	recipenum 	-> pknum de recipedb
 * 					edit 	-> anay value=yes
 *					status	-> login logout 
 *					logmessage = message erreur login
 */

 //-- initialisation sql et debug fonction
 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));
 //-- session creation and check if it is new
 session_start();
 //---- memorize call for recipe
 if (isset($_GET['recipenum'])){
	$recipenum=$_GET['recipenum'];
	$_SESSION['pknum']=$recipenum;
	}
 else $recipenum=0;
 if (isset($_GET['status'])){
	 debugtrace("Status=".$_GET['status']);
	 if ($_GET['status']=="logout") {
		 /// ----------------------------logout
		$_SESSION['auth']='no'; 
		$_SESSION['family'] = "";
		$_SESSION['pknum']=0;
		$expire = time()+60*60*24*30;
		setcookie("cb_family", "", $expire);
		$_SESSION['member'] = "";
		setcookie("cb_member", "", $expire);
		$_SESSION['id_user'] = "";
		header("Location: https://cookbookfamily.000webhostapp.com/cb/ix.php");
		exit;
	 }
 }
 if (isset($_SESSION['member'])&&($_SESSION['auth']=="yes")){
	/// ------------------  already logged
	debugtrace("Already logged");
	$member=$_SESSION['member'];
	$family=$_SESSION['family'];
	$id_user_session=$_SESSION['id_user']; 	
 } else {
	 if (isset($_GET['status'])&&($_GET['status']=="login")) { 
		// ------------------------------login
		$family_req=$_POST['family'];
		$member_req=$_POST['member'];
		$password_req=$_POST['password'];
		debugtrace("Family=".$family_req);
		debugtrace("Member=".$member_req);
		$sql="SELECT id_user, family FROM userdb  
			  WHERE family='$family_req' AND name='$member_req' AND pass='$password_req'";
		mysqli_query($cxn,'SET NAMES UTF8');
		$result = mysqli_query($cxn,$sql);
		if (!$result) {
			$message='Erreur dans BdD';
			debugtrace($message);
			header("Location: https://cookbookfamily.000webhostapp.com/cb/ix.php?logmessage=".urlencode($message));
			exit;
		} else {
			$row_member_count=$result->num_rows;
			debugtrace($row_member_count);
			if ($row_member_count==0) {
				$message='Utilisateur non trouve';
				debugtrace($message);
				header("Location: https://cookbookfamily.000webhostapp.com/cb/ix.php?logmessage=".urlencode($message));
				exit;
			}
			$ligne=mysqli_fetch_assoc($result);
			/// ---------------------- login successfull 
			extract($ligne); // id_user, family
			debugtrace($id_user);
		}
		//-----------  ecritures des cookies
		$_SESSION['auth']='yes'; 
		$_SESSION['family'] = $family_req;
		$expire = time()+60*60*24*30;
		setcookie("cb_family", $family_req, $expire);
		$_SESSION['member'] = $member_req;
		setcookie("cb_member", $member_req, $expire);
		$_SESSION['id_user'] = $id_user;
		header("Location: ix.php");
		debugtrace("After header cas login !!");
		exit;
	 } else {
		debugtrace("Pas de cookie et pas de status => input "); 
		unset($message);
		if (isset($_GET['logmessage'])) $message=$_GET['logmessage']; 
		include('ixlogform.inc');
		exit();
	 }
 }
 //-- at this point the session authorized reci
 $display="list";
 $id_user=$_SESSION['id_user'];
 include("ixdisheader.inc");
 if ((isset($_SESSION['pknum']))&&($_SESSION['pknum']>0)) {
	 $recipenum=$_SESSION['pknum'];
	 if (findPknum($recipenum,$id_user)=="none"){
		 // -------- recipe can't be dispayed
		 fillmain("Recipe cannot be displayed");
		 $_SESSION['pknum']=0;
		 exit;
		}
	 }
 if ($recipenum>0){
	 if (isset($_GET['edit'])){
		 $display="edit";
	 } else{
		 $display="display";
	 }
 }
 if (isset($_GET['logmessage'])) $message=$_GET['logmessage']; 
 else $message="";
 switch ($display){
	case 'list': 
		include("ixlist.inc");
		fillmain($message);
		// -> argument recipe et edit=oui
	break;
	case 'erreur': 
		fillmain($errecipe);
	break;
	case 'edit': 
		include("ixeditrecipe.inc");
		fillmain($message);
	break;	
	case 'display': 
		include("ixdisplayrecipe.inc");
		fillmain("");
	break;
	default:
		debugtrace("Wrong value of display");
 }
 exit;
 
 function fillmain($m)
	{
	 echo '<br><center><p class="errormessage">'.$m.'</p></center>';
	 global $serverip;
	 echo '</td></tr></table>
			<a href="ix.php">Home</a >
			<body ></html>';
	$_SESSION['pknum']=0;
	 return;
	}
	
 function findPknum($num,$user){
	 // return title or None
	 $sql="SELECT r.title FROM recipedb AS r
				LEFT JOIN cookbooks AS c ON r.id_recipe=c.id_recipe
				WHERE r.pknum='$num' AND c.id_user='$user'";
	 global $cxn;
	 mysqli_query($cxn,'SET NAMES UTF8');
	 $result = mysqli_query($cxn,$sql);
	 if (!$result) {
			return "none";
		} else {
			$row_member_count=$result->num_rows;
			if ($row_member_count!=1) return "none";
		}
		$ligne=mysqli_fetch_assoc($result);
		extract($ligne);
		return $title;
 }
?>