package com.ziroom.qa.quality.defende.provider.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/24 5:05 下午
 */
public class JsonPathTest {

    /**
     * {
     *     "store": {
     *         "book": [
     *             {
     *                 "category": "reference",
     *                 "author": "Nigel Rees",
     *                 "title": "Sayings of the Century",
     *                 "price": 8.95
     *             },
     *             {
     *                 "category": "fiction",
     *                 "author": "Evelyn Waugh",
     *                 "title": "Sword of Honour",
     *                 "price": 12.99
     *             },
     *             {
     *                 "category": "fiction",
     *                 "author": "Herman Melville",
     *                 "title": "Moby Dick",
     *                 "isbn": "0-553-21311-3",
     *                 "price": 8.99
     *             },
     *             {
     *                 "category": "fiction",
     *                 "author": "J. R. R. Tolkien",
     *                 "title": "The Lord of the Rings",
     *                 "isbn": "0-395-19395-8",
     *                 "price": 22.99
     *             }
     *         ],
     *         "bicycle": {
     *             "color": "red",
     *             "price": 19.95
     *         }
     *     },
     *     "expensive": 10
     * }
     * @param args
     */

    public static void main(String[] args) {
        String jsonStr = "{\n" +
                "    \"store\": {\n" +
                "        \"book\": [\n" +
                "            {\n" +
                "                \"category\": \"reference\",\n" +
                "                \"author\": \"Nigel Rees\",\n" +
                "                \"title\": \"Sayings of the Century\",\n" +
                "                \"price\": 8.95\n" +
                "            },\n" +
                "            {\n" +
                "                \"category\": \"fiction\",\n" +
                "                \"author\": \"Evelyn Waugh\",\n" +
                "                \"title\": \"Sword of Honour\",\n" +
                "                \"price\": 12.99\n" +
                "            },\n" +
                "            {\n" +
                "                \"category\": \"fiction\",\n" +
                "                \"author\": \"Herman Melville\",\n" +
                "                \"title\": \"Moby Dick\",\n" +
                "                \"isbn\": \"0-553-21311-3\",\n" +
                "                \"price\": 8.99\n" +
                "            },\n" +
                "            {\n" +
                "                \"category\": \"fiction\",\n" +
                "                \"author\": \"J. R. R. Tolkien\",\n" +
                "                \"title\": \"The Lord of the Rings\",\n" +
                "                \"isbn\": \"0-395-19395-8\",\n" +
                "                \"price\": 22.99\n" +
                "            }\n" +
                "        ],\n" +
                "        \"bicycle\": {\n" +
                "            \"color\": \"red\",\n" +
                "            \"price\": 19.95\n" +
                "        }\n" +
                "    },\n" +
                "    \"expensive\": 10\n" +
                "}";
        JSONObject jsObject = (JSONObject)JSON.parse(jsonStr);
        System.out.println(JSONPath.read(jsonStr,"$..book"));
    }
}
