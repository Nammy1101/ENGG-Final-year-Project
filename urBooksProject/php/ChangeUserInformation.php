<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established
	
	$ID = $_POST['ID'];
	$Selection = $_POST['selection'];
	$result = array();
	
	if(strpos($Selection,'changeall') !== FALSE ){
		$FirstName = $_POST['FirstName'];
		$LastName = $_POST['LastName'];
		$Email = $_POST['Email'];
		
		$data = mysqli_query($connection,"UPDATE users SET first_name = '". $FirstName ."', 
		last_name = '". $LastName."',
		email = '".$Email."'
		WHERE user_id='".$ID."'");
		
		$result["result"] = "User Updated";
	}
	else if(strpos($Selection,'changefirstname') !== FALSE){
		$FirstName = $_POST['Value'];
		$data = mysqli_query($connection,"UPDATE users SET first_name = '". $FirstName ."'
		WHERE user_id='".$ID."'");
		
		$result["result"] = "First Name updated";
	}
	else if(strpos($Selection,'changelastname') !== FALSE){
		$LastName = $_POST['Value'];
		$data = mysqli_query($connection,"UPDATE users SET last_name = '". $LastName ."'
		WHERE user_id='".$ID."'");
		
		$result["result"] = "Last Name updated";
	}
	else if(strpos($Selection,'changeemail') !== FALSE){
		$Email = $_POST['Value'];
		$data = mysqli_query($connection,"UPDATE users SET email = '". $Email ."'
		WHERE user_id='".$ID."'");
		
		$result["result"] = "Email updated";
	}
	
	$output["table_data"] = array();
	
	array_push($output["table_data"],$result);
	
	print(json_encode($output));
    //echo "User Found";
    mysqli_close($connection);
?>