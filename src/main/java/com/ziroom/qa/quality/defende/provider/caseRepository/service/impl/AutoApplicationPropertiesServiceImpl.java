package com.ziroom.qa.quality.defende.provider.caseRepository.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoApplicationProperties;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApi;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.AutoApplicationPropertiesDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo.AutoApplicationPropertiesVo;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo.SingleApiStorageVo;
import com.ziroom.qa.quality.defende.provider.caseRepository.mapper.AutoApplicationPropertiesMapper;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoApplicationPropertiesService;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiCaseService;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.OmegaService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.WeChatService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.util.DateUtil;
import com.ziroom.qa.quality.defende.provider.util.ExampleBuilder;
import com.ziroom.qa.quality.defende.provider.util.SwaggerJsonUtils;
import com.ziroom.qa.quality.defende.provider.util.swagger.models.Example;
import com.ziroom.qa.quality.defende.provider.util.swagger.processors.JsonNodeExampleSerializer;
import com.ziroom.qa.quality.defende.provider.vo.MatrixUserDetail;
import io.swagger.models.Model;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-10-12
 */
@Service
@Slf4j
public class AutoApplicationPropertiesServiceImpl extends ServiceImpl<AutoApplicationPropertiesMapper, AutoApplicationProperties> implements IAutoApplicationPropertiesService {
    @Autowired
    private IAutoSingleApiService autoSingleApiService;
    @Autowired
    private IAutoSingleApiCaseService autoSingleApiCaseService;
    @Autowired
    private OmegaService omegaService;
    @Autowired
    private MatrixService matrixService;
    @Autowired
    private WeChatService weChatService;
    @Resource
    private RestTemplate restTemplate;

    @Override
    public AutoApplicationPropertiesVo find(AutoApplicationPropertiesDto dto) {
        QueryWrapper<AutoApplicationProperties> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_name", dto.getApplicationName())
                .eq("env", dto.getEnv())
                .eq("deleted", 0);
        List<AutoApplicationProperties> list = this.list(queryWrapper);
        if (list.size() <= 0)
            return null;
        else {
            AutoApplicationProperties autoApplicationProperties = list.get(0);
            AutoApplicationPropertiesVo vo = new AutoApplicationPropertiesVo();
            BeanUtils.copyProperties(autoApplicationProperties, vo);
            return vo;
        }
    }

    @Override
    public String getApplicationName(String domain) {
        return omegaService.getApplicationNameByDomain(domain);
    }

    @Override
    public Integer synchronizeApiByUrl(String userCode, String applicationName, String domain, String env) {
        //        ?????????????????????
        return importByDomain(applicationName, domain, env);
    }

    @Override
    public Integer synchronizeApiByApp(String applicationName, String env) {
//        ??????????????????domain ??????????????????null
        String domain = getDomain(applicationName, env);
//        ?????????????????????
        return importByDomain(applicationName, domain, env);
    }

    private String getDomain(String applicationName, String env) {
        QueryWrapper<AutoApplicationProperties> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_name", applicationName)
                .eq("env", env)
                .eq("deleted", 0);
        List<AutoApplicationProperties> propertiesList = this.list(queryWrapper);
        String domain = null;
        if (propertiesList.size() > 0 && !StringUtils.isEmpty(propertiesList.get(0).getSwaggerUrl())) {
            List<String> list = propertiesList.stream().map(item -> {
                Base64.Decoder decoder = Base64.getDecoder();
                return new String(decoder.decode(item.getSwaggerUrl()));
            }).collect(Collectors.toList());
            domain = list.get(0);
        }
        return domain;
    }

    private int importByDomain(String applicationName, String domain, String env) {
        if (StringUtils.isEmpty(domain)) {
//            ?????????????????????????????????????????????
            domain = "http://" + omegaService.getDomainByEvn(applicationName, env);
        }
        List<String> list = getListByDomain(domain, applicationName);
        List<SingleApiStorageVo> singleApiStorageVos = convert2VoList(list);
        singleApiStorageVos.forEach(item -> {
            item.setApplicationName(applicationName);
            item.setCreateUserCode("??????qua??????");
            item.setUpdateUserCode("??????qua??????");
            item.getApiCase().setCreateUserCode("??????qua??????");
            item.getApiCase().setUpdateUserCode("??????qua??????");
        });
//        ????????????swagger???????????????
        log.info("singleApiStorageVos ?????? ???{}???", singleApiStorageVos.size());
        saveSingleApis(singleApiStorageVos);
        return singleApiStorageVos.size();
    }

