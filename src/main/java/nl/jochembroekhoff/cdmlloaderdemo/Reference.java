package nl.jochembroekhoff.cdmlloaderdemo;

/**
 * @author Jochem Broekhoff
 */
public class Reference {
    public static final String MOD_ID = "cdmlloader";
    public static final String MOD_NAME = "CDMLLoader";
    public static final String MOD_VERSION = "0.0.1";
    public static final String MC_VERSION = "[1.12.2]";
    public static final String DEPENDS = "required-after:cdm@[0.2.0,)";

    public class Proxy {
        public static final String CLIENT_SIDE = "nl.jochembroekhoff.cdmlloaderdemo.proxy.ClientProxy";
        public static final String SERVER_SIDE = "nl.jochembroekhoff.cdmlloaderdemo.proxy.ServerProxy";
        public static final String COMMON = "nl.jochembroekhoff.cdmlloaderdemo.proxy.CommonProxy";
    }
}
