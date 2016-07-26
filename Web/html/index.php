<?php
include_once '../includes/db_connect_mobile.php';
include_once '../includes/functions_mobile.php';

define("FAILED", NULL);
define("SUCCESSFUL", 1);
define("SIGN_UP_USERNAME_CRASHED", 2);
define("ADD_NEW_USERNAME_NOT_FOUND", 2);
define("CANT_SEND_MESSAGE", 3);
define("TIME_INTERVAL_FOR_USER_STATUS", 60);
define("USER_UNAPPROVED", 0);
define("WRONG_CREDS", "Invalid username or password.");

$zero = "0";
$one = "1";
$username = (isset($_POST['username']) && count($_POST['username']) > 0) ? $_POST['username'] : NULL;
$password = isset($_POST['password']) ? $_POST['password'] : NULL;
$action = isset($_POST['action']) ? $_POST['action'] : NULL;
$out = NULL;

sec_session_start();
if (login_check($mysqli) == true) {
  $username = $_SESSION['username'];
  $userId = $_SESSION['user_id'];
  switch($action)
  {    
    case "sendMessage":
    if (isset($_POST['message'])) {
      $tousername = $_POST['to'];
      $message = htmlspecialchars($_POST['message'], ENT_QUOTES);
      $postmsg = $mysqli->prepare("INSERT INTO messages (fromuid, messagetext) VALUES (?, ?)");
      $postmsg->bind_param("is", $userId, $message);
      if ($postmsg->execute()) {
          $out = SUCCESSFUL;
          $postmsg->close();
        } else {
          $out = FAILED;
        }
    } else {
      $out = FAILED;
    }
    break;
    
    case "authenticateUser":
    $out = "success";
    break;
    
    default:
    $out = FAILED;
    echo $out;
    header('Location: http://www.google.com/');
    break;
  }
} else {
  switch($action)
  {
    case "authenticateUser":
    if (login($username, $password, $mysqli) == TRUE) {
      $out = "success";
    } else {
      $out = WRONG_CREDS; // Exit the application if the user is not authenticated.
    }
    break;
          
    default:
    $out = FAILED;
    echo $out;
    header('Location: http://www.yahoo.com/');
    break;
    }
  }
  echo $out;