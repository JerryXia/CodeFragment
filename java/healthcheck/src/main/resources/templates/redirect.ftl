<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>redirect</title>
</head>
<body>
<input type="hidden" id="hid_jumpurl" value="${jumpurl?html}" />
<h1>${msg}</h1>
<script>
var jumpurl = document.getElementById('hid_jumpurl').value;
if(jumpurl && jumpurl.length > 0){
   setTimeout(function() {
      location.href = jumpurl;
   }, 1000);
}
</script>
</body>
</html>