<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established

    $wantedID   = $_POST['wanted_id'];
    $tradeID    = $_POST['trade'];
    $purchaseID = $_POST['purchase'];

    $queryString = "UPDATE user_books_wanted SET trade='" . $tradeID . "', purchase='" . $purchaseID . "' WHERE wanted_id='" . $wantedID . "'";

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
