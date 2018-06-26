package com.github.llchen.processing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @author llchen12
 * @date 2018/6/26
 */
public class Test {


    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream("D:\\ideaworkspace\\processor-learning\\src\\main\\resources\\doc-comment.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        fis.close();
        System.out.println(sb.toString());
        JSONObject jsonArray = JSON.parseObject(sb.toString());

        String doc = jsonArray.getJSONObject("com.github.llchen.utils.Test").getString("doc");

        String[] split = doc.split("\n");

        System.out.println(Arrays.toString(split));

        JSONArray methods = jsonArray.getJSONObject("com.github.llchen.utils.Test").getJSONArray("methods");

        String doc1 = methods.getJSONObject(0).getString("doc");

        System.out.println(doc1);

        String[] ss = doc1.split("\n");
        System.out.println(Arrays.toString(ss));


    }
}
