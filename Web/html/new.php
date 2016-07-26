<?php
exit;
$passwordp = "";
$password = hash('sha512', $passwordp);
$random_salt = hash('sha512', uniqid(mt_rand(1, mt_getrandmax()), true));
$password = hash('sha512', $password . $random_salt);
echo $random_salt;
echo "<br><br>";
echo $password;