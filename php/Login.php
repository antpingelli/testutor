<?php
    $con = mysql_connect("ns01.000webhost.com", "id423618_testutor", "buybacktheblock42069", "id423618_testutor");

    $username = $_POST["username"];
    $password = $_POST["password"];

    $statement = mysqli_prepare($con, "SELECT * FROM user WHERE username = ? AND password = ?");
    mysqli_stmt_bind_param($statement, "ss", $username, $password);
    mysqli_stmt_execute($statement);


    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $userID, $username, $password);

    $response = array();
    $response["success"] = false;

    while (mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["username"] = $username;
        $response["password"] = $password
    }

    echo json_encode($response);
?>