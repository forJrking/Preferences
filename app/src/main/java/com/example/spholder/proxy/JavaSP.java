package com.example.spholder.proxy;

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/21 15:03
 * @version: 1.0.0
 */
@SpConfig
interface JavaSP {

    String getCoin();

    void setCoin(String coin);

    boolean isFirstShare(boolean isFirst);

    void setFirstShare(boolean isFirst);

    void CLEAR();

}
