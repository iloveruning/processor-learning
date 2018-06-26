package com.github.llchen.processing;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author llchen12
 * @date 2018/6/26
 */
public class Parse {

    private static final int STATUS_COMMENT = 0;
    private static final int STATUS_PARAM = 1;
    private static final int STATUS_RETURN = 2;
    private static final int STATUS_DEMORESPONSE = 3;
    private static final int STATUS_OTHER=4;

    private static final String SPACE_STRING = " ";
    private static final String NEWLINE_STRING = "\n";
    private static final String SEPARATE_STRING = "@";
    private static final String PARAM_STRING = "@param";
    private static final String RETURN_STRING = "@return";
    private static final String DEMORESPONSE_STRING = "@demoResponse";


    private Map<String,AnnotatedClass> docClassMap=new HashMap<>(64);


    public Parse(String json) {
        parseJson(json);
    }

    private void parseJson(String json) {
        JSONObject jsonObj=JSONObject.parseObject(json);
        for (String key:jsonObj.keySet()){
            try {
                AnnotatedClass aClass = jsonObj.getObject(key, AnnotatedClass.class);
                docClassMap.put(key,aClass);
            }catch (Exception e){
                System.err.println("解析json异常:"+e.getMessage());
            }
        }
    }

    public AnnotatedClass getAnnotatedClass(String className) {
        return this.docClassMap.get(className);
    }

    public AnnotatedClass getAnnotatedClass(Class<?> clazz) {
        return getAnnotatedClass(clazz.getCanonicalName());
    }

    public AnnotatedMethod getAnnotatedMethod(String className, String methodName) {
        AnnotatedClass aClass = getAnnotatedClass(className);
        if (aClass != null) {
            List<AnnotatedMethod> methods = aClass.getMethods();
            for (AnnotatedMethod method : methods) {
                if (method.getMethodName().equals(methodName)) {
                    return method;
                }
            }
        }
        return null;
    }

    public Comment getMethodComment(String className, String methodName) {
        return parseDoc(getAnnotatedMethod(className, methodName).getDoc());
    }


    public static Comment parseDoc(String doc) {
        Comment comment = new Comment();
        if (doc == null || "".equals(doc.trim())) {
            return comment;
        }
        String[] str = doc.split(NEWLINE_STRING);
        int status = STATUS_COMMENT;
        String temp = "";
        for (String line : str) {
            line = line.trim();
            if ("".equals(line)) {
                continue;
            }
            if (line.startsWith(SEPARATE_STRING)) {
                int i = line.indexOf(SPACE_STRING);
                String sp = line.substring(i + 1).trim();
                if (line.startsWith(PARAM_STRING)) {
                    status = STATUS_PARAM;
                    int dx = sp.indexOf(SPACE_STRING);
                    String param = sp.substring(0, dx);
                    String desc = sp.substring(dx + 1).trim();
                    comment.addParam(param, desc);
                    temp = param;
                } else if (line.startsWith(RETURN_STRING)) {
                    status = STATUS_RETURN;
                    comment.setRet(sp);
                } else if (line.startsWith(DEMORESPONSE_STRING)) {
                    status = STATUS_DEMORESPONSE;
                    comment.setDemoResponse(sp);
                }else {
                    status=STATUS_OTHER;
                }
            } else {
                switch (status) {
                    case STATUS_COMMENT:
                        String cmt = comment.getComment() + SPACE_STRING + line;
                        comment.setComment(cmt.trim());
                        break;
                    case STATUS_PARAM:
                        String desc = comment.getParam(temp) + SPACE_STRING + line;
                        comment.addParam(temp, desc.trim());
                        break;
                    case STATUS_DEMORESPONSE:
                        String dr = comment.getDemoResponse() + SPACE_STRING + line;
                        comment.setDemoResponse(dr.trim());
                        break;
                    case STATUS_RETURN:
                        String ret = comment.getRet() + SPACE_STRING + line;
                        comment.setRet(ret.trim());
                        break;
                    default:
                        break;
                }
            }
        }
        return comment;
    }

    public static void main(String[] args) {
        System.out.println(parseDoc(" 导航菜单\n @demoResponse {djjdjd}\n @return hdhdhd\n\n"));


    }


    public static String doc = "Returns the leading surrogate (a\n" +
            "     <a href=\"http://www.unicode.org/glossary/#high_surrogate_code_unit\">\n" +
            "     high surrogate code unit</a>) of the\n" +
            "     <a href=\"http://www.unicode.org/glossary/#surrogate_pair\">\n" +
            "     surrogate pair</a>\n" +
            "     representing the specified supplementary character (Unicode\n" +
            "     code point) in the UTF-16 encoding.  If the specified character\n" +
            "     is not a\n" +
            "     <a href=\"Character.html#supplementary\">supplementary character</a>,\n" +
            "     an unspecified {@code char} is returned.\n" +
            "     \n" +
            "     <p>If\n" +
            "     {@link #isSupplementaryCodePoint isSupplementaryCodePoint(x)}\n" +
            "     is {@code true}, then\n" +
            "     {@link #isHighSurrogate isHighSurrogate}{@code (highSurrogate(x))} and\n" +
            "     {@link #toCodePoint toCodePoint}{@code (highSurrogate(x), }{@link #lowSurrogate lowSurrogate}{@code (x)) == x}\n" +
            "     are also always {@code true}.\n" +
            "     \n" +
            "     @param   codePoint a supplementary character (Unicode code point)\n" +
            "     @return  the leading surrogate code unit used to represent the\n" +
            "               character in the UTF-16 encoding\n" +
            "     @since   1.7";
}
