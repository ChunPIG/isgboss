修改/etc/crontab文件
在文件未，增加如下信息：
#每2分钟执行一次logrotate
*/2 * * * *   root  /usr/sbin/logrotate /etc/logrotate.conf >/dev/null 2>&1
*/1 * * * *   root  /usr/shell/gwfirelog.sh >/dev/null 2>&1



使用命令service cron restart重启cron service.