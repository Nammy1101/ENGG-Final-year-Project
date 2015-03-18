<?php

require_once("../config/creds.php");

// Database connection object created in creds.php is $connection
// $connection equals FALSE when a connection could not be established

$userID = $_POST['user_id'];
$queryString = "SELECT user_books_owned.owned_id,user_books_owned.user_id,user_books_owned.keep,user_books_owned.trade,user_books_owned.sell,books.* FROM user_books_owned JOIN books ON user_books_owned.book_id = books.book_id WHERE user_books_owned.user_id = ";
$queryString.= $userID;
$queryString.= " ORDER  BY books.title ASC LIMIT  15";
$data = mysqli_query($connection, $queryString);
$output["table_data"] = array();

while ($row = mysqli_fetch_array($data)) {
    $result = array();
    $result["owned_id"]    = $row["owned_id"];
    $result["user_id"]     = $row["user_id"];
    $result["keep"]        = $row["keep"];
    $result["trade"]       = $row["trade"];
    $result["sell"]        = $row["sell"];
    $result["title"]       = $row["title"];
    $result["isbn13"]      = $row["isbn13"];
    $result["isbn10"]      = $row["isbn10"];
    $result["book_id"]     = $row["book_id"];
    $result["year"]        = $row["year"];
    $result["author"]      = $row["author"];
    $result["has_cover"]   = $row["has_cover"];
    array_push($output["table_data"], $result);
}

print (json_encode($output));
mysqli_close($connection);
?>
