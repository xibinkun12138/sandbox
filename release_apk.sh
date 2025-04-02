./gradlew clean

./gradlew app:assembleRelease --stacktrace

cat app/gradle.properties
. app/gradle.properties

apk_32_dir=apks/$app_versionName/SandBox32/release
apk_64_dir=apks/$app_versionName/SandBox64/release
echo $apk_32_dir
mkdir -p $apk_32_dir
mkdir -p $apk_64_dir

./walle/run_sign.sh app/build/outputs/apk/SandBox64/release/app-SandBox64-release.apk
./walle/run_sign.sh app/build/outputs/apk/SandBox32/release/app-SandBox32-release.apk

cp app/build/outputs/apk/SandBox64/release/*.apk $apk_64_dir
cp app/build/outputs/apk/SandBox32/release/*.apk $apk_32_dir
