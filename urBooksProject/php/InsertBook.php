<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established

    $bookID     = $_POST['book_id'];
    $userID     = $_POST['user_id'];
    $keepID     = $_POST['keep'];
    $tradeID    = $_POST['trade'];
    $sellID     = $_POST['sell'];
    $purchaseID = $_POST['purchase'];
    $optionSelected = $_POST['option_selected']; 

    //if(strpos($optionSelected,'want') !== false){
    if ($optionSelected == "want") {
        $queryString = "INSERT INTO user_books_wanted (user_id,book_id,trade,purchase)
            VALUES('" . $userID . "','" . $bookID . "'," . $tradeID . "," . $purchaseID . ")";
    } else {
        $queryString = "INSERT INTO user_books_owned (user_id,book_id,keep,trade,sell)
                 VALUES('" . $userID . "','" . $bookID . "'," . $keepID . "," . $tradeID . "," . $sellID . ")";
    }

    $data = mysqli_query($connection,$queryString);
    if (!$data) {
        $responseString = "Could not insert.";
    } else {
        $responseString = "Successfully added book!";
    }

    $output["table_data"] = array();
    $result = array();

    $result["response"] = $responseString;
    array_push($output["table_data"], $result);

    print(json_encode($output));
    mysqli_close($connection);    
?>
