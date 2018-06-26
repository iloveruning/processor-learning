package com.github.llchen.processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author llchen12
 * @date 2018/6/26
 */
public class Test {


    public static void main(String[] args) throws IOException {
        InputStream is=Test.class.getResourceAsStream("/META-INF/doc-comment.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        is.close();

        Parse parse=new Parse(sb.toString());
        System.out.println(parse.getMethodComment("com.hfutonline.mly.modules.sys.controller.SysUserController","info(userId)"));
    }
}
