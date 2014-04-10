mtaaas
======

### Overview

A Multi-Tenant Authorization as a Service (MTAaaS) prototype using Sun XACML implementation.

Functional modules:
```
PDPServer   --  A multi-threading policy decision point (PDP) can be configured to use various policy sets.
PEPClient   --  A distributed policy enforcement point (PEP) can send requests to PDP Server for access decisions.
```
For more practical deployment, the PEPClient can be replaced by the following.
```
CloudSvcPEP --  A PEP runs with the cloud service receiving requests from the client and enforce access according to the response from the PDP.
Client      --  A cloud service client requesting access to cloud resource.
``` 

> **Note:** If error message "Exception: null" constantly prompted when startpdp.sh is run, check if there are multiple servers running on the same port. 
