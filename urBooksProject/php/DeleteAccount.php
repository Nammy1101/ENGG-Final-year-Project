<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established
	
	$userID = $_POST['user_id'];
	
	$queryString1 = "DELETE FROM `users` WHERE `user_id` ='".$userID."'";
	$queryString2 = "DELETE FROM user_books_owned WHERE user_id ='".$userID."'";
	$queryString3 = "DELETE FROM user_books_wanted WHERE user_id ='".$userID."'";
	
	 $data = mysqli_query($connection,$queryString1);
	 if (!$data) {
        $responseString = "Could not Delete.";
	 }
	 
	 $data = mysqli_query($connection,$queryString2);
	 if (!$data) {
        $responseString = "Could not Delete.";
	 }
	  
	 $data = mysqli_query($connection,$queryString3);
	 if (!$data) {
        $responseString = "Could not Delete.";
	 }
	  
	  else{
		  $responseString = "User Deleted";
	  }
	  
	$output["table_data"] = array();
    $result = array();

    $result["response"] = $responseString;
    array_push($output["table_data"], $result);

    print(json_encode($output));
	mysqli_close($connection);
?>