 <?php
 
 class mySign {
	 //public $title;
	 //public $location;
	 //public $text;
 }
 
 include 'db_config.php';

// Create connection
$conn = new mysqli(DB_SERVER, DB_USERNAME, DB_PASSWORD);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
//echo "Connected successfully";

$database = mysqli_select_db($conn, DB_DATABASE);
//if($database === true) {
	//echo 'true';
//}
//else {
	//echo 'false';
//}

$city = $_GET['city'];
//echo $city;
$result = mysqli_query($conn, "SELECT fldMarkerName, FldCityCounty, FldText FROM signs where fldVDOTDistrict='$city'");

//$data = mysqli_num_rows($result);
//echo $data;
$sign = array();

if (mysqli_num_rows($result) > 0) {
    // output data of each row
	//$sign = array();
    while($row = mysqli_fetch_assoc($result)) {
		//$title = $row["fldMarkerName"];
		//$location = $row["FldCityCounty"];
		//$text = $row["FldText"];
		//$data = array('title' => $title, 'location' => $location, 'text' => $text);
		//$final[$i]=$data;
		//$i++;
		//$MySign = new mySign();
		//$MySign->title = $row["fldMarkerName"];
		//$MySign->location = $row["FldCityCounty"];
		//$MySign->text = $row["FldText"];
		//$sign[] = $MySign;
		//var_dump($MySign);
		//$sign[] = $row["fldMarkerName"];
		$sign = array(
			'signs' => array( array(
			'title' => $row["fldMarkerName"],
			'text' => $row["FldText"],
			'location' => $row["FldCityCounty"]))
		);
			
    }
	//var_dump($sign);
	$json = json_encode($sign);
	echo $json;
	//print json_encode($r);
} else {
    echo "0 results";
}


//if($data) {
	//echo $data;
//}

mysqli_close($conn);
?>