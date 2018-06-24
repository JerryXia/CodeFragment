upstream med_fosunholiday_com_servers {
    server 10.27.163.59:8083 down;
    server 10.27.163.160:8083 down;
    server 10.27.163.181:8083 down;
    server 10.27.163.73:8083 weight=2;
}

server {
    listen 80;
    server_name med.fosunholiday.com;
    access_log /var/log/nginx/med.fosunholiday.com.access.log;
    error_log  /var/log/nginx/med.fosunholiday.com.error.log warn; 
    location / {
        proxy_pass http://med_fosunholiday_com_servers;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}

server {
    listen 443; 
    server_name med.fosunholiday.com;
    access_log /var/log/nginx/med.fosunholiday.com.access.log;
    error_log  /var/log/nginx/med.fosunholiday.com.error.log warn;
    
    ssl on;
    ssl_certificate  /etc/nginx/cert/214470612780424.pem;
    ssl_certificate_key  /etc/nginx/cert/214470612780424.key;
    ssl_session_timeout 5m;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
    ssl_prefer_server_ciphers on;
    
    location / {
        proxy_pass http://med_fosunholiday_com_servers;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}