    private List<String> getListByDomain(String domain, String applicationName) {
        String resources = domain + "/swagger-resources";
        List<String> list = new ArrayList<>();
        String host = "";
        try {
            //??????header??????
            URI uri = new URI(resources);
            HttpHeaders headers = new HttpHeaders();
            host = uri.getHost();
            headers.add("host", host);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<String>(null, headers);
            if (resources.contains(".t.") || resources.contains(".kt.") || resources.contains(".q.") || resources.contains(".kq.")) {
                resources = resources.replace(host, "10.216.4.19");
            }
            ResponseEntity<String> result = restTemplate.exchange(resources, HttpMethod.GET, entity, String.class);
            JSONArray jsonArray = JSONArray.parseArray(result.getBody());
            list = jsonArray.stream().map(jsonObj -> {
                String url = JSONObject.parseObject(jsonObj.toString()).getString("url");
                try {
                    return domain + urlEncode(url);
                } catch (UnsupportedEncodingException e) {
                    log.error("urlEncode??????", e);
                    return "";
                }
            }).collect(Collectors.toList());
        } catch (Exception e) {
            resources = resources.replace("10.216.4.19", host);
            log.error("????????????,?????????[{}]", resources);
            notifyUsers(applicationName, resources);
        }
        return list;
    }

    public static String urlEncode(String url) throws UnsupportedEncodingException {
        if (url == null) {
            return null;
        }
        final String reserved_char = ";/?:@=&";
        String ret = "";
        for (int i = 0; i < url.length(); i++) {
            String cs = String.valueOf(url.charAt(i));
            if (reserved_char.contains(cs)) {
                ret += cs;
            } else {
                ret += URLEncoder.encode(cs, "utf-8");
            }
        }
        return ret.replace("+", "%20");
    }

    private void notifyUsers(String applicationName, String resources) {
        //        ????????????
        List<String> userEmailList = omegaService.getEmailList(applicationName, null);
        String content = getContent(userEmailList, applicationName, resources);
        //        ????????????
        weChatService.sendMarkdown(userEmailList, content);
    }

    public List<SingleApiStorageVo> convert2VoList(List<String> fileUrl) {
        List<SingleApiStorageVo> res = new ArrayList<>();
        for (String file : fileUrl) {
            String contents = getFileContent(file).toString().replace("<Json>", "").replace("</Json>", "").toString();
            List<SingleApiStorageVo> singleApiStorageVos = analysisSwaggerContent(contents, file);
            res.addAll(singleApiStorageVos);
        }
        return res;
    }


    private String getContent(List<String> userEmailList, String applicationName, String resources) {
        StringBuffer stringBuffer = new StringBuffer();
        Map<String, MatrixUserDetail> userDetailByEmailPre = matrixService.getUserDetailByEmailPre(userEmailList);
        userEmailList.forEach(user -> {
            if (userDetailByEmailPre.containsKey(user)) {
                stringBuffer.append(userDetailByEmailPre.get(user).getUserName()).append(" ");
            }
        });
        String format = String.format("????????????????????????swagger????????????????????????[%s](%s)????????????????????????????????????\n" +
                "                              >????????????<font color=\"info\">%s</font>\n" +
                "                              >????????????qua\n" +
                "                              >????????????%s\n" +
                "                              >????????????1.??????swagger??????????????? 2.???????????????context-path,?????????????????????[swagger??????]???????????????qua???????????????+context-path????????????http://telot-ci-web.kq.ziroom.com/ci\n" +
                "                              [?????????????????????swagger????????????](%s)", resources, resources, applicationName, stringBuffer, "http://qa.kp.ziroom.com/testCase");
        return format;
    }

    /**
     * ???????????????????????????
     *
     * @param voList ????????????
     */
    public void saveSingleApis(List<SingleApiStorageVo> voList) {
        voList.forEach(vo -> {
//            ???????????????????????????
            QueryWrapper<AutoSingleApi> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("application_name", vo.getApplicationName())
                    .eq("request_uri", vo.getRequestUri())
                    .eq("deleted", 0);
            List<AutoSingleApi> list = autoSingleApiService.list(queryWrapper);
            AutoSingleApi api;
            if (list.size() > 0) {
                api = list.get(0);
            } else {
                api = new AutoSingleApi();
                BeanUtils.copyProperties(vo, api);
                autoSingleApiService.save(api);
            }
            if (api.getId() != null) {
                autoSingleApiCaseService.deleteBatch(api.getId(), "swagger??????");
                AutoSingleApiCase apiCase = vo.getApiCase();
                apiCase.setApiId(api.getId());
                autoSingleApiCaseService.save(apiCase);
            }

        });
    }

