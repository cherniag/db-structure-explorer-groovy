<?
$filename = "emails.csv";
if (is_writable($filename)) {
	$fh = fopen($filename, "a");
	fputs($fh,$_GET['email'].";");
	close($fh);
} else {
header("HTTP/1.1 500 Internal Server Error");
exit(0);
}
?>