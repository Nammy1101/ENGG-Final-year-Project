<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established
	
	$userID = $_POST['user_id'];
	
	$queryString = "";
	
	 $data = mysqli_query($connection,$queryString);
	 if (!$data) {
        $responseString = "Could not insert.";
	 }
	  
	$output["table_data"] = array();
    $result = array();

    $result["response"] = $responseString;
    array_push($output["table_data"], $result);

    print(json_encode($output));
	mysqli_close($connection);
?>