upstream med_servers {
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
    proxy_pass http://med_servers;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}