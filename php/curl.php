<?php

header('Content-type:text/html;charset=utf-8');
function fly_curl($url, $postFields = null) {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_USERAGENT, 'Mozilla/5.0 (compatible; MSIE 5.01; Windows NT 5.1)');
    curl_setopt($ch, CURLOPT_HEADER, 0);
    curl_setopt($ch, CURLOPT_FAILONERROR, 0);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    //curl_setopt($ch,CURLOPT_HTTPHEADER,array("Expect:"));
    if (is_array($postFields) && 0 < count($postFields)) {
        $postBodyString = "";
        $postMultipart = false;
        //判断是不是文件上传
        foreach ($postFields as $k => $v) {
            if("@" != substr($v, 0, 1)) {
                $postBodyString .= "$k=" . urlencode($v) . "&";
            }
            //文件上传用multipart/form-data，否则用www-form-urlencoded
            else {
                $postMultipart = true;
            }
        }
        unset($k, $v);
        curl_setopt($ch, CURLOPT_POST, 1);
        if ($postMultipart) {
            curl_setopt($ch, CURLOPT_POSTFIELDS, $postFields);
        }
        else {
            //var_dump($postBodyString);
            curl_setopt($ch, CURLOPT_POSTFIELDS, substr($postBodyString,0,-1));
        }
    }
    $reponse = curl_exec($ch);
    //return curl_getinfo($ch);
    if (curl_errno($ch)) {
        throw new Exception(curl_error($ch),0);
    }
    else {
        $httpStatusCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        if (200 !== $httpStatusCode) {
            throw new Exception($reponse,$httpStatusCode);
        }
    }
    curl_close($ch);
    return $reponse;
}
function microtime_float(){
    list ($usec, $sec) = explode(" ", microtime());
    return ((float) $usec + (float) $sec);
}

?>
