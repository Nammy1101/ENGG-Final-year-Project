<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established
    
    $userID = $_POST['user_id'];
    //$userID = 3;
    $trade = "SELECT q2.owned_user_id   AS my_user_id,
                     q2.owned_book_id   AS outgoing_book_id,
                     q2.owned_title     AS outgoing_title,
                     q2.owned_author    AS outgoing_author,
                     q2.owned_year      AS outgoing_year,
                     q1.owned_user_id   AS incoming_user_id,
                     q2.wanted_username AS incoming_username,
                     q1.wanted_book_id  AS incoming_book_id,
                     q1.wanted_title    AS incoming_title,
                     q1.wanted_author   AS incoming_author,
                     q1.wanted_year     AS incoming_year
            FROM   (SELECT user_books_owned.user_id  AS owned_user_id,
                           user_books_wanted.user_id AS wanted_user_id,
                           books.book_id             AS wanted_book_id,
                           books.title               AS wanted_title,
                           books.author              AS wanted_author,
                           books.year                AS wanted_year
                    FROM   user_books_wanted
                           JOIN books
                             ON user_books_wanted.book_id = books.book_id
                           INNER JOIN user_books_owned
                                   ON user_books_owned.book_id = user_books_wanted.book_id
                    WHERE  user_books_wanted.user_id = '".$userID."'
                           AND user_books_wanted.trade = true
                           AND user_books_owned.trade = true
                           AND user_books_owned.keep = false) AS q1
                   INNER JOIN (SELECT user_books_wanted.user_id AS wanted_user_id,
                                      user_books_owned.user_id  AS owned_user_id,
                                      books.book_id             AS owned_book_id,
                                      books.title               AS owned_title,
                                      books.author              AS owned_author,
                                      books.year                AS owned_year,
                                      users.username            AS wanted_username
                               FROM   user_books_wanted
                                      JOIN books
                                        ON user_books_wanted.book_id = books.book_id
                                      JOIN users
                                        ON user_books_wanted.user_id = users.user_id
                                      INNER JOIN user_books_owned
                                              ON user_books_owned.book_id =
                                                 user_books_wanted.book_id
                               WHERE  user_books_owned.user_id = '".$userID."'
                                      AND user_books_wanted.trade = true
                                      AND user_books_owned.trade = true
                                      AND user_books_owned.keep = false) AS q2
                           ON q1.wanted_user_id = q2.owned_user_id
            WHERE  q1.owned_user_id = q2.wanted_user_id
            ORDER  BY incoming_book_id,
                      outgoing_book_id;";
    
    $sell = "SELECT books.book_id AS outgoing_book_id,
                    books.title   AS outgoing_title,
                    books.author  AS outgoing_author,
                    books.year    AS outgoing_year,
                    user_books_wanted.user_id  AS incoming_user_id,
                    user_books_owned.sell      AS incoming_price,
                    users.username             AS incoming_username
             FROM   user_books_wanted
                    JOIN books
                      ON user_books_wanted.book_id = books.book_id
                    JOIN user_books_owned
                      ON user_books_owned.book_id = user_books_wanted.book_id
                    JOIN users
                      ON user_books_wanted.user_id = users.user_id
             WHERE  user_books_owned.user_id = '".$userID."'
                    AND user_books_wanted.purchase IS NOT NULL
                    AND user_books_owned.sell IS NOT NULL
                    AND user_books_wanted.purchase >= user_books_owned.sell;";
    
    $purchase = "SELECT user_books_owned.sell    AS outgoing_price,
                        user_books_owned.user_id AS incoming_user_id,
                        users.username AS incoming_username,
                        books.book_id  AS incoming_book_id,
                        books.title    AS incoming_title,
                        books.author   AS incoming_author,
                        books.year     AS incoming_year
                 FROM   user_books_owned
                        JOIN books
                          ON user_books_owned.book_id = books.book_id
                        JOIN users
                          ON user_books_owned.user_id = users.user_id
                        JOIN user_books_wanted
                          ON user_books_wanted.book_id = user_books_owned.book_id
                 WHERE  user_books_wanted.user_id = '".$userID."'
                        AND user_books_wanted.purchase IS NOT NULL
                        AND user_books_owned.sell IS NOT NULL
                        AND user_books_wanted.purchase >= user_books_owned.sell;";
    
    $output = array();
    if ($dataTrade = mysqli_query($connection, $trade)) {
        $output["trade_data"] = array();
        
        while ($row = mysqli_fetch_array($dataTrade)) {
            $resultTrade = array();
            $resultTrade["my_user_id"]        = $row["my_user_id"];
            $resultTrade["outgoing_book_id"]  = $row["outgoing_book_id"];
            $resultTrade["outgoing_title"]    = $row["outgoing_title"];
            $resultTrade["outgoing_author"]   = $row["outgoing_author"];
            $resultTrade["outgoing_year"]     = $row["outgoing_year"];
            $resultTrade["incoming_user_id"]  = $row["incoming_user_id"];
            $resultTrade["incoming_username"] = $row["incoming_username"];
            $resultTrade["incoming_book_id"]  = $row["incoming_book_id"];
            $resultTrade["incoming_title"]    = $row["incoming_title"];
            $resultTrade["incoming_author"]   = $row["incoming_author"];
            $resultTrade["incoming_year"]     = $row["incoming_year"];
            array_push($output["trade_data"], $resultTrade);
        }
    }
    
    
    if ($dataSell = mysqli_query($connection, $sell)) {
        $output["sell_data"] = array();

        while ($row = mysqli_fetch_array($dataSell)) {
            $resultSell = array();
            $resultSell["outgoing_book_id"]  = $row["outgoing_book_id"];
            $resultSell["outgoing_title"]    = $row["outgoing_title"];
            $resultSell["outgoing_author"]   = $row["outgoing_author"];
            $resultSell["outgoing_year"]     = $row["outgoing_year"];
            $resultSell["incoming_user_id"]  = $row["incoming_user_id"];
            $resultSell["incoming_username"] = $row["incoming_username"];
            $resultSell["incoming_price"]    = $row["incoming_price"];
            array_push($output["sell_data"], $resultSell);
        }
    }
    
    if ($dataPurchase = mysqli_query($connection, $purchase)) {
        $output["purchase_data"] = array();
        
        while ($row = mysqli_fetch_array($dataPurchase)) {
            $resultPurchase = array();
            $resultPurchase["outgoing_price"]    = $row["outgoing_price"];
            $resultPurchase["incoming_user_id"]  = $row["incoming_user_id"];
            $resultPurchase["incoming_username"] = $row["incoming_username"];
            $resultPurchase["incoming_book_id"]  = $row["incoming_book_id"];
            $resultPurchase["incoming_title"]    = $row["incoming_title"];
            $resultPurchase["incoming_author"]   = $row["incoming_author"];
            $resultPurchase["incoming_year"]     = $row["incoming_year"];
            array_push($output["purchase_data"], $resultPurchase);
        }
    }
	if (empty($output["trade_data"]) && empty($output["sell_data"]) && empty($output["purchase_data"])) {
		print("");
	} else {
		print(json_encode($output));
	}
    
    mysqli_close($connection);
?>