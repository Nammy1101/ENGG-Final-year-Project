<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established

	$ID = $_POST['ID'];

	$data = mysqli_query($connection,"SELECT * FROM users WHERE user_id='".$ID."'");

	$output["table_data"] = array();
	
	$row = mysqli_fetch_array($data);
	
	$result = array();
	$result["firstName"] = $row["first_name"];
	$result["lastName"] = $row["last_name"];
	$result["email"] = $row["email"];
	$result["username"] = $row["username"];
	
	array_push($output["table_data"],$result);
	
	// echo json_encode($output);
    print(json_encode($output));
    //echo "User Found";
    mysqli_close($connection);

?>