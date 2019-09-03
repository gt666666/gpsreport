package com.ynzhongxi.gpsreport.filter;

import com.ynzhongxi.gpsreport.utils.PatternUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author lixingwu
 */
public class XssCodeWrapper extends HttpServletRequestWrapper {
    //判断是否是上传 上传忽略
    boolean isUpData = false;
    //是否严格模式
    boolean isStrict = false;

    public XssCodeWrapper(HttpServletRequest request) {
        super(request);
        String contentType = request.getContentType();
        if (null != contentType) {
            isUpData = contentType.startsWith("multipart");
        }
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (isUpData) {
            return super.getInputStream();
        } else {

            final ByteArrayInputStream bais = new ByteArrayInputStream(inputHandlers(super.getInputStream()).getBytes());

            return new ServletInputStream() {

                @Override
                public int read() throws IOException {
                    return bais.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }
            };
        }
    }

    private String inputHandlers(ServletInputStream servletInputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(servletInputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (servletInputStream != null) {
                try {
                    servletInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cleanXSS(sb.toString());
    }

    private String cleanXSS(String value) {
        if (value != null) {
            value = value.replaceAll("", "");
            value = PatternUtils.matchScripts.matcher(value).replaceAll("");
            value = PatternUtils.matchScriptEnd.matcher(value).replaceAll("");
            value = PatternUtils.matchScriptBegin.matcher(value).replaceAll("");
            value = PatternUtils.matchEval.matcher(value).replaceAll("");
            value = PatternUtils.matchJavascript.matcher(value).replaceAll("");
            value = PatternUtils.matchVbscript.matcher(value).replaceAll("");
            value = PatternUtils.matchOnload.matcher(value).replaceAll("");
            if (isStrict) {
                value = PatternUtils.matchSrcSingle.matcher(value).replaceAll("");
                value = PatternUtils.matchSrcDouble.matcher(value).replaceAll("");
                value = value.replaceAll("'", "&#39;");
                value = value.replaceAll("/", "&#x2f;");
                value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                value = value.replaceAll("%3C", "&lt;").replaceAll("%3E", "&gt;");
                value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
                value = value.replaceAll("%28", "&#40;").replaceAll("%29", "&#41;");
            }
        }
        return value;

    }
}
