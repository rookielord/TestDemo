package com.zhd.hi_test.util;

import android.util.Xml;

import com.zhd.hi_test.module.UpdateBean;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * Created by 2015032501 on 2015/10/20.
 */
public class XmlParseUtil {
    /**
     * 解析xml
     *
     * @param inputStream
     * @return
     */
    public static UpdateBean getUpdataInfo(InputStream inputStream) {
        XmlPullParser parser = Xml.newPullParser();//启动xml解析器
        UpdateBean bean = new UpdateBean();//创建对象
        try {
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("version".equals(parser.getName())) {
                            bean.setVersion(parser.nextText());
                        } else if ("description".equals(parser.getName())) {
                            bean.setDes(parser.nextText());
                        } else if ("apkurl".equals(parser.getName())) {
                            bean.setURL(parser.nextText());
                        }
                        break;
                }
                type = parser.next();
            }
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
