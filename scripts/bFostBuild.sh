cd server
ant compile_core
ant compile_plugins
ant runserver &
cd ../client
ant compile
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/lib/x86_64-linux-gnu/jni/
ant runclient
