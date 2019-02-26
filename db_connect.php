 <?php
$servername = "samssignfinder.cqlsc1c79y05.us-east-2.rds.amazonaws.com";
$username = "shreinhart";
$password = "shrsignfinder246";

// Create connection
$conn = new mysqli($servername, $username, $password);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
echo "Connected successfully";

?> 
     