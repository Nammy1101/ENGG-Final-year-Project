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

if (isset($_FILES['myFile'])) {
    move_uploaded_file($_FILES['myFile']['tmp_name'], "picupload/" . $_FILES['myFile']['name']);
    $command = "zbarimg -q --raw ";
    $command .= "picupload/" . $_FILES['myFile']['name'];
    //echo exec($command);
    
    $scanned_isbn = exec($command);
    $remove       = "rm -f ";
    $remove .= "picupload/" . $_FILES['myFile']['name'];
    exec($remove);
    
    $usedISBN = false;
    
    if (strlen($scanned_isbn) == 10) {
        $queryString = "SELECT * FROM books WHERE isbn10 ='" . $scanned_isbn . "'";
        $usedISBN = true;
    } elseif (strlen($scanned_isbn) == 13) {
        $queryString = "SELECT * FROM books WHERE isbn13 ='" . $scanned_isbn . "'";
        $usedISBN = true;
    } else {
        $queryString = "";
    }
    
    $data                 = mysqli_query($connection, $queryString);
    $output["table_data"] = array();
    
    while ($row = mysqli_fetch_array($data)) {
        $result                = array();
        $result["title"]     = $row["title"];
        $result["isbn13"]    = $row["isbn13"];
        $result["isbn10"]    = $row["isbn10"];
        $result["book_id"]   = $row["book_id"];
        $result["year"]      = $row["year"];
        $result["author"]    = $row["author"];
        $result["has_cover"] = $row["has_cover"];
        
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
            mysqli_query($connection, $queryString);
        }
        array_push($output["table_data"], $result);
    }
    
    if (empty($output["table_data"])) {
        if ($usedISBN && checkOpenLibrary($scanned_isbn, $connection)) {

            $data = mysqli_query($connection, $queryString);
            $output["table_data"] = array();
            
            while ($row = mysqli_fetch_array($data)) {
                $result                = array();
                $result["title"]     = $row["title"];
                $result["isbn13"]    = $row["isbn13"];
                $result["isbn10"]    = $row["isbn10"];
                $result["book_id"]   = $row["book_id"];
                $result["year"]      = $row["year"];
                $result["author"]    = $row["author"];
                $result["has_cover"] = $row["has_cover"];
                
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
                    mysqli_query($connection, $queryString);
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
    
    //$remove = "rm -f ";
    //$remove .= "picupload/".$_FILES['myFile']['name'];
    //exec($remove);
    //echo 'successful';
    //echo exec("zbarimg -q --raw picupload/ . $_FILES['myFile']['name']");
}
//else {
//    echo "[HARDCODED] " . exec('zbarimg -q --raw picupload/1420087936524.jpg');
//}

//exec("zbarimg -q --raw picupload/1420087936524.jpg", $output, $return);
//echo "returned: $return and output:\n";
//var_dump($output);

//$test = shell_exec("zbarimg -q --raw picupload/1420087936524.jpg");
//echo $test;
?>
