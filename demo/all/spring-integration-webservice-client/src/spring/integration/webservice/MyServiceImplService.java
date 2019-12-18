/**
 * MyServiceImplService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package spring.integration.webservice;

public interface MyServiceImplService extends javax.xml.rpc.Service {
    public java.lang.String getMyServiceImplPortAddress();

    public spring.integration.webservice.MyService getMyServiceImplPort() throws javax.xml.rpc.ServiceException;

    public spring.integration.webservice.MyService getMyServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
