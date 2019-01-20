<?php

// Directory to save user documents.
$data_directory = '_notepad';

/**
 * Sanitizes a string to include only alphanumeric characters.
 *
 * @param  string $string the string to sanitize
 * @return string         the sanitized string
 */
function sanitizeString($string) {
    return preg_replace("/[^a-zA-Z0-9]+/", "", $string);
}

/**
 * Generates a random string.
 *
 * @param  integer $length the length of the string
 * @return string          the new string
 *
 * Initially based on http://stackoverflow.com/a/4356295/1391963
 */
function generateRandomString($length = 5) {
    // Do not generate ambiguous characters. See http://ux.stackexchange.com/a/53345/25513
    $characters = '23456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ';
    $randomString = '';
    for ($i = 0; $i < $length; $i++) {
        $randomString .= $characters[rand(0, strlen($characters) - 1)];
    }
    return $randomString;
}


// Disable caching.
header("Cache-Control: no-cache, no-store, must-revalidate");
header("Pragma: no-cache");
header("Expires: 0");

if (empty($_GET['f']) || sanitizeString($_GET['f']) !== $_GET['f']) {

    // User has not specified a valid name, generate one.
    header('Location: ' . rtrim(dirname($_SERVER['SCRIPT_NAME']), '/') . '/' . generateRandomString());
    die();
}

$name = sanitizeString($_GET['f']);
$path = $data_directory . '/' . $name;

if (isset($_POST['t'])) {

    // Update file.
    file_put_contents($path, $_POST['t']);
    die();
}

if (preg_match('/^curl\//', $_SERVER ['HTTP_USER_AGENT'])) {
    // Output file.
    echo file_get_contents($path);
    die();
}
?>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title><?php print $name; ?></title>
    <link href="data:image/x-icon;base64,R0lGODlhEAAQAKIGAL7FzEBUaLK5wnOBkJCbp9nd4f///wAAACH5BAEAAAYALAAAAAAQABAAAANJaKrR7msFMga1o8UguvcBECyDUJ1lJjIdWgrharxfG49cDQL8SNeqXk4H4/V+n2BB9GjGjCOJcQrdUHmFZZRxnW4NzyvhC3ZCEgA7" rel="icon" type="image/x-icon" />
    <style type="text/css">
body {
    margin: 0;
    background: #ebeef1;
}

#content {
    font-size: 100%;
    margin: 0;
    padding: 20px;
    overflow-y: auto;
    resize: none;
    width: 100%;
    height: 100%;
    min-height: 100%;
    -webkit-box-sizing: border-box;
    -moz-box-sizing: border-box;
    box-sizing: border-box;
    border: 1px #ddd solid;
    outline: none;
}

.container {
    position: absolute;
    top: 20px;
    right: 20px;
    bottom: 20px;
    left: 20px;
}

#printable {
    display: none;
}

@media print {
    #content {
        display: none;
    }

    #printable {
        display: block;
    }
} 
    </style>
</head>
<body>
    <div class="container">
        <textarea id="content"><?php
            if (file_exists($path)) {
                print htmlspecialchars(file_get_contents($path), ENT_QUOTES, 'UTF-8');
            }
      ?></textarea>
        <pre id="printable"></pre>
    </div>
    <script>
function uploadContent() {
    if (content !== textarea.value) {
        // Text area value has changed.
        var temp = textarea.value;
        var request = new XMLHttpRequest();
        request.open('POST', window.location.href, true);
        request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');

        request.onload = function() {
            if (request.readyState === 4) {
                // Request has ended, check again after 1 second.
                content = temp;
                setTimeout(uploadContent, 1000);
            }
        }

        request.onerror = function() {
            // On connection error, try again after 1 second.
            setTimeout(uploadContent, 1000);
        }

        // Send the request.
        request.send("t=" + encodeURIComponent(temp));

        // Make the content available to print.
        printable.removeChild(printable.firstChild);
        printable.appendChild(document.createTextNode(temp));
    } else {
        // Content has not changed, check again after 1 second.
        setTimeout(uploadContent, 1000);
    }
}

var textarea = document.getElementById('content');
var printable = document.getElementById('printable');
var content = textarea.value;

// Make the content available to print.
printable.appendChild(document.createTextNode(content));

textarea.focus();

uploadContent();
    </script>
</body>
</html>

