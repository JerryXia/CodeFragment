#!/bin/bash

#MYSQL_USER="dbUserName"
#MYSQL_PASS="dbPwd"

baidupan_DIR="/backup/$(date +%Y-%m-%d)"
BACK_DIR="bdbackup"

# site dir to backup
BACKUP_DEFAULT="/var/www"
NGINX_DATA="/usr/jexus/siteconf"

# backup fileName
#mysql_DATA=mysql_$(date +"%Y%m%d").tar.gz
www_DEFAULT=default_$(date +%Y%m%d).tar.gz
nginx_CONFIG=nginx_$(date +%Y%m%d).tar.gz

# if not exists  then create
if [ ! -d $BACK_DIR ] ;
  then
   mkdir -p "$BACK_DIR"
fi

cd $BACK_DIR

# 备份所有数据库, 导出需要备份的数据库，清除不需要备份的库
#mysql -u$MYSQL_USER -p$MYSQL_PASS -B -N -e 'SHOW DATABASES' > databases.db
#sed -i '/performance_schema/d' databases.db
#sed -i '/information_schema/d' databases.db
#sed -i '/mysql/d' databases.db

#for db in $(cat databases.db)
# do
#   mysqldump -u$MYSQL_USER -p$MYSQL_PASS ${db} | gzip -9 - > ${db}.sql.gz
#done

# zip
#tar -zcvf $mysql_DATA *.sql.gz

# zip site
tar -zcvf $www_DEFAULT $BACKUP_DEFAULT

# zip nginx config file
tar -zcvf $nginx_CONFIG $NGINX_DATA/*.*

# upload
cd ~
/home/azureuser/bpcs_uploader.php upload $BACK_DIR/$nginx_CONFIG $baidupan_DIR/$nginx_CONFIG
#/home/azureuser/bpcs_uploader.php upload $BACK_DIR/$mysql_DATA $baidupan_DIR/$mysql_DATA
/home/azureuser/bpcs_uploader.php upload $BACK_DIR/$www_DEFAULT $baidupan_DIR/$www_DEFAULT

# Delete all local backup
rm -rf $BACK_DIR

exit 0
