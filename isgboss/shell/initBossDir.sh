#!/bin/sh

home=/data/msplogs
mkdir -p $home/logs/t0
mkdir -p $home/logs/t1
mkdir -p $home/logs/t2
mkdir -p $home/logs/t22
mkdir -p $home/logs/boss
mkdir -p $home/logs/virus
mkdir -p $home/logs/cdrtxt
mkdir -p $home/logs/kpi

mkdir -p $home/virus
mkdir -p $home/boss

mkdir -p $home/bakup/msp
mkdir -p $home/bakup/t0
mkdir -p $home/bakup/t1
mkdir -p $home/bakup/t2
mkdir -p $home/bakup/t22

mkdir -p $home/kpi/t0/1
mkdir -p $home/kpi/t0/2
mkdir -p $home/kpi/t0/3
mkdir -p $home/kpi/t0/4

mkdir -p $home/kpi/ua/backup
mkdir -p $home/kpi/ua/work
mkdir -p $home/kpi/ua/history

mkdir -p $home/kpi/t1
mkdir -p $home/kpi/t2
mkdir -p $home/kpi/t22/1
mkdir -p $home/kpi/t22/2

mkdir -p $home/bad/t0
mkdir -p $home/bad/t1
mkdir -p $home/bad/t2
mkdir -p $home/bad/t22


chown -Rh ecgwuser $home/logs/virus
chown -Rh ecgwuser $home/logs/boss
