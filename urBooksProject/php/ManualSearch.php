<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established
    
    $bookTitle = $_POST['title'];
    $bookAuthor = $_POST['author'];
    $bookYear = $_POST['year'];
    $bookISBN = $_POST['isbn'];
    $queryString = "SELECT * FROM books WHERE ";
    
    if (!empty($bookISBN) && strlen($bookISBN) == 10) {
        $queryString = "SELECT * FROM books WHERE isbn10 ='" . $bookISBN . "' ";
    } elseif (!empty($bookISBN) && strlen($bookISBN) == 13) {
        $queryString = "SELECT * FROM books WHERE isbn13 ='" . $bookISBN . "' ";
    } else {
        $queryString = "SELECT * FROM books WHERE ";

        if (!empty($bookTitle)) {
            $queryString .= "title LIKE '" . $bookTitle . "%' ";
        }
        
        if (!empty($bookAuthor) && !empty($bookTitle)) {
            $queryString .= "AND author LIKE '%" . $bookAuthor . "%' ";
        } elseif (!empty($bookAuthor)) {
            $queryString .= "author LIKE '%" . $bookAuthor . "%' ";
        }

        if (!empty($bookYear)) {
            $queryString .= "AND year =" . $bookYear . " ";
        }
    }

    $queryString .= "ORDER BY books.title ASC LIMIT 25";
   
    $data = mysqli_query($connection,$queryString);

    $output["table_data"] = array();

    while($row = mysqli_fetch_array($data)){
        $result = array();
        $result["title"]     = $row["title"];
        $result["isbn13"]    = $row["isbn13"];
        $result["isbn10"]    = $row["isbn10"];
        $result["book_id"]   = $row["book_id"];
        $result["year"]      = $row["year"];
        $result["author"]    = $row["author"];
        $result["has_cover"] = $row["has_cover"];

        // Download cover .jpg from OpenLibrary
        if ($row["has_cover"] == null) {
            $command = "wget -O " . $row["book_id"] . ".jpg ";

            if (strlen($row["isbn13"]) == 13) {
                $command .= "http://covers.openlibrary.org/b/isbn/" . $row["isbn13"] . "-L.jpg";
            } elseif (strlen($row["isbn10"]) == 10) {
                $command .= "http://covers.openlibrary.org/b/isbn/" . $row["isbn10"] . "-L.jpg";
            }
            exec($command);
            $command = "mv " . $row["book_id"] . ".jpg covers/" . $row["book_id"] . ".jpg";
            exec($command);

            $queryString = "UPDATE books SET has_cover=1 WHERE book_id='" . $row["book_id"] . "'";
            mysqli_query($connection,$queryString);
        }

        array_push($output["table_data"], $result);
    }

    if (empty($output["table_data"])) {
		print("");
	} else {
		print(json_encode($output));
	}
    mysqli_close($connection);
    
?>
