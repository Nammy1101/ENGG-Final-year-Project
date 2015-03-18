<?php

require_once("../config/creds.php");

// Database connection object created in creds.php is $connection
// $connection equals FALSE when a connection could not be established

$wantedID = $_POST['wanted_id'];
$queryString = "DELETE FROM user_books_wanted WHERE wanted_id = ";
$queryString.= $wantedID;
$data = mysqli_query($connection, $queryString);

if(!$data){
    $responseString = "Could not delete.";
} else{
    $responseString = "Successfully removed book!";
}

print ($responseString);
mysqli_close($connection);
?>
