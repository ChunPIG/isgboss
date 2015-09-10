boss项目编译步骤：
1、编译cdr-jibx.jar
ant -f ./jibx-compile/build.xml jar
2、编译打包项目
ant tar.gz

编译后的文件存放在 install