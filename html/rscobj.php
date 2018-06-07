
<?php
define('LUNA_ROOT', dirname(__FILE__) . '/');

// Databas uppkopplings konfigurationer.
	$host = "127.0.0.1";
	$username = "root";
	$password = "malware";
	$dbname = "wolf_kingdom";

	// Skapar länk med databasen.
	// variablen $db kommer innehålla connection till databasen.
	$obj_db = mysqli_connect($host, $username, $password, $dbname);

	// Kollar om den existerar, gör den inte det dödar vi och skickar fel meddelande.
	if (!$obj_db) {
		die("Database Connection failed: " . mysqli_connect_error());
	}


if(isset($_POST['id'])) {
	$id = mysqli_real_escape_string($obj_db, $_POST['id']);
	$x = mysqli_real_escape_string($obj_db, $_POST['x']);
	$y = mysqli_real_escape_string($obj_db, $_POST['y']);
	$dir = mysqli_real_escape_string($obj_db, $_POST['dir']);
	$type = mysqli_real_escape_string($obj_db, $_POST['type']);
	
	$exists = mysqli_query($obj_db, "SELECT 1 FROM `rscl_objects` WHERE `x`='".$x."' AND `y`='".$y."' AND `direction`='".$dir."' AND `type`='".$type."'");
	
	if(mysqli_num_rows($exists) > 0) {
		echo "Exists:('".$x."','".$y."','".$id."','".$dir."','".$type."')";
	} else {
		mysqli_query($obj_db, "INSERT INTO `rscl_objects`(`x`, `y`, `id`, `direction`, `type`) VALUES ('".$x."','".$y."','".$id."','".$dir."','".$type."')");
		echo "Success:('".$x."','".$y."','".$id."','".$dir."','".$type."')";
	}
} else {
	echo 'nothing to see here..';
}

$obj_db->close();