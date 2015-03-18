<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established
    
    $matchedUserID = $_POST['matched_user_id'];
	
	$queryString = "
				SELECT 
				users.first_name AS matched_first_name,
				users.last_name AS matched_last_name,
				users.email AS matched_email,
				users.username AS matched_username
				FROM 
				users
				WHERE user_id='".$matchedUserID."'
	";
	
	$data = mysqli_query($connection,$queryString);
	$output["table_data"] = array();
	$row = mysqli_fetch_array($data);
	$result = array();
	
	$result["matched_first_name"] = $row["matched_first_name"];
	$result["matched_last_name"] = $row["matched_last_name"];
	$result["matched_email"] = $row["matched_email"];
	$result["matched_username"] = $row["matched_username"];
	
	array_push($output["table_data"],$result);
    print(json_encode($output));
    //echo "User Found";
    mysqli_close($connection);
?>