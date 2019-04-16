#!/bin/sh

set -eu

echo "Compiling mbedtls"
(cd mbedtls && make no_test)

echo "Compiling zlib"
(cd zlib && CFLAGS=-fPIC ./configure --static && make)

# Waterfall - rewrite below to extend platform support

if [[ "$OSTYPE" == "darwin"* ]]; then
  PREFIX="osx-"
  CXX_ARGS="-I$JAVA_HOME/include/ -I$JAVA_HOME/include/darwin/"
else
  CXX_ARGS="-I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/ -Wl,--wrap=memcpy"
fi

CXX="g++ -shared -fPIC -O3 -Wall -Werror"

$CXX -Imbedtls/include src/main/c/NativeCipherImpl.cpp -o src/main/resources/${PREFIX:-}native-cipher.so mbedtls/library/libmbedcrypto.a $CXX_ARGS
$CXX -Izlib src/main/c/NativeCompressImpl.cpp -o src/main/resources/${PREFIX:-}native-compress.so zlib/libz.a $CXX_ARGS
