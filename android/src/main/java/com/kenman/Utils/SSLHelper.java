package com.kenman.Utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.kenman.Utils.OkHttpUtils;

public class SSLHelper {

    public SSLHelper() {
    }

    public SSLSocketFactory provideSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            //第一个参数：表示服务端对客户端的验证，如果不验证，可直接传递null 
            // provideKeyManagerArray()
            //第二个参数：表示客户端对服务端的验证，如果不验证，可以直接传null
            // provideTrustManagerArray()
            sslContext.init(provideKeyManagerArray(), null, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    //二、服务端对客户端的校验
    public KeyManager[] provideKeyManagerArray() {
        try {
            //1.生成客户端证书的流对象
            InputStream mClientInputStream  = new BufferedInputStream(OkHttpUtils.class.getClassLoader().getResourceAsStream("assets/client.bks"));//context.getAssets().open("证书名称");
            //2.客户端证书的密钥库对象并初始化
            KeyStore mClientKeyStore = KeyStore.getInstance("BKS");
            char password[] = "123456".toCharArray();
            mClientKeyStore.load(mClientInputStream, password);
            if (mClientInputStream != null) {
                mClientInputStream.close();
            }
            //3.初始化密钥库管理工厂类，这里没有将客户端的证书密钥库添加到信任管理中，原因是没有必要
            //因为这个证书信不信任是服务器说的算的
            //不同于客户端对服务器端的校验，客户端需要将服务端的证书放到自己的信任管理器中这样才能信任服务端
            KeyManagerFactory mKeyManagerFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            mKeyManagerFactory.init(mClientKeyStore,password);
            return mKeyManagerFactory.getKeyManagers();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return new KeyManager[0];
    }

    public X509TrustManager provideX509TrustManager() {
        return tm;
    }

    private TrustManager[] provideTrustManagerArray() {
        TrustManager[] trustManagers = new TrustManager[1];
        trustManagers[0] = tm;
        return trustManagers;
    }

    private X509TrustManager tm = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            Log.d("WH","checkClientTrusted");
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            try {
                // 以下代码也可用
//                1. 获取客户端预埋的服务器端的证书对象
                InputStream mCaInputStream  = new BufferedInputStream(OkHttpUtils.class.getClassLoader().getResourceAsStream("assets/server.crt")); //context.getAssets().open("证书名称");

//                2.生成符合x509标准的证书
                CertificateFactory mCertificateFactory = CertificateFactory.getInstance("X.509");
                X509Certificate certificate =
                        (X509Certificate) mCertificateFactory.generateCertificate(mCaInputStream);
                if (mCaInputStream != null) {
                    mCaInputStream.close();
                }

//                3.将证书导入到本地的证书密钥库中去
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                //导入需要解密的数据流 ，以及解密的秘钥
                keyStore.load(null, null);
                keyStore.setCertificateEntry("server", certificate);

//                4.使用本地密钥库初始化信任管理器中去
                TrustManagerFactory trustManagerFactory
                        = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
//                5.使用信任管理器得到X509TrustManager
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                //这个位置，我直接取了数组的第一个元素，貌似不妥。
                X509TrustManager x509TrustManager = (X509TrustManager) trustManagers[0];
//                6.使用X509TrustManager校验服务端的证书，此方法不报异常即使校验成功
//                异常：CertPathValidatorException
                x509TrustManager.checkServerTrusted(chain, authType);
            } catch (Exception e) {
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            //这里如果传空会报异常
            return new X509Certificate[0];
        }
    };

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }
}
