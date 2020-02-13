package com.pt.hadoop.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

public class KerberosUtil {

    /**
     * 通过Kerberos认证用户的,注意keytabPath为本地路径不是HDFS路径
     *
     * @param conf
     * @param user       user为运行jar的hadoop用户
     * @param keytabPath
     * @throws IOException
     */
    public static void AuthenByKerberos(Configuration conf, String user, String keytabPath) throws IOException {
        UserGroupInformation.setConfiguration(conf);
        if (!UserGroupInformation.isSecurityEnabled())
            return;
        UserGroupInformation.getCurrentUser().setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.KERBEROS);
        UserGroupInformation.loginUserFromKeytab(user, keytabPath);
    }

    /**
     * 通过Kerberos认证用户的,注意keytabPath为本地路径不是HDFS路径
     *
     * @param conf
     * @param keytabPath
     * @throws IOException
     */
    public static void AuthenByKerberos(Configuration conf, String keytabPath) throws IOException {
        String user = UserGroupInformation.getLoginUser().getUserName();
        AuthenByKerberos(conf, user, keytabPath);
    }
}
