package com.ziroom.qa.quality.defende.provider.util.xmind.pojo;
import lombok.Data;

import java.util.List;


@Data
public class Attached {

    private String id;
    /**
     * 主要内容
     */
    private String title;

    /**
     * 孩子节点
     */
    private Children children;
    /**
     * 父亲节点
     */
    private Attached parent;
    /**
     * 树根
     */
    private Attached rootTopic;
    /**
     * 树结构的孩子节点
     */
    private List<Attached> attachedChildren;
    /**
     * 是否为根节点
     */
    private boolean isRoot = false;
    /**
     * 是否为叶子节点
     */
    private boolean isLeaf = false;
    private Notes notes;
    private List<Comments> comments;

}
