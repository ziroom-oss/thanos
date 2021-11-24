package com.ziroom.qa.quality.defende.provider.outinterface.client.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhujj5
 * @Title: 对omega的方法进行处理
 * @Description:
 * @date 2021/9/26 3:42 下午
 */
@Slf4j
@Service
public class OmegaService {

    /**
     * 根据应用和环境找到域名
     *
     * @param applicationName
     * @param env
     * @return
     */
    public String getDomainByEvn(String applicationName, String env) {
        // TODO 返回应用和环境对应的域名信息，可对接公司内部系统
        return "http://localhost:8080";
//        RestResultVo<OmegaPageInfo<DetailApplicationVo>> restResult;
//        AtomicReference<String> domain = new AtomicReference<>(null);
//        try {
//            restResult = omegaApiClient.allAppByProjectName(applicationName);
//            Optional.ofNullable(restResult.getData().getList()).filter(item -> item.size() > 0).ifPresent(item -> {
//                DetailApplicationVo applicationVo = item.stream().filter(applicationVoTmp -> applicationName.equals(applicationVoTmp.getApplicationName())).collect(Collectors.toList()).get(0);
//                List<String> collect = applicationVo.getEnvInfoList().stream().
//                        filter(envInfoTemp -> env.equals(envInfoTemp.getEnvLevel())).
//                        map(DetailApplicationVo.EnvInfo::getEnvDomain).collect(Collectors.toList());
//                domain.set(collect.get(0));
//            });
//        } catch (Exception e) {
//            log.error("omega返回数据有误 restResult[{}]", e);
//            throw new RuntimeException("根据应用和环境在omega查不到域名或异常！" + applicationName + "，" + env);
//        }
//        return Optional.ofNullable(domain.get()).orElseThrow(() -> new RuntimeException("根据环境去omega查询不到域名" + applicationName + "," + env));
    }

    /**
     * @param applicationName
     * @param role            测试 开发 管理员  null:查询全部
     * @return
     */
    public List<String> getEmailList(String applicationName, String role) {
        // TODO 返回应用对应的负责人信息，可对接公司内部系统
        return Arrays.asList("zhangsan,lisi,wangwu");
//        RestResultVo<OmegaPageInfo<DetailApplicationVo>> omegaPageInfoRestResult = omegaApiClient.allAppByProjectName(applicationName);
//        List<DetailApplicationVo> list = omegaPageInfoRestResult.getData().getList();
//        List<DetailApplicationVo> collect = list.stream().filter(app -> applicationName.equals(app.getApplicationName())).collect(Collectors.toList());
//        if(collect.size()>0){
//            if(!StringUtils.isEmpty(role)){
//                return omegaApiClient.userInfos(collect.get(0).getId()).getData().stream().
//                        filter(item -> role.equals(item.getUserType())).map(OmegaApplicationUser::getAdCode).collect(Collectors.toSet()).stream().collect(Collectors.toList());
//            }else{
//                return omegaApiClient.userInfos(collect.get(0).getId()).getData().stream().
//                        map(OmegaApplicationUser::getAdCode).collect(Collectors.toSet()).stream().collect(Collectors.toList());
//            }
//        }
//        throw  new CustomException("根据应用找不到用户信息");
    }

    /**
     * 根据应用名称获取所在组名
     *
     * @param appName
     * @return
     */
    public String getGroupNameByAppName(String appName) {
        // TODO 返回应用对应的负责人名称，可对接公司内部系统
        return "zhangsan";
//        if (StringUtils.isBlank(appName)) {
//            return null;
//        }
//        JSONObject result = omegaApiClient.allAppByApplicationNameLK(appName);
//        Integer total = Integer.parseInt(result.getJSONObject("data").get("total").toString());
//        // total小于1，说明没有查到
//        if (total < 1) {
//            return null;
//            // total==1，说明只查到一条
//        } else if (total == 1) {
//            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");
//            Object groupObj = jsonArray.getJSONObject(0).get("groupName");
//            return this.getGroupNameByObject(groupObj);
//            // total>1，说明查到多条，需要遍历
//        } else {
//            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");
//            for (int i = 0; i < total; i++) {
//                Object appObj = jsonArray.getJSONObject(i).get("applicationName");
//                String omgaAppName = Objects.isNull(appObj) ? "" : appObj.toString();
//                if (appName.equals(omgaAppName)) {
//                    Object groupObj = jsonArray.getJSONObject(0).get("groupName");
//                    return this.getGroupNameByObject(groupObj);
//                }
//            }
//        }
//        return null;
    }

//    /**
//     * 获取最后以及组名
//     *
//     * @param groupObj
//     * @return
//     */
//    public String getGroupNameByObject(Object groupObj) {
//        String groupName = Objects.isNull(groupObj) ? "" : groupObj.toString();
//        if (StringUtils.isNotBlank(groupName)) {
//            String[] groupTree = groupName.split("\\.");
//            if (groupTree.length == 0) {
//                return groupName;
//            } else {
//                return groupTree[groupTree.length - 1];
//            }
//        }
//        return null;
//    }

    public String getApplicationNameByDomain(String domain) {
        // TODO 根据域名获取应用名称，可对接公司内部系统
        return "测试项目";
//        domain = domain.replace("http://", "").replace("https://", "");
//        RestResultVo<OmegaPageInfo<DetailApplicationVo>> omegaPageInfoRestResult = omegaApiClient.allAppByDomainNameLK(domain.substring(0, domain.indexOf(".")));
//        List<DetailApplicationVo> list = null;
//        try {
//            list = omegaPageInfoRestResult.getData().getList();
//        } catch (Exception e) {
//            log.error("根据域名名找到应用，domain[{}],e", domain, e);
//            return null;
//        }
//        if (list.size() <= 0) {
//            throw new CustomException("找不到应用，请去omega中修改【项目域名】");
//        }
//        String finalDomain = domain;
//        List<DetailApplicationVo> collect = list.stream().filter(item -> {
//                    return item.getEnvInfoList().stream().filter(
//                            envInfo -> finalDomain.equals(envInfo.getEnvDomain())).collect(Collectors.toList()).size() > 0;
//                }
//        ).collect(Collectors.toList());
//        if (collect != null && collect.size() > 0) {
//            return collect.get(0).getApplicationName();
//        } else {
////            没有equals瞎给一个
//            return list.get(0).getApplicationName();
//        }
    }

}
