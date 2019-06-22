package io.kurumi.fuckmfs;

import com.sollyu.android.appenv.helper.RandomHelper;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.BufferedOutputStream;
import com.sollyu.android.appenv.utils.IMEIGen;
import java.util.LinkedList;
import com.sollyu.android.appenv.helper.PhoneHelper;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

public class FuckMFS implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam pkg) throws Throwable {

        if (!"com.Android.MFSocket".equals(pkg.packageName)) {

            return;

        }

        // 阻止SHELL执行

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.ShellUtils", false, pkg.classLoader),
            "execCommand",
            String[].class, boolean.class, boolean.class,
            new XC_MethodReplacement() {

                @Override
                protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam p1) throws Throwable {

                    return Class
                        .forName("com.Android.MFSocket.ShellUtils$CommandResult")
                        .getConstructor(int.class, String.class, String.class)
                        .newInstance(-1, null, null);

                }

            });

        XC_MethodReplacement METHOD_EMPTY = new XC_MethodReplacement() {

            @Override
            protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam p1) throws Throwable {

                return null;

            }

        };

        // 阻止读取联系人

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.ContactsMsgOS20", false, pkg.classLoader),
            "getContactOS20",
            BufferedOutputStream.class, StringBuffer.class, int.class,
            METHOD_EMPTY);

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.ContactsSIM", false, pkg.classLoader),
            "getContact",
            BufferedOutputStream.class, StringBuffer.class, int.class,
            METHOD_EMPTY);

        // 应用信息

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.AppMsg", false, pkg.classLoader),
            "GetAppMsg",
            StringBuffer.class,
            METHOD_EMPTY);

        // 音频 / 视频

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.AudioMsg", false, pkg.classLoader),
            "getAudio",
            BufferedOutputStream.class, StringBuffer.class, int.class,
            METHOD_EMPTY);

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.VideoMsg", false, pkg.classLoader),
            "getVideo",
            BufferedOutputStream.class, StringBuffer.class, int.class,
            METHOD_EMPTY);
        
            
        // 蓝牙

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.BtMsg", false, pkg.classLoader),
            "getBtInfo",
            StringBuffer.class,
            METHOD_EMPTY);

        // 定位

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.GpsMsg", false, pkg.classLoader),
            "getGpsInfo",
            StringBuffer.class,
            METHOD_EMPTY);

        // 相册

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.ImageMsg", false, pkg.classLoader),
            "getImage",
            BufferedOutputStream.class, StringBuffer.class, int.class,
            METHOD_EMPTY);


        // 短信

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.SmsMsg", false, pkg.classLoader),
            "getSms",
            BufferedOutputStream.class, StringBuffer.class, int.class,
            METHOD_EMPTY);

        // 文件Hash

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.MD5Msg", false, pkg.classLoader),
            "fileMD5",
            String.class,
            new XC_MethodReplacement() {

                @Override
                protected String replaceHookedMethod(XC_MethodHook.MethodHookParam p1) throws Throwable {

                    return "1145141919810FDB1471EF51EC3A32CD";

                }
            });
            
            

        String[] networkOperator = new String[] {

            "46000","46002", // 移动
            "46001", // 联通
            "46003" , // 电信

        };

        final String no = networkOperator[RandomHelper.getInstance().randomInt(0, 4)];

        try {

            StringBuilder json = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(pkg.classLoader.getResourceAsStream("assets/phone.json")));

            String line;

            while ((line = reader.readLine()) != null) {

                json.append(line + "\n");

            }

            PhoneHelper.getInstance().setPhoneJsonObject(new JSONObject(json.toString()));

        } catch (Exception ex) {}

        XposedHelpers.findAndHookMethod(
            Class.forName("com.Android.MFSocket.SystemMsg", false, pkg.classLoader),
            "getSystemMsg",
            StringBuffer.class,
            new XC_MethodReplacement() {

                @Override
                protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam params) throws Throwable {

                    StringBuffer sysMsg =  (StringBuffer) params.args[0];

                    RecordPackage.AppendItem("STAT", sysMsg);
                    RecordPackage.AppendItem(114514, sysMsg);
                    RecordPackage.AppendRecord(sysMsg);
                    RecordPackage.AppendItem("IMEI", sysMsg);
                    RecordPackage.AppendItem(RandomHelper.getInstance().randomTelephonyGetDeviceId(), sysMsg);
                    RecordPackage.AppendRecord(sysMsg);
                    RecordPackage.AppendItem("IMSI", sysMsg);
                    RecordPackage.AppendItem(RandomHelper.getInstance().randomSimSubscriberId(), sysMsg);
                    RecordPackage.AppendRecord(sysMsg);
                    RecordPackage.AppendItem("IMOS", sysMsg);
                    RecordPackage.AppendItem(RandomHelper.getInstance().randomBuildVersionName(), sysMsg);
                    RecordPackage.AppendRecord(sysMsg);
                    RecordPackage.AppendItem("INET", sysMsg);
                    RecordPackage.AppendItem(no, sysMsg);
                    RecordPackage.AppendRecord(sysMsg);
                    RecordPackage.AppendItem("ISIM", sysMsg);
                    RecordPackage.AppendItem(RandomHelper.getInstance().randomTelephonySimSerialNumber(), sysMsg);
                    RecordPackage.AppendRecord(sysMsg);
                    try {
                        RecordPackage.AppendItem("WIFIMAC", sysMsg);
                        RecordPackage.AppendItem(RandomHelper.getInstance().randomWifiInfoMacAddress(), sysMsg);
                        RecordPackage.AppendRecord(sysMsg);
                        RecordPackage.AppendItem("BTMAC", sysMsg);
                        RecordPackage.AppendItem(RandomHelper.getInstance().randomWifiInfoMacAddress(), sysMsg);
                        RecordPackage.AppendRecord(sysMsg);
                        RecordPackage.AppendItem("OWNER", sysMsg);
                        RecordPackage.AppendItem(RandomHelper.getInstance().randomString(RandomHelper.getInstance().randomInt(1, 11), true, true, true), sysMsg);
                        RecordPackage.AppendRecord(sysMsg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String mf = "HUAWEI";
                    String name = "P10";
                    String model = "VTR-AL00";


                    try {


                        ArrayList<String> mfList = PhoneHelper.getInstance().getManufacturerList();

                        mf = mfList.get(RandomHelper.getInstance().randomInt(0, mfList.size()));

                        HashMap<String, String> modelList = PhoneHelper.getInstance().getModelList(mf);


                        name = (String) modelList.keySet().toArray()[RandomHelper.getInstance().randomInt(0, modelList.size())];
                        model = modelList.get(name);

                    } catch (Exception ex) {}

                    RecordPackage.AppendItem("MODEL", sysMsg);
                    RecordPackage.AppendItem(model, sysMsg);
                    RecordPackage.AppendRecord(sysMsg);
                    RecordPackage.AppendItem("MANUFACTURE", sysMsg);
                    RecordPackage.AppendItem(mf, sysMsg);
                    RecordPackage.AppendRecord(sysMsg);
                    RecordPackage.AppendItem("RELEASE", sysMsg);
                    RecordPackage.AppendItem(RandomHelper.getInstance().randomBuildVersionName(), sysMsg);
                    RecordPackage.AppendRecord(sysMsg);
                    RecordPackage.AppendItem("NAME", sysMsg);
                    RecordPackage.AppendItem(name, sysMsg);
                    RecordPackage.AppendRecord(sysMsg);

                    return null;

                }
            });

    }

    public static class RecordPackage {
        
        public static void AppendItem(double paramDouble, StringBuffer paramStringBuffer) {
            paramStringBuffer.append(paramDouble);
            paramStringBuffer.append("#/}.[*#");
        }

        public static void AppendItem(int paramInt, StringBuffer paramStringBuffer) {
            paramStringBuffer.append(paramInt);
            paramStringBuffer.append("#/}.[*#");
        }

        public static void AppendItem(long paramLong, StringBuffer paramStringBuffer) {
            paramStringBuffer.append(paramLong);
            paramStringBuffer.append("#/}.[*#");
        }

        public static void AppendItem(String paramString, StringBuffer paramStringBuffer) {
            paramStringBuffer.append(paramString);
            paramStringBuffer.append("#/}.[*#");
        }

        public static void AppendRecord(StringBuffer paramStringBuffer) {
            paramStringBuffer.append("$/}.[*$");
        }

    }


}