    /**
     * ??????json???
     *
     * @param
     * @return
     */
    public List<SingleApiStorageVo> analysisSwaggerContent(String contents, String path) {
        JsonNode jsonNode;
        List<SingleApiStorageVo> retList = new ArrayList<>();
        try {
            jsonNode = SwaggerJsonUtils.readNode(contents);

            JsonNode host = jsonNode.get("host");
            AtomicReference<String> domain = new AtomicReference<>("http://" + host.asText());
            String basePath = jsonNode.get("basePath").asText();
            Iterator<Map.Entry<String, JsonNode>> paths = jsonNode.get("paths").fields();

            // ??????paths
            log.info("start read json");
            while (paths.hasNext()) {
                try {
                    //            path
                    Map.Entry<String, JsonNode> next1 = paths.next();
                    String uriKey = next1.getKey();
                    // ????????????key
                    final String[] key = {uriKey};
                    log.info("key [{}]", key[0]);
                    //            ??????requestUri
                    Optional.ofNullable(basePath).filter(basePathTmp -> basePathTmp.length() > 1).ifPresent(basePathTemp -> {
                        key[0] = basePathTemp + key[0];
                    });

                    JsonNode uriValue = next1.getValue();
                    Iterator<Map.Entry<String, JsonNode>> fields = uriValue.fields();
                    Map.Entry<String, JsonNode> methodJson = fields.next();
                    String method = methodJson.getKey();
                    JsonNode content = methodJson.getValue();
                    if (!method.equalsIgnoreCase("post") && !method.equalsIgnoreCase("get")) {
                        throw new CustomException("??????contents??????");
                    }
                    String summary = content.findValue("summary").asText();
                    JsonNode consumes = content.findValue("consumes");
                    String tag = content.findValue("tags").get(0).asText();
                    // ??????parameters?????????singleApi?????????????????????
                    StringBuffer requestParam = new StringBuffer();
                    StringBuffer header = new StringBuffer();
                    Optional.ofNullable(consumes).filter(consumesTemp -> consumesTemp.size() > 0).ifPresent(consumesTemp -> {
                        header.append("Content-Type:")
                                .append(consumesTemp.get(0).asText())
                                .append(";");
                    });
                    Object requestBodyJson = "";
                    if (content.has("parameters")) {
                        JsonNode paraArray = content.get("parameters");
                        Iterator<JsonNode> iterator1 = paraArray.iterator();
                        while (iterator1.hasNext()) {
                            JsonNode next = iterator1.next();
                            // ?????? if "in":"query"
                            String in = next.get("in") == null ? "????????????" : next.get("in").asText();
                            String paramName = next.get("name").asText();

                            if (in != null && "query".equals(in)) {
                                String format = "";
                                if (next.has("format")) {
                                    format = next.get("format").asText();
                                }
                                String type = String.valueOf(getValueByType(next.get("type").asText(), format));
                                requestParam.append(paramName + "=" + type + "&");
                            } else if (in != null && "body".equals(in)) {
                                JsonNode schema = next.get("schema");
                                if (schema != null) {
                                    if (schema.has("type") && "array".equals(schema.get("type").asText())) {
                                        JsonNode items = schema.get("items");
                                        String $ref = items.get("$ref").asText();
                                        String substring = $ref.substring($ref.lastIndexOf("/") + 1);
                                        JSONArray jsonArray = new JSONArray();
                                        jsonArray.add(getBody(path, substring));
                                        requestBodyJson = jsonArray;
                                    } else if (schema.has("$ref")) {
                                        String $ref = schema.get("$ref").asText();
                                        String substring = $ref.substring($ref.lastIndexOf("/") + 1);
                                        requestBodyJson = getBody(path, substring);
                                    } else if (schema.has("additionalProperties")) {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put(paramName, schema.get("additionalProperties").get("type").asText());
                                        requestBodyJson = jsonObject;
                                    } else {
                                        log.info("????????????schema??????,schema:", schema);
                                    }
                                    log.info("requestBodyJson[{}]", requestBodyJson);
                                }
                            } else if (in != null && "header".equals(in)) {
                                String format = "";
                                if (next.has("format")) {
                                    format = next.get("format").asText();
                                }
                                header.append(next.get("name").asText())
                                        .append(":")
                                        .append(getValueByType(next.get("type").asText(), format))
                                        .append(";");
                            } else if (in != null && "formData".equals(in)) {
                                String format = "";
                                if (next.has("format")) {
                                    format = next.get("format").asText();
                                }
                                String type = String.valueOf(getValueByType(next.get("type").asText(), format));
                                requestParam.append(paramName + "=" + type + "&");
                            } else {
                                log.error("?????????????????????in", in);
                            }

                        }
                    }
                    AutoSingleApiCase apiCase = new AutoSingleApiCase();
                    apiCase.setCaseName("[swagger??????]" + summary);
                    if (requestParam.length() == 0) {
                        apiCase.setRequestParam(requestParam.toString());
                    } else {
                        apiCase.setRequestParam(requestParam.substring(0, requestParam.length() - 1));

                    }
                    if (requestBodyJson != null && requestBodyJson.toString().length() <= 5120) {
                        apiCase.setRequestBody(requestBodyJson.toString());
                    }
                    SingleApiStorageVo vo = new SingleApiStorageVo();
                    vo.setApiCase(apiCase);
                    vo.setApiName(summary);
                    vo.setControllerName(tag);
                    vo.setRequestType(method);
                    vo.setRequestUri(key[0]);
                    vo.setHeader(header.length() <= 0 ? "" : header.substring(0, header.length() - 1));
                    retList.add(vo);
                } catch (Exception e) {
                    log.error("?????????????????????", e);
                }

            }
        } catch (IOException e) {
            log.error("?????????????????????", e);
        }
        return retList;
    }

