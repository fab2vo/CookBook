<?php
/* Program: addcommentwithdate.php
 * Create new comment for a recipe 
 * input : idrecipe comment idfrom (pwd)
 * output : false or true
 */


 include ("ikonexct007.inc");
 debugtrace("---> ".date('Y-m-d H:i:s')."  ->  ".__FILE__);
 
 if (! $cxn = mysqli_connect($host,$user,$password, $nombase,$port)) exitfail(mysqli_error($cxn));

 $idrecipe_req=$_POST['idrecipe'];
 $iduser_req=$_POST['idfrom'];
 $comment_req=addslashes($_POST['comment']);
 $date_req=$_POST['date'];
 /// check inputs
 if (! preg_match($pat_uuid, $idrecipe_req)) exitfail("Error in Id recipe ".$idrecipe_req);
 if (! preg_match($pat_uuid, $iduser_req)) exitfail("Error in Id user ".$iduser_req);
 if (! preg_match($pat_date, $date_req)) exitfail("Error in date format ".$date_req);
   //input pwd from $id_user_req
 include ("itchekpwd.inc");
   /// check if recipe exists
 $sql="SELECT * FROM `recipedb` WHERE id_recipe='$idrecipe_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $row_count=$result->num_rows;
 if ($row_count!=1) exitfail("No / too many recipe found :".$idrecipe_req);
 
 /// check if user exists
 $sql="SELECT * FROM userdb WHERE id_user='$iduser_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $row_count=$result->num_rows;
 if ($row_count!=1) exitfail("No / too many user found :".$iduser_req);	
 
 /// check if comment exists already
 $sql="SELECT * FROM commentsdb WHERE id_user='$iduser_req' AND id_recipe='$idrecipe_req' AND date_comment='$date_req'";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("No result");
 $row_count=$result->num_rows;
 if ($row_count!=0) exitfail("Comment already exists ");	

  /// add comment
 $sql="INSERT INTO `commentsdb` (`id_recipe`, `comment`, `id_user`, `date_comment`)
		VALUES ( '$idrecipe_req', '$comment_req', '$iduser_req', '$date_req') ";
 debugtrace($sql);
 mysqli_query($cxn,'SET NAMES UTF8');
 $result = mysqli_query($cxn,$sql);
 if (!$result) exitfail("Insert failed");
 /// success !
 echo $found;
  exitsuccess($cxn);
?>

