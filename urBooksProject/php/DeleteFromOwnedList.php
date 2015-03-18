<?php

require_once("../config/creds.php");

// Database connection object created in creds.php is $connection
// $connection equals FALSE when a connection could not be established

$ownedID = $_POST['owned_id'];
$queryString = "DELETE FROM user_books_owned WHERE owned_id = ";
$queryString.= $ownedID;
$data = mysqli_query($connection, $queryString);

if(!$data){
    $responseString = "Could not delete.";
} else {
    $responseString = "Successfully removed book!";
}

print ($responseString);
mysqli_close($connection);
?>
