<?php
include_once '../includes/db_connect_mobile.php';

$abouttabsql = 'SELECT id, fromuid, sentdt, messagetext FROM messages';

if ($stmt = $mysqli->prepare($abouttabsql)) {
  $stmt->execute();
  $stmt->store_result();
  $num_of_rows = $stmt->num_rows;
  $stmt->bind_result($dbid, $dbfrom, $dbsentdt, $dbmessagetext);
  while ($stmt->fetch()) {
    echo '<html><body>';
    echo '<h3>'.$dbid.' from '.$dbfrom.'</h3>';
    echo '<h4>'.$dbsentdt.'</h4>';
    echo '<p>'.$dbmessagetext.'</p><br>';
    echo '</body></html>';
  } 
  $stmt->free_result();
}
$stmt->close();
$mysqli->close();