package com.isport.blelibrary.managers;

/**
 * F18数据同步状态
 * Created by Admin
 * Date 2022/4/28
 */
public enum F18SyncStatus {

    /**
     * SYNC_NONE 正常默认状态
     * SYNC_ING 同步运动数据中
     * SYNC_COMPLETE 运动数同步完成
     * SYNC_DIAL_ING 同步表盘中
     * SYNC_DIAL_FAIL 表盘同步失败
     * SYNC_DIAL_COMPLETE 同步表盘完成
     */

    SYNC_NONE,

    SYNC_ING,
    SYNC_COMPLETE,

    SYNC_DIAL_ING,
    SYNC_DIAL_FAIL,
    SYNC_DIAL_COMPLETE
    ;


    private F18SyncStatus() {
    }
}
