<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established
	
	$ID = $_POST['ID'];
	$OldPassword = $_POST['OldPassword'];
	$NewPassword = $_POST['NewPassword'];
	$options = ['cost' =>11,];
	
	$data = mysqli_query($connection,"SELECT password FROM users WHERE user_id='".$ID."'");
	
	$output["table_data"] = array();
	$result = array();
	$row = mysqli_fetch_array($data);
	
	 if(password_verify($OldPassword,$row["password"])){
		$hashpassword = password_hash($NewPassword,PASSWORD_BCRYPT,$options);
		$data = mysqli_query($connection,"UPDATE users SET password = '".$$hashpassword."' WHERE user_id='".$ID."'");
		$result["result"] = "Password Changed";
	 }else{
		 $result["result"] = "Password Verification Failed";
	 }
	 
	 array_push($output["table_data"],$result);
	 
	print(json_encode($output));
    //echo "User Found";
    mysqli_close($connection);
	
?>