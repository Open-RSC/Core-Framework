using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace RSCXNALib
{
    public class Config
    {
        public static String CONF_DIR = "./Resources/";
		public static String MEDIA_DIR = "./Resources/";

        public static int CLIENT_VERSION = 40;
        public static bool MEMBERS_FEATURES = true;

        public static String SERVER_IP = "127.0.0.1";//83.248.5.67
        public static int SERVER_PORT = 43595;

        public static String CACHE_URL = "http://216.24.201.81/cache/";
        public static String CRASH_URL = "http://216.24.201.81/crash.php";
    }
}
