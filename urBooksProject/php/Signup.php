<?php

    require_once("../config/creds.php");

    // Database connection object created in creds.php is $connection
    // $connection equals FALSE when a connection could not be established

    $username = $_POST['username'];
    $password = $_POST['password'];
    $email = $_POST['email'];
    $firstname = $_POST['first_name'];
    $lastname = $_POST['last_name'];
	
	if(empty($username) || empty($password) || empty($email) || empty($firstname) || empty($lastname) ){
		
		print("0");
		mysqli_close($connection);
	}
	else{
    $options = ['cost' =>11,];

    $hashpassword = password_hash($password,PASSWORD_BCRYPT,$options);

    $data = mysqli_query($connection, "INSERT INTO users (first_name, last_name, password, email, username)
            VALUES ('$firstname','$lastname','$hashpassword','$email','$username')");
	
    print($data);
    mysqli_close($connection);
	}
?>
