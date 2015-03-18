<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established

    $username = $_POST['username'];
    $password = $_POST['password'];
    
    $data = mysqli_query($connection,"SELECT * FROM users WHERE username='".$username."'");
       // AND password='".$password."'");

    //if($count){
      //  echo($count);
   // } 
    $output["table_data"] = array();

    $row = mysqli_fetch_array($data);
    
    if($row["username"]){
        if(password_verify($password,$row["password"])){
            $result = array();
            $result["user_id"] = $row["user_id"];
            $result["first_name"] = $row["first_name"];
            $result["last_name"] = $row["last_name"];
            $result["email"] = $row["email"];
            $result["username"] = $row["username"];
            $result["password"] = $row["password"];
            $result["response"] = "true";
            array_push($output["table_data"],$result);
        }
        else{
            $result = array();
            $result["response"] = "Invalid login, please try again";
            array_push($output["table_data"],$result);
        }
    }
    else{
        $result = array();
        $result["response"] = "Invalid login, please try again";
        array_push($output["table_data"],$result);
    }

    print(json_encode($output));
    mysqli_close($connection);
?>
