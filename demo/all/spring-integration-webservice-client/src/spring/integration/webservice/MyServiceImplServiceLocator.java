/**
 * MyServiceImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package spring.integration.webservice;

public class MyServiceImplServiceLocator extends org.apache.axis.client.Service implements spring.integration.webservice.MyServiceImplService {

    public MyServiceImplServiceLocator() {
    }


    public MyServiceImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MyServiceImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MyServiceImplPort
    private java.lang.String MyServiceImplPort_address = "http://localhost:8081/show";

    public java.lang.String getMyServiceImplPortAddress() {
        return MyServiceImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MyServiceImplPortWSDDServiceName = "MyServiceImplPort";

    public java.lang.String getMyServiceImplPortWSDDServiceName() {
        return MyServiceImplPortWSDDServiceName;
    }

    public void setMyServiceImplPortWSDDServiceName(java.lang.String name) {
        MyServiceImplPortWSDDServiceName = name;
    }

    public spring.integration.webservice.MyService getMyServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MyServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMyServiceImplPort(endpoint);
    }

    public spring.integration.webservice.MyService getMyServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            spring.integration.webservice.MyServiceImplPortBindingStub _stub = new spring.integration.webservice.MyServiceImplPortBindingStub(portAddress, this);
            _stub.setPortName(getMyServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMyServiceImplPortEndpointAddress(java.lang.String address) {
        MyServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (spring.integration.webservice.MyService.class.isAssignableFrom(serviceEndpointInterface)) {
                spring.integration.webservice.MyServiceImplPortBindingStub _stub = new spring.integration.webservice.MyServiceImplPortBindingStub(new java.net.URL(MyServiceImplPort_address), this);
                _stub.setPortName(getMyServiceImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("MyServiceImplPort".equals(inputPortName)) {
            return getMyServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.integration.spring/", "MyServiceImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.integration.spring/", "MyServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MyServiceImplPort".equals(portName)) {
            setMyServiceImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
