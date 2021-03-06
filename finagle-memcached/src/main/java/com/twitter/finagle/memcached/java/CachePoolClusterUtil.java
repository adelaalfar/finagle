package com.twitter.finagle.memcached.java;

import com.twitter.common.zookeeper.ZooKeeperClient;
import com.twitter.finagle.memcached.CacheNode;
import com.twitter.finagle.memcached.CachePoolCluster$;
import com.twitter.finagle.stats.NullStatsReceiver;
import com.twitter.finagle.stats.StatsReceiver;

import java.util.Collections;
import java.util.Set;
import scala.collection.JavaConversions;

/**
 * A Java-friendly CachePoolCluster.
 */
public class CachePoolClusterUtil {

  /**
   * Cache pool based on a static list.
   *
   * @param cacheNodeSet static set of cache nodes to construct the cluster
   * @return a scala CachePoolCluster
   */
  public static com.twitter.finagle.memcached.CachePoolCluster newStaticCluster(
      Set<CacheNode> cacheNodeSet) {
    scala.collection.immutable.Set<CacheNode> staticSet =
        JavaConversions.asScalaSet(cacheNodeSet).toSet();
    return CachePoolCluster$.MODULE$.newStaticCluster(staticSet);
  }

  /**
   * Zookeeper based cache pool cluster.
   * The cluster will monitor the underlying serverset changes and report the detected underlying
   * pool size. The cluster snapshot will be updated during cache-team's managed operation, and
   * the Future spool will be updated with corresponding changes. In case of zookeeper failure,
   * the backup pool will be used to fill the cluster after a certain timeout.
   *
   * @param zkPath the zookeeper path representing the cache pool
   * @param zkClient zookeeper client to read zookeeper
   * @param backupPool the backup static pool to use in case of ZK failure. Backup pool cannot be
   *                   null and empty backup pool means the same as no backup pool.
   * @param statsReceiver the destination to report the stats to
   * @return a scala CachePoolCluster
   */
  public static com.twitter.finagle.memcached.CachePoolCluster newZkCluster(
      String zkPath,
      ZooKeeperClient zkClient,
      Set<CacheNode> backupPool,
      StatsReceiver statsReceiver) {
    scala.collection.immutable.Set<CacheNode> backupSet =
        JavaConversions.asScalaSet(backupPool).toSet();
    return CachePoolCluster$.MODULE$.newZkCluster(
        zkPath, zkClient, scala.Option.apply(backupSet), statsReceiver);
  }

  /**
   * Equivalent to calling {@link #newZkCluster(String, ZooKeeperClient, Set, StatsReceiver)}
   * with a {@link NullStatsReceiver}.
   *
   * @param zkPath the zookeeper path representing the cache pool
   * @param zkClient zookeeper client to read zookeeper
   * @param backupPool the backup static pool to use in case of ZK failure. Backup pool cannot be
   *                   null and empty backup pool means the same as no backup pool.
   * @return a scala CachePoolCluster
   */
  public static com.twitter.finagle.memcached.CachePoolCluster newZkCluster(
      String zkPath,
      ZooKeeperClient zkClient,
      Set<CacheNode> backupPool) {
    return newZkCluster(zkPath, zkClient, backupPool, new NullStatsReceiver());
  }

  /**
   * Equivalent to calling {@link #newZkCluster(String, ZooKeeperClient, Set, StatsReceiver)}
   * with no backup pool and a {@link NullStatsReceiver}.
   * Using no backup pool means the cluster won't be ready until ZK membership is available.
   *
   * @param zkPath the zookeeper path representing the cache pool
   * @param zkClient zookeeper client to read zookeeper
   * @return a scala CachePoolCluster
   */
  public static com.twitter.finagle.memcached.CachePoolCluster newZkCluster(
      String zkPath,
      ZooKeeperClient zkClient) {
    return newZkCluster(zkPath, zkClient, Collections.<CacheNode>emptySet());
  }
}
