<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established
    
    function checkOpenLibrary($isbn, $conn) {
        if (empty($isbn) || $isbn == "") { return false; }
        
        $response = file_get_contents("https://openlibrary.org/api/books?bibkeys=ISBN:".$isbn."&jscmd=data&format=json");
        if (empty($response)) { return false; }

        $json_response = json_decode($response, true);
        if (empty($json_response)) { return false; }
        
        $keys = array_keys($json_response);
        $json_response = $json_response[$keys[0]];
        if (empty($json_response)) { return false; }

        $title     = $json_response['title'];
        $isbn10    = $json_response['identifiers']['isbn_10'][0];
        $isbn13    = $json_response['identifiers']['isbn_13'][0];
        $cover_url = $json_response['cover']['large'];
        $year      = $json_response['publish_date'];
        $key       = $json_response['key'];
        
        if (empty($key)) { return false; }
        if (empty($title) || empty($cover_url) || empty($year)) {return false;}
        if (empty($isbn10) && empty($isbn13)) { return false; }
        
        if (!empty($title) && !empty($json_response['subtitle'])) {
            $title .= ": ".$json_response['subtitle'];
        }

        if (preg_match('/\b\d{4}\b/', $json_response['publish_date'], $match)) {
            $year = $match[0];
        }
        
        if (!empty($json_response['key'])) {
            $response = file_get_contents("http://openlibrary.org".$json_response['key'].".json");
            if (empty($response)) { return false; }
            
            $author_array = json_decode($response, true);
            if (empty($author_array)) { return false; }

            $authors = "";

            foreach ($author_array['authors'] as $author) {
                $response = file_get_contents("http://openlibrary.org".$author['key'].".json");
                if (empty($response)) { return false; }
                
                $name = json_decode($response, true);
                if (empty($name) || empty($name['name'])) { return false; }

                $authors .= "; ".$name['name'];
            }

            if (empty($authors) && !empty($author_array['by_statement'])) {
                $authors = $author_array['by_statement'];
            } else {
                $authors = substr($authors, 2, strlen($authors)-1);
            }
        }
        
        $query = "INSERT INTO books (isbn10,isbn13,author,title,year)
            VALUES('".$isbn10."','".$isbn13."','".$authors."','".$title."',".$year.")";

        $q_data = mysqli_query($conn,$query);
        if (!$q_data) { return false; }
        
        return true;
    }
    
    $bookTitle = $_POST['title'];
    $bookAuthor = $_POST['author'];
    $bookYear = $_POST['year'];
    $bookISBN = $_POST['isbn'];
    $queryString = "SELECT * FROM books WHERE ";
    
    $usedISBN = false;
    
    if (!empty($bookISBN) && strlen($bookISBN) == 10) {
        $queryString = "SELECT * FROM books WHERE isbn10 ='" . $bookISBN . "' ";
        $usedISBN = true;
    } elseif (!empty($bookISBN) && strlen($bookISBN) == 13) {
        $queryString = "SELECT * FROM books WHERE isbn13 ='" . $bookISBN . "' ";
        $usedISBN = true;
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

            $updateQuery = "UPDATE books SET has_cover=1 WHERE book_id='" . $row["book_id"] . "'";
            mysqli_query($connection,$updateQuery);
        }

        array_push($output["table_data"], $result);
    }

    if (empty($output["table_data"])) {  // book not in urBooks database
        if ($usedISBN && checkOpenLibrary($bookISBN, $connection)) {
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

                    $updateQuery = "UPDATE books SET has_cover=1 WHERE book_id='" . $row["book_id"] . "'";
                    mysqli_query($connection,$updateQuery);
                }

                array_push($output["table_data"], $result);
            }

            if (empty($output["table_data"])) {
                print("");
            } else {
                print(json_encode($output));
            }
        } else {
		    print("");
        }
	} else {
		print(json_encode($output));
	}
    mysqli_close($connection);
    
?>
