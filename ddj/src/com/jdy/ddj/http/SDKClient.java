/**
 * SDKClient.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.jdy.ddj.http;

public interface SDKClient extends java.rmi.Remote {
    public String getVersion() throws java.rmi.RemoteException;
    public com.jdy.ddj.http.StatusReport[] getReport(String arg0, String arg1) throws java.rmi.RemoteException;
    public int cancelMOForward(String arg0, String arg1) throws java.rmi.RemoteException;
    public int chargeUp(String arg0, String arg1, String arg2, String arg3) throws java.rmi.RemoteException;
    public double getBalance(String arg0, String arg1) throws java.rmi.RemoteException;
    public double getEachFee(String arg0, String arg1) throws java.rmi.RemoteException;
    public com.jdy.ddj.http.Mo[] getMO(String arg0, String arg1) throws java.rmi.RemoteException;
    public int logout(String arg0, String arg1) throws java.rmi.RemoteException;
    public int registDetailInfo(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9) throws java.rmi.RemoteException;
    public int registEx(String arg0, String arg1, String arg2) throws java.rmi.RemoteException;
    public int sendSMS(String arg0, String arg1, String arg2, String[] arg3, String arg4, String arg5, String arg6, int arg7, long arg8) throws java.rmi.RemoteException;
    public String sendVoice(String arg0, String arg1, String arg2, String[] arg3, String arg4, String arg5, String arg6, int arg7, long arg8) throws java.rmi.RemoteException;
    public int serialPwdUpd(String arg0, String arg1, String arg2, String arg3) throws java.rmi.RemoteException;
    public int setMOForward(String arg0, String arg1, String arg2) throws java.rmi.RemoteException;
    public int setMOForwardEx(String arg0, String arg1, String[] arg2) throws java.rmi.RemoteException;
}
