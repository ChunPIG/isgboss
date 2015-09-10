export ORACLE_BASE=/opt/oracle
export ORACLE_HOME=$ORACLE_BASE/product/11gR1/db
export ORACLE_SID=ECGWORAC
export PATH=$PATH:$ORACLE_HOME/bin
export LD_LIBRARY_PATH=$ORACLE_HOME/lib:/lib:/usr/lib
oracle_userid=isg_log/isgpwd@ecgworac 

echo "ORACLE_HOME="$ORACLE_HOME
echo "oracle_userid="$oracle_userid
