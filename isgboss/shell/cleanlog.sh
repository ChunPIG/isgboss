bakup_home=/data/msplogs/bakup
msp_home=$bakup_home/msp
t0_home=$bakup_home/t0
t1_home=$bakup_home/t1
t2_home=$bakup_home/t2
t22_home=$bakup_home/t22
boss_home=/data/msplogs/boss
virus_home=/data/msplogs/virus
cdrtxt_home=/data/msplogs/cdrtxt
bad_home=/data/msplogs/bad

#rm mspfile before 120minute
find ${msp_home} -amin +1440 -exec rm {} \;
find ${t0_home} -amin +1440 -exec rm {} \;
find ${t1_home} -amin +1440 -exec rm {} \;
find ${t2_home} -amin +1440 -exec rm {} \;
find ${t22_home} -amin +1440 -exec rm {} \;
find ${boss_home} -amin +1440 -exec rm {} \;
find ${virus_home} -amin +1440 -exec rm {} \;
find ${cdrtxt_home} -amin +180 -exec rm {} \;
find ${bad_home} -amin +14400 -exec rm {} \;


shelllogs=/opt/isg/boss/shell/shell-logs
find ${shelllogs} -amin +600 -exec rm {} \;
