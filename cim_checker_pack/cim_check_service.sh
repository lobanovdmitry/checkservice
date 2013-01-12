lib=./lib

java -cp \
$lib/cim-check-1.0.1.jar:\
$lib/protobuf-java-2.4.1.jar:\
$lib/protobuf-proxy-1.0.jar:\
$lib/sblim-cim-client2-2.0.9.3.jar:\
$lib/xercesImpl-2.7.0.jar\
 com.wbem.checker.kernel.CheckServer --config server_cfg.xml
