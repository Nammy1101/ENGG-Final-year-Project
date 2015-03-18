<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established

    $ownedID    = $_POST['owned_id'];
    $keepID     = $_POST['keep'];
    $tradeID    = $_POST['trade'];
    $sellID     = $_POST['sell'];

    $queryString = "UPDATE user_books_owned SET keep='" . $keepID . "', trade='" . $tradeID . "', sell='" . $sellID . "' WHERE owned_id='" . $ownedID . "'";

    $data = mysqli_query($connection,$queryString);
    if (!$data) {
        $responseString = "Could not insert.";
    } else {
        $responseString = "Successfully updated book!";
    }

    $output["table_data"] = array();
    $result = array();

    $result["response"] = $responseString;
    array_push($output["table_data"], $result);

    print(json_encode($output));
    mysqli_close($connection);    
?>
