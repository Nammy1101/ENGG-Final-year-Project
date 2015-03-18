<?php

require_once("../config/creds.php");

// Database connection object created in creds.php is $connection
// $connection equals FALSE when a connection could not be established

if (isset($_FILES['myFile'])) {
    move_uploaded_file($_FILES['myFile']['tmp_name'], "picupload/" . $_FILES['myFile']['name']);
    $command = "zbarimg -q --raw ";
    $command .= "picupload/" . $_FILES['myFile']['name'];
    //echo exec($command);
    
    $scanned_isbn = exec($command);
    $remove       = "rm -f ";
    $remove .= "picupload/" . $_FILES['myFile']['name'];
    exec($remove);
    
    if (strlen($scanned_isbn) == 10) {
        $queryString = "SELECT * FROM books WHERE isbn10 ='" . $scanned_isbn . "'";
    } elseif (strlen($scanned_isbn) == 13) {
        $queryString = "SELECT * FROM books WHERE isbn13 ='" . $scanned_isbn . "'";
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
    
    print(json_encode($output));
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
