package com.lxkj.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * list转换为树形结构工具类
 *
 * @author <a href="mailto:jiaoqx@tydic.com">jiaoqx</a>
 * @version 1.0
 */
public final class Trees {

  private Trees() {
    throw new AssertionError(Strings.INSTANTIATION_PROHIBITED);
  }

  /**
   * 自动寻找根节点，适用于列表数据量小的场景
   *
   * @param data 待转换的数据
   * @param keyId id对应的key名称
   * @param keyPid 父id对应的key名称
   */
  public static List<Map<String, Object>> build(List<Map<String, Object>> data, String keyId, String keyPid) {
    List<Map<String, Object>> rootNodes = filterRootNode(data, keyId, keyPid);
    rootNodes.forEach((node) -> {
      buildTree(node, data, keyPid, keyId);
    });
    return rootNodes;
  }

  /**
   * 指定根节点key名称，适用于列表数据量大的场景
   *
   * @param data 待转换的数据
   * @param keyId id对应的key名称
   * @param keyPid 父id对应的key名称
   * @param rootPid 根节点父id对应的key名称
   */
  public static List<Map<String, Object>> build(List<Map<String, Object>> data, String keyId, String keyPid, String rootPid) {
    List<Map<String, Object>> rootNodes = data.stream()
        .filter((node) -> rootPid == null ? (node.get(keyPid) == null) : rootPid.equals(Strings.of(node.get(keyPid))))
        .collect(Collectors.toList());
    rootNodes.forEach((node) -> {
      buildTree(node, data, keyPid, keyId);
    });
    return rootNodes;
  }

  private static List<Map<String, Object>> filterRootNode(List<Map<String, Object>> data, String
      keyId, String keyPid) {
    List<Map<String, Object>> rootNodes = new ArrayList<>();
    data.forEach((node) -> {
      List<Map<String, Object>> children = data.stream()
          .filter((comparedNode) -> Strings.of(node.get(keyPid)).equals(Strings.of(comparedNode.get(keyId))))
          .collect(Collectors.toList());
      if (children.size() == 0) {
        rootNodes.add(node);
      }
    });
    return rootNodes;
  }

  private static void buildTree(Map<String, Object> node, List<Map<String, Object>> data, String keyPid, String keyId) {
    List<Map<String, Object>> children = data.stream()
        .filter((comparedNode) -> Strings.of(node.get(keyId)).equals(Strings.of(comparedNode.get(keyPid))))
        .collect(Collectors.toList());
    if (children != null) {
      node.put("children", children);
      children.forEach((child) -> {
        buildTree(child, data, keyPid, keyId);
      });
    }
  }

}
