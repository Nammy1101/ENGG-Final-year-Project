<?php

require_once("../config/creds.php");

// Database connection object created in creds.php is $connection
// $connection equals FALSE when a connection could not be established

$userID = $_POST['user_id'];
$queryString = "SELECT user_books_wanted.wanted_id,user_books_wanted.user_id,user_books_wanted.purchase,user_books_wanted.trade,books.* FROM user_books_wanted JOIN books ON user_books_wanted.book_id = books.book_id WHERE  user_books_wanted.user_id = ";
$queryString.= $userID;
$queryString.= " ORDER BY books.title ASC LIMIT 15";
$data = mysqli_query($connection, $queryString);
$output["table_data"] = array();

while ($row = mysqli_fetch_array($data)) {
    $result = array();
    $result["wanted_id"] = $row["wanted_id"];
    $result["user_id"]   = $row["user_id"];
    $result["purchase"]  = $row["purchase"];
    $result["trade"]     = $row["trade"];
    $result["title"]     = $row["title"];
    $result["isbn13"]    = $row["isbn13"];
    $result["isbn10"]    = $row["isbn10"];
    $result["book_id"]   = $row["book_id"];
    $result["year"]      = $row["year"];
    $result["author"]    = $row["author"];
    $result["has_cover"] = $row["has_cover"];
    array_push($output["table_data"], $result);
}

print (json_encode($output));
mysqli_close($connection);
?>