    private JSONObject getBody(String path, String dataType) {
        // Load your OpenAPI/Swagger definition
        Swagger swagger = new SwaggerParser().read(path);

// Create an Example object for the Pet model
        Map<String, Model> definitions = swagger.getDefinitions();
        Model pet = definitions.get(dataType);
        Example example = ExampleBuilder.fromModel(dataType, pet, definitions, new HashSet<String>());
// Another way:
// Example example = ExampleBuilder.fromProperty(new RefProperty("Pet"), swagger.getDefinitions());

// Configure example serializers
        SimpleModule simpleModule = new SimpleModule().addSerializer(new JsonNodeExampleSerializer());
        Json.mapper().registerModule(simpleModule);
        Yaml.mapper().registerModule(simpleModule);

// Convert the Example object to string

// JSON example
        String jsonExample = Json.pretty(example);
        JSONObject jsonObject = JSONObject.parseObject(jsonExample);
        return jsonObject;
    }

    private Object getValueByType(String type, String format) {
        Object res = "";
        switch (type) {
            case "string":
                if ("date-time".equals(format)) {
                    res = DateUtil.dateStr("yyyy-MM-dd HH:mm:ss");
                } else {
                    res = "string";
                    break;
                }

            case "integer":
                res = 1;
                break;
            case "boolean":
                res = true;
                break;
            case "number":
                res = 1.0f;
                break;
        }

        return res;
    }

    /**
     * ???????????????????????????
     *
     * @param urlPath ??????????????????
     * @return
     */
    public static StringBuffer getFileContent(String urlPath) {
        StringBuffer contents = new StringBuffer();

        URL url;
        URLConnection conn;
        try {
            if (urlPath.contains(".t.") || urlPath.contains(".kt.") || urlPath.contains(".q.") || urlPath.contains(".kq.") || urlPath.contains(".uat.")) {
                URL urlOld = new URL(urlPath);
                if (urlPath.contains(".html")) {
                    urlPath = urlPath.replace("doc.html", "v2/api-docs")
                            .replace("swagger-ui.html", "v2/api-docs");
                }
                urlPath = urlPath.replace(urlOld.getHost(), "10.216.4.19");
                url = new URL(urlPath);
                conn = url.openConnection();
                conn.setRequestProperty("host", urlOld.getHost());
            } else {
                url = new URL(urlPath);
                conn = url.openConnection();
            }
            conn.connect();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    contents.append(line);
                }
            } else {
                log.info("Cannot connect to the " + urlPath);
                throw new CustomException("??????swagger???????????????" + urlPath);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new CustomException("??????swagger???????????????" + urlPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("??????swagger???????????????" + urlPath);
        }

        log.info("Finished getting the contents from URL...");
        return contents;
    }
}
