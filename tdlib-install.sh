MAIN_DIR=$PWD
git clone https://github.com/tdlib/td.git
cd td
rm -rf example/java/org
cd example
cd java
mkdir -p ru
cd ru
mkdir -p ilyasok
cd ilyasok
mkdir -p StickKs
cd StickKs
mkdir -p tdapi
echo $MAIN_DIR/td
cd $MAIN_DIR/td
cp $MAIN_DIR/src/main/kotlin/ru/ilyasok/StickKs/tdapi/Client.java $MAIN_DIR/src/main/kotlin/ru/ilyasok/StickKs/tdapi/TdApi.java -t example/java/ru/ilyasok/StickKs/tdapi
rm -rf build
mkdir build
cd build
cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX:PATH=../example/java/td -DTD_ENABLE_JNI=ON ..
cmake --build . --target install
cd ..
cd example/java
sed -i 's#org/drinkless/tdlib#ru/ilyasok/StickKs/tdapi#g' CMakeLists.txt
sed -i 's#org.drinkless.tdlib#ru.ilyasok.StickKs.tdapi#g' CMakeLists.txt
sed -i 's#${JAVA_SOURCE_PATH}/example/Example.java##g' CMakeLists.txt
rm -rf build
mkdir build
cd build
cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX:PATH=../../../tdlib -DTd_DIR:PATH=$(readlink -e ../td/lib/cmake/Td) ..
cmake --build . --target install
